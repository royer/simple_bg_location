part of 'request_ui_flow_cubit.dart';

enum RequestUiFlowStep {
  none,
  requestPermission,
  showBackgroundRationale,
  showPowerSavedModeWarning
}

@immutable
class RequestUiFlowState with EquatableMixin {
  final RequestUiFlowStep step;
  final bool forBackground;

  const RequestUiFlowState({
    this.step = RequestUiFlowStep.none,
    this.forBackground = false,
  });

  @override
  List<Object?> get props => [step, forBackground];

  RequestUiFlowState copyWith({
    RequestUiFlowStep? step,
    bool? forBackground,
  }) {
    return RequestUiFlowState(
      step: step ?? this.step,
      forBackground: forBackground ?? this.forBackground,
    );
  }
}

class RequestUiFlowInitial extends RequestUiFlowState {
  const RequestUiFlowInitial() : super();
  @override
  List<Object?> get props => [step, forBackground];
}
