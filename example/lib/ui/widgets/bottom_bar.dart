import 'dart:developer' as dev;
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location_example/cubit/position/position_cubit.dart';
import 'package:simple_bg_location_example/cubit/settings/settings_cubit.dart';

class MyBottomBar extends StatelessWidget {
  const MyBottomBar({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 5.0),
        child: Row(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            IconButton(
              onPressed: () =>
                  context.read<PositionCubit>().getCurrentPosition(),
              icon: const Icon(Icons.gps_fixed),
            ),
            BlocBuilder<PositionCubit, PositionState>(
              buildWhen: (previous, current) =>
                  previous.odometer != current.odometer,
              builder: (context, state) {
                final distance = state.odometer;
                var text = 'distance: ';
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
              child: BlocBuilder<PositionCubit, PositionState>(
                builder: (context, state) {
                  return ElevatedButton(
                    onPressed: () {
                      if (!state.isTracking) {
                        _requestPositionUpdate(context);
                      } else {
                        _stopPositionUpdate(context);
                      }
                    },
                    child: Icon(
                        (!state.isTracking) ? Icons.play_arrow : Icons.stop),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _requestPositionUpdate(BuildContext context) async {
    final accuracy = context.read<SettingsCubit>().state.accuracy;

    final allow = await _checkAndRequestPermission(context);
    if (allow) {
      
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
      // ignore: use_build_context_synchronously
      context.read<PositionCubit>().requestPositionUpdate(
          requestSettings);
    }
  }

  void _stopPositionUpdate(BuildContext context) {
    context.read<PositionCubit>().stopPositionUpdate();
  }

  Future<bool> _checkAndRequestPermission(BuildContext context) async {
    // Since Android 6.0 (API level 23) or high, you must request any dangerous
    // permission at runtime.
    // more detail description:
    // https://developer.android.com/training/permissions/requesting#manage-request-code-yourself
    // and request location permission
    // https://developer.android.com/training/location/permissions
    //
    var requestResult = await SimpleBgLocation.requestPermission();

    if (requestResult == LocationPermission.always ||
        requestResult == LocationPermission.whileInUse) {
      // Since Android 11 (API level 30) the system enforces app performs
      // incremental request for location permission. so if your app needs
      // background permission, first time request only permit whileInUse
      // permission. second time request will bring user to permit always
      // permission.
      return true;
    }

    if (requestResult == LocationPermission.denied) {
      final alreadyHasPermission = await SimpleBgLocation.checkPermission();
      if (alreadyHasPermission == LocationPermission.whileInUse) {
        // upgrade permission from whileInUse to always was denied by user

        // Because the example app need always permission, so follow android best
        // practice, explain why you need the background permission
        // show your rationale dialog here. or call SimpleBgLocation.requestPermission
        // with BackgroundPermissionRationale argument again.
        final rationResult = await showDialog<bool>(
          context: context,
          builder: (_) => AlertDialog(
            content: const Text(
                'Share you location to your family need background location permission'),
            title: const Text('This App need background Location Permission'),
            actions: [
              TextButton(
                child: const Text('Change to "Allow always"'),
                onPressed: () => Navigator.pop(context, true),
              ),
              TextButton(
                child: const Text('No, Thanks'),
                onPressed: () => Navigator.pop(context, false),
              ),
            ],
          ),
        );
        if (rationResult == true) {
          await SimpleBgLocation.requestPermission();
          // we don't care this time request result. if user upgrade to always
          // or just keep whileInUse permission, we should return true.
          // if user downgrade permission to 'don't allow', android system will
          // force restart app. so whatever return true or false is no matter.
        }
        return true;
      } else if (alreadyHasPermission == LocationPermission.always) {
        /// this situation happen after Android 12, user denied upgrade accuracy
        /// permission from approximate to precise, but you still have background
        /// permission.
        return true;
      } else {
        /// user denied any permission.
        return false;
      }
    } else if (requestResult == LocationPermission.deniedForever) {
      // User denied same permission more than one times. system will never
      // bring permission setting dialog.
      final alreadyHasPermission = await SimpleBgLocation.checkPermission();
      if (alreadyHasPermission == LocationPermission.denied) {
        return false;
      }
      if (alreadyHasPermission == LocationPermission.whileInUse) {
        // we want always permission, but system never show permission setting
        // again, we can suggest user to open App Settings dialog the permit.
        final prefs = await SharedPreferences.getInstance();
        final dontAskOpenAppSettings =
            prefs.getBool('dont_ask_open_app_settings') ?? false;
        if (dontAskOpenAppSettings == false) {
          final useSayYes = await showDialog<bool>(
              context: context,
              builder: (_) => AlertDialog(
                    title:
                        const Text('We truly need the backgroud permissioin'),
                    content: const Text(
                        'Do you want open AppSettings to upgrade location permission?'),
                    actions: [
                      TextButton(
                        child: const Text('Yes'),
                        onPressed: () => Navigator.pop(context, true),
                      ),
                      TextButton(
                        child: const Text('No, Never ask me again!'),
                        onPressed: () {
                          prefs.setBool('dont_ask_open_app_settings', true);
                          Navigator.pop(context, false);
                        },
                      ),
                    ],
                  ));
          if (useSayYes == true) {
            SimpleBgLocation.openAppSettings();
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
}
