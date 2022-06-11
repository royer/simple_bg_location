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
  List<Position> positions = [];

  PositionCubit() : super(PositionInitial()) {
    _positionSub = SimpleBgLocation.getPositionStream(_positionErrorHandle)
        .listen(_onPosition);

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
    _positionSub.cancel();
    dev.log('cubit closed');
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
    dev.log('Listen position error: $err');
    //TODO set error state
  }

  void _onPosition(Position position) {
    positions.add(position);
    emit(PositionArrived(position,
        isTracking: isTracking, positions: positions));
  }
}
