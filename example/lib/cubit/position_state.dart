part of 'position_cubit.dart';

@immutable
abstract class PositionState {
  const PositionState({
    this.isTracking = false,
    this.positions = const [],
  });
  final bool isTracking;
  final List<Position> positions;
}

@immutable
class PositionInitial extends PositionState {}

class PositionArrived extends PositionState with EquatableMixin {
  final Position newPosition;

  const PositionArrived(
    this.newPosition, {
    required bool isTracking,
    required List<Position> positions,
  }) : super(
          isTracking: isTracking,
          positions: positions,
        );

  @override
  List<Object?> get props => [newPosition, isTracking, positions];
}

class PositionUpdateState extends PositionState with EquatableMixin {
  const PositionUpdateState({
    required bool isTracking,
    required List<Position> positions,
  }) : super(isTracking: isTracking, positions: positions);

  @override
  List<Object?> get props => [isTracking, positions];
}
