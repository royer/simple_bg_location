import 'package:flutter/material.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

class OtherApiView extends StatefulWidget {
  const OtherApiView({Key? key}) : super(key: key);

  @override
  State<OtherApiView> createState() => _OtherApiViewState();
}

class _OtherApiViewState extends State<OtherApiView> {
  String _resultInfo = "";
  String _method = "";

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              const Text("Method:"),
              const SizedBox(width: 4),
              Expanded(
                child: Text(
                  _method,
                  style: const TextStyle(fontSize: 12.0),
                  overflow: TextOverflow.ellipsis,
                  softWrap: true,
                  maxLines: 5,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          const Text("Returns:"),
          Container(
            height: 200,
            width: double.infinity,
            decoration: BoxDecoration(
                border: Border.all(color: Colors.black, width: 1)),
            padding: const EdgeInsets.all(4),
            child: Text(
              _resultInfo,
              style: const TextStyle(fontSize: 12),
            ),
          ),
          const SizedBox(height: 16),
          Expanded(
            child: SingleChildScrollView(
              child: Column(
                children: [
                  TextButton(
                    onPressed: _checkPermission,
                    child: const Text("checkPermission"),
                  ),
                  TextButton(
                    onPressed: _onlyCheckBackgroundPermission,
                    child: const Text("checkPermission (only background)"),
                  ),

                  TextButton(
                      onPressed: _requestPermission,
                      child: const Text("requestPermission")),
                  TextButton(
                    onPressed: _isLocationServiceEnabled,
                    child: const Text("isLocationServicesEnabled"),
                  ),
                  TextButton(
                    onPressed: _getAccuracyPermission,
                    child: const Text("getAccuracyPermission"),
                  ),
                  // PopupMenuButton<GetLastKnownPositionMenu>(
                  //   itemBuilder: (context) =>
                  //       <PopupMenuEntry<GetLastKnownPositionMenu>>[
                  //     const PopupMenuItem<GetLastKnownPositionMenu>(
                  //       value: GetLastKnownPositionMenu.normal,
                  //       child: Text('normal'),
                  //     ),
                  //     const PopupMenuItem<GetLastKnownPositionMenu>(
                  //       value: GetLastKnownPositionMenu.forceLocationManager,
                  //       child: Text('forceLocationManager'),
                  //     ),
                  //   ],
                  //   onSelected: (item) {
                  //     _onGetLastKnownPosition(item ==
                  //         GetLastKnownPositionMenu.forceLocationManager);
                  //   },
                  //   child: const Text('getLastKnownPosition'),
                  // ),
                  DropdownButton<String>(
                    value: 'getLastKnownPosition',
                    icon: const Icon(Icons.arrow_downward),
                    items: <String>[
                      'getLastKnownPosition',
                      ForceLocationManagerOrNot.normal.name,
                      ForceLocationManagerOrNot.forceLocationManager.name
                    ]
                        .map((e) =>
                            DropdownMenuItem<String>(value: e, child: Text(e)))
                        .toList(),
                    onChanged: (item) {
                      _onGetLastKnownPosition(item ==
                          ForceLocationManagerOrNot.forceLocationManager.name);
                    },
                  ),
                  DropdownButton<String>(
                    value: 'getCurrentPosition',
                    icon: const Icon(Icons.arrow_downward),
                    items: <String>[
                      'getCurrentPosition',
                      ForceLocationManagerOrNot.normal.name,
                      ForceLocationManagerOrNot.forceLocationManager.name
                    ]
                        .map((e) =>
                            DropdownMenuItem<String>(value: e, child: Text(e)))
                        .toList(),
                    onChanged: (item) {
                      _onGetCurrentPosition(item ==
                          ForceLocationManagerOrNot.forceLocationManager.name);
                    },
                  ),

                  TextButton(
                    onPressed: _openAppSettings,
                    child: const Text("openAppSettings"),
                  ),
                  TextButton(
                    onPressed: _openLocationSettings,
                    child: const Text("openLocationSettings"),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _reset() {
    setState(() {
      _method = "";
      _resultInfo = "";
    });
  }

  void _checkPermission() async {
    _reset();
    try {
      setState(() {
        _method = "checkPermission";
      });
      final result = await SimpleBgLocation.checkPermission();
      setState(() {
        _resultInfo += "$result";
      });
    } on PermissionDefinitionsNotFoundException catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nException: ${e.runtimeType}\n"${e.toString()}"';
      });
    } on Exception catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nOther Exception: ${e.runtimeType}\n"${e.toString()}"';
      });
    }
  }

  void _onlyCheckBackgroundPermission() async {
    _reset();
    try {
      setState(() {
        _method = "checkPermission(onlyBackground: true)";
      });
      final result = await SimpleBgLocation.checkPermission(onlyCheckBackground: true);
      setState(() {
        _resultInfo += "$result";
      });
    } on PermissionDefinitionsNotFoundException catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nException: ${e.runtimeType}\n"${e.toString()}"';
      });
    } on Exception catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nOther Exception: ${e.runtimeType}\n"${e.toString()}"';
      });
    }
  }


  void _requestPermission() async {
    _reset();

    try {
      setState(() {
        _method = "requestPermission";
      });
      final result = await SimpleBgLocation.requestPermission(
          const BackgroundPermissionRationale(
        title: 'This is demo of request background location permission rationale',
        message: 'Keep share your location to your family need background location permission',
      ));
      setState(() {
        _resultInfo += "$result";
      });
    } on PermissionDefinitionsNotFoundException catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nException: ${e.runtimeType}\n"${e.toString()}"';
      });
    } on Exception catch (e) {
      setState(() {
        _resultInfo +=
            'failed.\nOther Exception: ${e.runtimeType}\n"${e.toString()}"';
      });
    }
  }

  void _openAppSettings() async {
    _reset();

    try {
      setState(() {
        _method = "openAppSettings";
      });
      final result = await SimpleBgLocation.openAppSettings();
      setState(() {
        _resultInfo = "$result";
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }

  void _openLocationSettings() async {
    _reset();

    try {
      setState(() {
        _method = "openLocationSettings";
      });

      final result = await SimpleBgLocation.openLocationSettings();
      setState(() {
        _resultInfo = "$result";
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }

  void _isLocationServiceEnabled() async {
    _reset();

    try {
      setState(() {
        _method = "isLocationServiceEnabled";
      });
      final result = await SimpleBgLocation.isLocationServiceEnabled();
      setState(() {
        _resultInfo = '$result';
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }

  void _getAccuracyPermission() async {
    _reset();

    try {
      setState(() {
        _method = "getAccuracyPermission";
      });
      final result = await SimpleBgLocation.getAccuracyPermission();
      setState(() {
        _resultInfo = "$result";
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }

  void _onGetLastKnownPosition(bool forceLocationManager) async {
    _reset();
    try {
      setState(() {
        _method =
            "getLastKnownPosition(forceLocationManager = $forceLocationManager)";
      });
      final position = await SimpleBgLocation.getLastKnowPosition(
          forceLocationManager: forceLocationManager);
      setState(() {
        _resultInfo = "$position";
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }

  void _onGetCurrentPosition(bool forceLocationManager) async {
    _reset();
    try {
      setState(() {
        _method =
            "getCurrentPosition(forceLocationManager = $forceLocationManager)";
      });
      final position = await SimpleBgLocation.getCurrentPosition(
          forceLocationManager: forceLocationManager);
      setState(() {
        _resultInfo = "$position";
      });
    } catch (e) {
      setState(() {
        _resultInfo = "failed. Exception: $e";
      });
    }
  }
}

enum ForceLocationManagerOrNot { normal, forceLocationManager }
