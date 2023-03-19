import 'package:flutter/material.dart';
import 'package:simple_bg_location/simple_bg_device_info.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Simple_BG_Location Tiny Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List<Position> positions = [];
  bool isReady = false;
  double odometer = 0;
  bool isTracking = false;
  final List<LocationEventItem> events = [];

  final scrollController = ScrollController();
  void _scrollToBottom() {
    scrollController.animateTo(scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300), curve: Curves.easeOut);
  }

  @override
  void initState() {
    super.initState();
    SimpleBgLocation.onPosition(_onPosition, _positionErrorHandle);
    SimpleBgLocation.onNotificationAction(_onNotificationAction);
    SimpleBgLocation.ready().then((sbglState) {
      isReady = true;
      positions.addAll(sbglState.positions ?? []);
      events.addAll(positions.map((e) => LocationEventItem(
            LocationEventType.position,
            'position record restored from service',
            detail: 'lat: ${e.latitude}, lng: ${e.longitude}',
          )));
      odometer = 0;
      if (positions.length >= 2) {
        for (int i = 1; i < positions.length; i++) {
          final d = SimpleBgLocation.distance(
            positions[i - 1].latitude,
            positions[i - 1].longitude,
            positions[i].latitude,
            positions[i].longitude,
          );
          odometer += d;
        }
      }
      isTracking = sbglState.isTracking;
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: const Text('Simple Background Location Demo'),
      ),
      body: ListView.separated(
        controller: scrollController,
        itemCount: events.length,
        itemBuilder: (context, index) {
          final item = events[index];
          if (item.type == LocationEventType.position) {
            return ListTile(
              dense: true,
              title: Text(
                item.title,
                style: const TextStyle(fontSize: 12.0),
              ),
              subtitle:
                  Text(item.detail, style: const TextStyle(fontSize: 10.0)),
              leading: const Icon(Icons.location_on),
            );
          } else {
            return ListTile(
              dense: true,
              title: Text(item.title, style: const TextStyle(fontSize: 12.0)),
              subtitle:
                  Text(item.detail, style: const TextStyle(fontSize: 10.0)),
              leading: const Icon(Icons.info),
            );
          }
        },
        separatorBuilder: (context, index) {
          return const Divider();
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: isReady
            ? () {
                if (!isTracking) {
                  _startPositionUpdate();
                } else {
                  SimpleBgLocation.stopPositionUpdate();
                  isTracking = false;
                  setState(() {});
                }
              }
            : null,
        tooltip: isTracking ? 'Stop' : 'Start',
        child: Icon(isTracking ? Icons.stop : Icons.play_arrow),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  void _startPositionUpdate() async {
    if (!(await SimpleBgLocation.isLocationServiceEnabled())) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Location service disabled',
      ));
      setState(() {});
      SimpleBgLocation.openLocationSettings();
      return;
    }
    var permission = await SimpleBgLocation.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await SimpleBgLocation.requestPermission();
      if (permission == LocationPermission.denied) {
        events.add(const LocationEventItem(
          LocationEventType.log,
          'Permission denied',
        ));
        setState(() {});

        return;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Permission denied forever',
      ));
      setState(() {});
      // Do not call openAppSetting directly in the formal product.
      // Instead, you should ask the user if they are willing,
      // and do not ask again after the user has refused multiple times.
      SimpleBgLocation.openAppSettings();

      return;
    }

    if ((await SimpleBgDeviceInfo.isPowerSaveMode())) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Power save mode enabled!',
        detail: '''Track recording may not work properly in Power Save Mode. 
            If track does not record properly, disable Power Save Mode.''',
      ));
      return;
    }

    final requestSettings = RequestSettings.good();
    requestSettings.notificationConfig = ForegroundNotificationConfig(
      notificationId: 100,
      title: "Simple BG Location",
      text: "distance: {distance}",
      priority: ForegroundNotificationConfig.NOTIFICATION_PRIORITY_DEFAULT,
      actions: ['Action1', 'Action2', 'cancel'],
    );

    final success =
        await SimpleBgLocation.requestPositionUpdate(requestSettings);
    if (success) {
      isTracking = true;
    } else {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Error',
        detail: 'Request position update failed',
      ));
    }
    setState(() {});
  }

  void _onPosition(Position position) {
    final strEvent = 'lat: ${position.latitude}, lng: ${position.longitude}';
    events.add(LocationEventItem(LocationEventType.position, strEvent));
    setState(() {});
  }

  void _positionErrorHandle(PositionError err) {
    events.add(LocationEventItem(
        LocationEventType.log, 'PositionError CODE: ${err.code}',
        detail: err.message));
    isTracking = false;
    setState(() {});
  }

  void _onNotificationAction(String action) {
    events.add(LocationEventItem(
      LocationEventType.log,
      'Notification action: $action',
    ));
    setState(() {});
  }
}

class LocationEventItem {
  final String title;
  final String detail;
  final LocationEventType type;

  const LocationEventItem(this.type, this.title, {this.detail = ''});
}

enum LocationEventType { log, position }
