part of 'settings_cubit.dart';

enum CustomAccuracy { best, good, balance, lowest }

@immutable
abstract class SettingsState with EquatableMixin {
  final CustomAccuracy accuracy;
  final bool forceLocationManager;
  const SettingsState({
    this.accuracy = CustomAccuracy.best,
    this.forceLocationManager = false,
  });
}

@immutable
class SettingsInitial extends SettingsState with EquatableMixin {
  @override
  List<Object?> get props => [accuracy, forceLocationManager];
}

@immutable
class AccuracyChanged extends SettingsState with EquatableMixin {
  const AccuracyChanged({
    required CustomAccuracy accuracy,
    required bool forceLocationManager,
  }) : super(
          accuracy: accuracy,
          forceLocationManager: forceLocationManager,
        );
  @override
  List<Object?> get props => [accuracy, forceLocationManager];
}

class ForceLocationManagerChanged extends SettingsState with EquatableMixin {
  const ForceLocationManagerChanged({
    required CustomAccuracy accuracy,
    required bool forceLocationManager,
  }) : super(accuracy: accuracy, forceLocationManager: forceLocationManager);

  @override
  List<Object?> get props => [accuracy, forceLocationManager];
}
