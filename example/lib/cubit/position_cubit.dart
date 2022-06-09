import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'dart:developer' as dev;
import 'package:simple_bg_location/simple_bg_location.dart';

part 'position_state.dart';

class PositionCubit extends Cubit<PositionState> {
  late final StreamSubscription<Position> _positionSub;
  bool isTracking = false;

  PositionCubit() : super(PositionInitial()) {
    _positionSub = SimpleBgLocation.getPositionStream(_positionErrorHandle)
        .listen(_onPosition);

    SimpleBgLocation.ready().then((state) {
      isTracking = state.isTracking;
      dev.log('state: $state');
      if (isTracking) {
        emit(PositionUpdateState(isTracking));
      }
    });
  }

  @override
  Future<void> close() {
    _positionSub.cancel();
    dev.log('cubit closed');
    return super.close();
  }

  Future<void> requestPositionUpdate(RequestSettings requestSettings) async {
    await SimpleBgLocation.requestPositionUpdate(requestSettings);
    isTracking = true;
    emit(PositionUpdateState(isTracking));
  }

  Future<void> stopPositionUpdate() async {
    await SimpleBgLocation.stopPositionUpdate();
    isTracking = false;
    emit(const PositionUpdateState(false));
  }

  void _positionErrorHandle(PositionError err) {
    dev.log('Listen position error: $err');
    //TODO set error state
  }

  void _onPosition(Position position) {
    dev.log('onPosition $position');
    emit(PositionArrived(position, isTracking: isTracking));
  }
}
