import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

part 'request_ui_flow_state.dart';

class RequestUiFlowCubit extends Cubit<RequestUiFlowState> {
  RequestUiFlowCubit() : super(const RequestUiFlowInitial());

  void startRequestPermission({bool forBackground = false}) {
    emit(RequestUiFlowState(
      step: RequestUiFlowStep.requestPermission,
      forBackground: forBackground,
    ));
  }

  void startShowBackgroundRationale({bool forBackground = false}) {
    emit(state.copyWith(
        step: RequestUiFlowStep.showBackgroundRationale,
        forBackground: forBackground));
  }

  void startShowPowerSavedModeWarning({bool forBackground = false}) {
    emit(state.copyWith(
        step: RequestUiFlowStep.showPowerSavedModeWarning,
        forBackground: forBackground));
  }

  void finish() {
    emit(const RequestUiFlowInitial());
  }
}
