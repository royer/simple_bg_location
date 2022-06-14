part of 'settings_cubit.dart';

enum CustomAccuracy { best, good, balance, lowest }

@immutable
abstract class SettingsState with EquatableMixin {
  final CustomAccuracy accuracy;
  const SettingsState({this.accuracy = CustomAccuracy.best});
}

@immutable
class SettingsInitial extends SettingsState with EquatableMixin {
  @override
  List<Object?> get props => [accuracy];
}

@immutable
class AccuracyChanged extends SettingsState with EquatableMixin {
  
  const AccuracyChanged({required CustomAccuracy accuracy})
      : super(accuracy: accuracy);
  @override
  List<Object?> get props => [accuracy];
}
