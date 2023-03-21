part of 'location_cubit.dart';

@immutable
abstract class LocationState with EquatableMixin {
  const LocationState({
    required this.isTracking,
    required this.positions,
    required this.odometer,
    required this.permission,
  });
  final bool isTracking;
  final List<Position> positions;
  final double odometer;
  final LocationPermission permission;

  @override
  List<Object?> get props => [isTracking, positions, odometer, permission];
}

@immutable
class PositionInitial extends LocationState with EquatableMixin {
  const PositionInitial()
      : super(
            isTracking: false,
            positions: const [],
            odometer: 0.0,
            permission: LocationPermission.denied);
}

@immutable
class PositionUpdateState extends LocationState with EquatableMixin {
  const PositionUpdateState({
    required bool isTracking,
    required List<Position> positions,
    required double odometer,
    required LocationPermission permission,
  }) : super(
            isTracking: isTracking,
            positions: positions,
            odometer: odometer,
            permission: permission);
}

@immutable
class PositionArrived extends LocationState with EquatableMixin {
  final Position newPosition;

  PositionArrived({
    required this.newPosition,
    required List<Position> positions,
    required double odometer,
    required LocationState oldState,
  }) : super(
            isTracking: oldState.isTracking,
            positions: positions,
            odometer: odometer,
            permission: oldState.permission);

  @override
  List<Object?> get props => [newPosition, ...super.props];
}

@immutable
class PositionCurrentPositionResult extends LocationState with EquatableMixin {
  final Position currentResult;

  PositionCurrentPositionResult({
    required LocationState oldState,
    required this.currentResult,
  }) : super(
            isTracking: oldState.isTracking,
            positions: oldState.positions,
            odometer: oldState.odometer,
            permission: oldState.permission);

  @override
  List<Object?> get props => [currentResult, ...super.props];
}

class PositionStateError extends LocationState with EquatableMixin {
  final PositionError error;

  PositionStateError({
    required LocationState oldState,
    required this.error,
  }) : super(
          isTracking: oldState.isTracking,
          positions: oldState.positions,
          odometer: oldState.odometer,
          permission: oldState.permission,
        );

  @override
  List<Object?> get props => [error, ...super.props];
}
