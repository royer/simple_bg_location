import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location/simple_bg_device_info.dart';
import '../../cubit/location/location_cubit.dart';
import '../../cubit/request_ui_flow/request_ui_flow_cubit.dart';
import '../../cubit/settings/settings_cubit.dart';

class MyBottomBar extends StatelessWidget {
  const MyBottomBar({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => RequestUiFlowCubit(),
      child: BottomAppBar(
        child: BlocListener<RequestUiFlowCubit, RequestUiFlowState>(
          listener: (context, state) {
            final forBackground = state.forBackground;
            switch (state.step) {
              case RequestUiFlowStep.requestPermission:
                _requestPermission(context, state.forBackground).then((result) {
                  if (result) {
                    _requestPositionUpdate(context,
                        forBackground: forBackground);
                  }
                });
                break;
              case RequestUiFlowStep.showBackgroundRationale:
                _showBackgroundRationaleDialog(context).then((result) {
                  if (result) {
                    context
                        .read<RequestUiFlowCubit>()
                        .startRequestPermission(forBackground: true);
                  }
                });
                break;
              case RequestUiFlowStep.showPowerSavedModeWarning:
                _showPowerSavedModeWarningDialog(context).then((result) {
                  final accuracy = context.read<SettingsCubit>().state.accuracy;
                  final forceLocationManager =
                      context.read<SettingsCubit>().state.forceLocationManager;
                  final locationCubit = context.read<LocationCubit>();
                  final uiFlowCubit = context.read<RequestUiFlowCubit>();
                  _callRequestPositionUpdate(accuracy, forceLocationManager,
                      locationCubit, uiFlowCubit);
                });

                break;
              case RequestUiFlowStep.none:
                break;
            }
          },
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 5.0),
            child: Row(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                IconButton(
                  onPressed: () =>
                      context.read<LocationCubit>().getCurrentPosition(),
                  icon: const Icon(Icons.gps_fixed),
                ),
                BlocBuilder<LocationCubit, LocationState>(
                  buildWhen: (previous, current) =>
                      previous.odometer != current.odometer,
                  builder: (context, state) {
                    final distance = state.odometer;
                    var text = '';
                    if (distance > 1000) {
                      text += "${(distance / 1000.0).toStringAsFixed(1)}km";
                    } else {
                      text += '${distance.toStringAsFixed(0)}m';
                    }
                    return Text(text);
                  },
                ),
                SizedBox(
                  width: 54.0,
                  child: BlocBuilder<LocationCubit, LocationState>(
                    buildWhen: (previous, current) =>
                        previous.isTracking != current.isTracking,
                    builder: (context, state) {
                      return ElevatedButton(
                        onPressed: () {
                          if (!state.isTracking) {
                            _requestPositionUpdate(context);
                          } else {
                            _stopPositionUpdate(context);
                          }
                        },
                        child: Icon((!state.isTracking)
                            ? Icons.play_arrow
                            : Icons.stop),
                      );
                    },
                  ),
                ),
                BlocBuilder<RequestUiFlowCubit, RequestUiFlowState>(
                  builder: (context, state) {
                    return ElevatedButton(
                      onPressed: context.select(
                              (LocationCubit cubit) => cubit.state.isTracking)
                          ? null
                          : () => _requestPositionUpdate(context,
                              forBackground: true),
                      child: const Text('Background Task'),
                    );
                  },
                )
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _requestPositionUpdate(BuildContext context,
      {bool forBackground = false}) async {
    final accuracy = context.read<SettingsCubit>().state.accuracy;
    final forceLocationManager =
        context.read<SettingsCubit>().state.forceLocationManager;
    final locationCubit = context.read<LocationCubit>();

    // use RequestUiFlowCubit just for avoid dart compile lint warning for
    // DON'T use BuildContext across asynchronous gaps.
    // https://dart-lang.github.io/linter/lints/use_build_context_synchronously.html
    final uiFlowCubit = context.read<RequestUiFlowCubit>();

    final permission = await SimpleBgLocation.checkPermission(
        onlyCheckBackground: forBackground);

    if (permission == LocationPermission.denied) {
      if (forBackground) {
        final shouldShowRationale =
            await SimpleBgLocation.shouldShowRequestPermissionRationale();
        if (shouldShowRationale) {
          uiFlowCubit.startShowBackgroundRationale(
              forBackground: forBackground);
          return;
        }
      }
      uiFlowCubit.startRequestPermission(forBackground: forBackground);
      return;
    } else if (permission == LocationPermission.deniedForever) {
      //uiFlowCubit.startShowBackgroundRationale(forBackground: forBackground);
      // todo: show dialog to open app setting
      return;
    } else if (permission == LocationPermission.whileInUse && forBackground) {
      uiFlowCubit.startRequestPermission(forBackground: forBackground);
      return;
    } else {
      if ((await SimpleBgDeviceInfo.isPowerSaveMode())) {
        uiFlowCubit.startShowPowerSavedModeWarning(
            forBackground: forBackground);
        return;
      } else {
        _callRequestPositionUpdate(
            accuracy, forceLocationManager, locationCubit, uiFlowCubit);
      }
    }
  }

  void _callRequestPositionUpdate(
    CustomAccuracy accuracy,
    bool forceLocationManager,
    LocationCubit locationCubit,
    RequestUiFlowCubit uiFlowCubit,
  ) {
    late final RequestSettings requestSettings;
    switch (accuracy) {
      case CustomAccuracy.best:
        requestSettings = RequestSettings();
        break;
      case CustomAccuracy.good:
        requestSettings = RequestSettings.good();
        break;
      case CustomAccuracy.balance:
        //!! Does not worked on Android Emulator
        requestSettings = RequestSettings.balance();
        break;
      case CustomAccuracy.lowest:
        //!! Does not worked on Android Emulator
        requestSettings = RequestSettings.lowPower();
        break;
    }
    requestSettings.notificationConfig = ForegroundNotificationConfig(
        smallIcon: const AndroidResource(name: 'drawable/ic_baseline_route_24'),
        title: "Simple BG Location",
        actions: ['Action1', 'Action2', 'Cancel']);
    requestSettings.distanceFilter = 20;
    requestSettings.forceLocationManager = forceLocationManager;
    locationCubit.requestPositionUpdate(requestSettings);
    uiFlowCubit.finish();
  }

  void _stopPositionUpdate(BuildContext context) {
    context.read<LocationCubit>().stopPositionUpdate();
  }

  Future<bool> _requestPermission(
      BuildContext context, bool forBackground) async {
    final permission = await SimpleBgLocation.requestPermission();
    if (forBackground) {
      return permission == LocationPermission.always;
    }
    return permission == LocationPermission.always ||
        permission == LocationPermission.whileInUse;
  }

  Future<bool> _showPowerSavedModeWarningDialog(BuildContext context) async {
    final result = await showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Power Save Mode is ON'),
        content: const Text(
            "Track recording may not work properly in Power Save Mode. If track does not record properly, disable Power Save Mode."),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('OK'),
          ),
        ],
      ),
    );
    return result ?? false;
  }

  Future<bool> _showBackgroundRationaleDialog(BuildContext context) async {
    final result = await showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Background Permission'),
        content: const Text(
            "Geofence and Share your location to your Family requires Background Location Permission."),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Change to "Allow always"'),
          ),
          TextButton(
            child: const Text('No, Thanks'),
            onPressed: () => Navigator.pop(context, false),
          ),
        ],
      ),
    );
    return result ?? false;
  }
}
