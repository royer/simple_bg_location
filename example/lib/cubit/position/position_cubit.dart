import 'dart:async';
import 'dart:developer' as dev;

import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

part 'position_state.dart';

class PositionCubit extends Cubit<PositionState> {
  // If use SimpleBgLocation.getPositionStream, define your owner stream
  // subscription
  // late final StreamSubscription<Position> _positionSub;

  PositionCubit() : super(const PositionInitial()) {
    // _positionSub = SimpleBgLocation.getPositionStream(_positionErrorHandle)
    //     .listen(_onPosition);

    SimpleBgLocation.onPosition(_onPosition, _positionErrorHandle);

    SimpleBgLocation.ready().then((sbglState) {
      sbglState.isTracking;
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
      emit(const PositionUpdateState(
          isTracking: true, positions: [], odometer: 0.0));
    } on LocationServiceDisabledException catch (e) {
      emit(PositionStateError(
        PositionError(PositionError.locationServiceDisabled,
            e.message ?? 'Location Services disabled'),
        isTracking: false,
        positions: [],
        odometer: 0.0,
      ));
    } on PlatformException catch (e) {
      emit(PositionStateError(
        PositionError.fromPlatformException(e),
        isTracking: false,
        positions: [],
        odometer: 0.0,
      ));
    }
  }

  Future<void> stopPositionUpdate() async {
    await SimpleBgLocation.stopPositionUpdate();
    emit(PositionUpdateState(
        isTracking: false,
        positions: state.positions,
        odometer: state.odometer));
  }

  Future<void> getCurrentPosition() async {
    try {
      final position = await SimpleBgLocation.getCurrentPosition();
      if (position != null) {
        emit(PositionCurrentPositionResult(
          position,
          isTracking: state.isTracking,
          positions: state.positions,
          odometer: state.odometer,
        ));
      }
    } catch (e) {
      dev.log('getCurrentPosition() something wrong. $e');
    }
  }

  void _positionErrorHandle(PositionError err) {
    dev.log('_onPositionError, errorCode: $err');

    emit(PositionStateError(err,
        isTracking: false,
        positions: state.positions,
        odometer: state.odometer));
  }

  void _onPosition(Position position) {
    var positions = state.positions.toList();
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
    emit(PositionArrived(position,
        isTracking: state.isTracking,
        positions: positions,
        odometer: odometer));
  }
}
