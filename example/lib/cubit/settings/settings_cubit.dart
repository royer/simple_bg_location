import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

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
