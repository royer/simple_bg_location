import 'dart:async';
import 'dart:developer' as dev;

import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

part 'position_state.dart';

class PositionCubit extends Cubit<PositionState> {
  // If use SimpleBgLocation.getPositionStream, define your owner stream
  // subscription
  // late final StreamSubscription<Position> _positionSub;

  bool isTracking = false;
  List<Position> positions = [];

  PositionCubit() : super(PositionInitial()) {
    // _positionSub = SimpleBgLocation.getPositionStream(_positionErrorHandle)
    //     .listen(_onPosition);

    SimpleBgLocation.onPosition(_onPosition, _positionErrorHandle);

    SimpleBgLocation.ready().then((sbglState) {
      isTracking = sbglState.isTracking;
      if (isTracking) {
        positions.addAll(sbglState.positions ?? []);
        emit(PositionUpdateState(
          isTracking: isTracking,
          positions: positions,
        ));
      }
    });
  }

  @override
  Future<void> close() {
    // _positionSub.cancel();
    SimpleBgLocation.removeListener(_onPosition);
    return super.close();
  }

  Future<void> requestPositionUpdate(RequestSettings requestSettings) async {
    await SimpleBgLocation.requestPositionUpdate(requestSettings);
    isTracking = true;
    positions.clear();
    emit(PositionUpdateState(
        isTracking: isTracking, positions: state.positions));
  }

  Future<void> stopPositionUpdate() async {
    await SimpleBgLocation.stopPositionUpdate();
    isTracking = false;
    emit(PositionUpdateState(isTracking: false, positions: positions));
  }

  void _positionErrorHandle(PositionError err) {
    dev.log('_onPositionError, errorCode: $err');
    isTracking = false;
    emit(PositionUpdateState(isTracking: isTracking, positions: positions));
  }

  void _onPosition(Position position) {
    positions.add(position);
    emit(PositionArrived(position,
        isTracking: isTracking, positions: positions));
  }
}
