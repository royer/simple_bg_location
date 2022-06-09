part of 'position_cubit.dart';

@immutable
abstract class PositionState {
  const PositionState([this.isTracking = false]);
  final isTracking ;
}

@immutable
class PositionInitial extends PositionState {}

class PositionArrived extends PositionState with EquatableMixin {
  final Position position;

  const PositionArrived(this.position, {required bool isTracking}): super(isTracking);

  @override
  List<Object?> get props => [position, isTracking];
}

class PositionUpdateState extends PositionState with EquatableMixin {

  const PositionUpdateState(bool isTracking): super(isTracking);

  @override
  List<Object?> get props => [isTracking];
}
