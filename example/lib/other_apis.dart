import 'package:flutter/material.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

class OtherApiBody extends StatefulWidget {
  const OtherApiBody({Key? key}) : super(key: key);

  @override
  State<OtherApiBody> createState() => _OtherApiBodyState();
}

class _OtherApiBodyState extends State<OtherApiBody> {
  final _simpleBgLocation = SimpleBgLocation();

  String _resultInfo = "result";

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Container(
            height: 200,
            decoration: BoxDecoration(
                border: Border.all(color: Colors.black, width: 1)),
            padding: const EdgeInsets.all(4),
            child: Text(_resultInfo),
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
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _checkPermission() async {
    setState(() {
      _resultInfo = "";
    });
    _resultInfo = "checkPermission ";
    try {
      final result = await _simpleBgLocation.checkPermission();
      setState(() {
        _resultInfo += "successful: $result";
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
    setState(() {
      _resultInfo = "";
    });
    _resultInfo = "requestPermission";
    try {
      final result = await _simpleBgLocation.requestPermission();
      setState(() {
        _resultInfo += "successful: $result";
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
}
