import 'package:flutter/material.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

class OtherApiBody extends StatefulWidget {
  const OtherApiBody({Key? key}) : super(key: key);

  @override
  State<OtherApiBody> createState() => _OtherApiBodyState();
}

class _OtherApiBodyState extends State<OtherApiBody> {
  final _simpleBgLocation = SimpleBgLocation();

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
                  DropdownButton<GetLastKnownPositionMenu>(
                    value: GetLastKnownPositionMenu.normal,
                    icon: const Icon(Icons.arrow_downward),
                    items: [
                      GetLastKnownPositionMenu.normal,
                      GetLastKnownPositionMenu.forceLocationManager
                    ]
                        .map((e) => DropdownMenuItem<GetLastKnownPositionMenu>(
                            value: e, child: Text(e.name)))
                        .toList(),
                    onChanged: (item) {
                      _onGetLastKnownPosition(item ==
                          GetLastKnownPositionMenu.forceLocationManager);
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
      final result = await _simpleBgLocation.checkPermission();
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
      final result = await _simpleBgLocation.requestPermission();
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
      final result = await _simpleBgLocation.openAppSettings();
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

      final result = await _simpleBgLocation.openLocationSettings();
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
      final result = await _simpleBgLocation.isLocationServiceEnabled();
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
      final result = await _simpleBgLocation.getAccuracyPermission();
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
      final position = await _simpleBgLocation.getLastKnowPosition(
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

enum GetLastKnownPositionMenu { normal, forceLocationManager }
