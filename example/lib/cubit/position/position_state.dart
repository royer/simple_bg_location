part of 'position_cubit.dart';

@immutable
abstract class PositionState with EquatableMixin {
  const PositionState({
    required this.isTracking,
    required this.positions,
    required this.odometer,
  });
  final bool isTracking;
  final List<Position> positions;
  final double odometer;

  @override
  List<Object?> get props => [isTracking, positions, odometer];
}

@immutable
class PositionInitial extends PositionState with EquatableMixin {
  const PositionInitial()
      : super(isTracking: false, positions: const [], odometer: 0.0);
}

class PositionArrived extends PositionState with EquatableMixin {
  final Position newPosition;

  const PositionArrived(
    this.newPosition, {
    required bool isTracking,
    required List<Position> positions,
    required double odometer,
  }) : super(
          isTracking: isTracking,
          positions: positions,
          odometer: odometer,
        );

  @override
  List<Object?> get props => [newPosition, ...super.props];
}

class PositionUpdateState extends PositionState with EquatableMixin {
  const PositionUpdateState({
    required bool isTracking,
    required List<Position> positions,
    required double odometer,
  }) : super(isTracking: isTracking, positions: positions, odometer: odometer);
}

class PositionCurrentPositionResult extends PositionState with EquatableMixin {
  final Position currentResult;

  const PositionCurrentPositionResult(
    this.currentResult, {
    required bool isTracking,
    required List<Position> positions,
    required double odometer,
  }) : super(isTracking: isTracking, positions: positions, odometer: odometer);
  @override
  List<Object?> get props => [currentResult, ...super.props];
}

class PositionStateError extends PositionState with EquatableMixin {
  final PositionError error;

  const PositionStateError(
    this.error, {
    required bool isTracking,
    required List<Position> positions,
    required double odometer,
  }) : super(isTracking: isTracking, positions: positions, odometer: odometer);

  @override
  List<Object?> get props => [error, ... super.props ];
}
