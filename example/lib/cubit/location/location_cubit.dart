import 'dart:async';
import 'dart:developer' as dev;

import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

part 'location_state.dart';

class LocationCubit extends Cubit<LocationState> {
  // If use SimpleBgLocation.getPositionStream, define your owner stream
  // subscription
  // late final StreamSubscription<Position> _positionSub;



  LocationCubit() : super(const PositionInitial()) {
    // _positionSub = SimpleBgLocation.getPositionStream(_positionErrorHandle)
    //     .listen(_onPosition);

    SimpleBgLocation.onPosition(_onPosition, _positionErrorHandle);

    SimpleBgLocation.ready().then((sbglState) async {
      List<Position> positions = [];
      positions.addAll(sbglState.positions ?? []);
      double odometer = 0;
      if (positions.length >= 2) {
        for (int i = 1; i < positions.length; i++) {
          final d = SimpleBgLocation.distance(
            positions[i - 1].latitude,
            positions[i - 1].longitude,
            positions[i].latitude,
            positions[i].longitude,
          );
          odometer += d;
        }
      }
      emit(PositionUpdateState(
        isTracking: sbglState.isTracking,
        positions: positions,
        odometer: odometer,
        permission: await SimpleBgLocation.checkPermission(),
      ));
    });
  }

  @override
  Future<void> close() {
    // _positionSub.cancel();
    SimpleBgLocation.removeListener(_onPosition);
    return super.close();
  }

  Future<void> requestPositionUpdate(RequestSettings requestSettings) async {
    try {
      await SimpleBgLocation.requestPositionUpdate(requestSettings);

      emit(PositionUpdateState(
          isTracking: true, positions: const [], odometer: 0.0,
          permission: await SimpleBgLocation.checkPermission()));
    } on LocationServiceDisabledException catch (e) {
      emit(PositionStateError(
        oldState: state,
        error: PositionError(PositionError.locationServiceDisabled,
            e.message ?? 'Location Services disabled'),

      ));
    } on PlatformException catch (e) {
      emit(PositionStateError(
        oldState: state,
        error:PositionError.fromPlatformException(e),

      ));
    }
  }

  Future<void> stopPositionUpdate() async {
    await SimpleBgLocation.stopPositionUpdate();
    emit(PositionUpdateState(
        isTracking: false,
        positions: state.positions,
        odometer: state.odometer,
        permission: state.permission));
  }

  Future<void> getCurrentPosition() async {
    try {
      final position = await SimpleBgLocation.getCurrentPosition();
      if (position != null) {
        emit(PositionCurrentPositionResult(
          oldState: state,
          currentResult: position,

        ));
      }
    } catch (e) {
      dev.log('getCurrentPosition() something wrong. $e');
    }
  }

  void _positionErrorHandle(PositionError err) {
    dev.log('_onPositionError, errorCode: $err');

    emit(PositionStateError(
      error: err,
      oldState: state,
));
  }

  void _onPosition(Position position) async{
    List<Position> positions = state.positions.toList();
    positions.add(position);
    double odometer = state.odometer;
    if (positions.length >= 2) {
      odometer += SimpleBgLocation.distance(
        positions[positions.length - 2].latitude,
        positions[positions.length - 2].longitude,
        positions[positions.length - 1].latitude,
        positions[positions.length - 1].longitude,
      );
    }
    // dev.log(
    //     'onPosition received. (timestamp: ${position.timestamp}, [${position.latitude}, ${position.longitude}], accuracy: ${position.accuracy})');
    emit(PositionArrived(
      oldState: state,
      newPosition: position,
      positions: positions,
      odometer: odometer,
));
  }
}
