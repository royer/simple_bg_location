import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

part 'settings_state.dart';

class SettingsCubit extends Cubit<SettingsState> {
  SettingsCubit() : super(SettingsInitial());

  void setCustomerAccuracy(CustomAccuracy accuracy) {
    emit(AccuracyChanged(
      accuracy: accuracy,
      forceLocationManager: state.forceLocationManager,
    ));
  }

  void toggleForceLocationManager() {
    emit(
      ForceLocationManagerChanged(
        accuracy: state.accuracy,
        forceLocationManager: !state.forceLocationManager,
      ),
    );
  }
}
