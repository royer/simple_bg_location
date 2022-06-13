import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location_example/cubit/position_cubit.dart';

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
              onPressed: () {},
              icon: const Icon(Icons.gps_fixed),
            ),
            const Text("distance:"),
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
    final allow = await _checkAndRequestPermission(context);
    if (allow) {
      context.read<PositionCubit>().requestPositionUpdate(RequestSettings());
    }
  }

  void _stopPositionUpdate(BuildContext context) {
    context.read<PositionCubit>().stopPositionUpdate();
  }

  Future<bool> _checkAndRequestPermission(BuildContext context) async {
    var hasPermission = await SimpleBgLocation.checkPermission();

    /// if your App request background permission check LocationPermission.always
    /// otherwise check whileInUse
    if (hasPermission == LocationPermission.always) {
      return true;
    } else {
      var requestResult = await SimpleBgLocation.requestPermission();
      if (requestResult == LocationPermission.always) {
        /// got you desire permission
        return true;
      } else if (requestResult == LocationPermission.whileInUse) {
        // this time use only approve whileInUse, app can run
        // next time ask user for background permission again.
        return true;
      } else if (requestResult == LocationPermission.denied) {
        if (hasPermission == LocationPermission.whileInUse) {
          // todo: show your rationale dialog here
          final rationresult = await showDialog<bool>(
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
          if (rationresult == true) {
            requestResult = await SimpleBgLocation.requestPermission();
            if (requestResult != LocationPermission.always) {
              return false;
            }
          }
          return true;
        } else {
          return false;
        }
      } else if (requestResult == LocationPermission.deniedForever) {
        hasPermission = await SimpleBgLocation.checkPermission();
        if (hasPermission == LocationPermission.whileInUse) {
          // todo: ask user to open AppSettings to change permission
          final prefs = await SharedPreferences.getInstance();
          final dontAskApp =
              prefs.getBool('dont_ask_open_app_settings') ?? false;
          if (dontAskApp == false) {
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
                          onPressed: () async {
                            await prefs.setBool(
                                'dont_ask_open_app_settings', true);
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
          return true;
        }
      }
      return false;
    }
  }
}
