import 'dart:async';
import 'dart:math';

import 'package:collection/collection.dart';

import 'simple_bg_location_platform_interface.dart';
import 'src/errors/errors.dart';
import 'src/models/models.dart';
import 'src/enums/enums.dart';

export 'src/errors/errors.dart';
export 'src/models/models.dart';
export 'src/enums/enums.dart';

class _Subscription {
  final StreamSubscription<dynamic> subscription;
  final Function callback;
  _Subscription(this.subscription, this.callback);
}

class SimpleBgLocation {
  static void _registerSubscription(
      StreamSubscription<dynamic> sub, Function callback) {
    _subscriptions.add(_Subscription(sub, callback));
  }

  static final List<_Subscription> _subscriptions = [];

  /// Remove a single event listener.
  ///
  /// ## Example
  ///
  /// ```dart
  ///
  /// // Create a position callback
  /// Function(Position) callback = (Position position) {
  ///   print('Position: $position')
  /// }
  ///
  /// SimpleBgLocation.onPosition(callback)
  /// .
  /// .
  /// .
  /// .
  /// SimpleBgLocation.removeListener(callback)
  /// ```
  static bool removeListener(Function callback) {
    _Subscription? found = _subscriptions
        .firstWhereOrNull((element) => element.callback == callback);
    if (found != null) {
      found.subscription.cancel();
      _subscriptions.remove(found);
      return true;
    } else {
      return false;
    }
  }

  /// Remove all registered event listener
  ///
  /// - [onPosition]
  ///
  static Future<void> removeListeners() async {
    await Future.wait(_subscriptions.map((e) => e.subscription.cancel()));
    _subscriptions.clear();
  }

  /// see [SimpleBgLocationPlatform.checkPermission]
  static Future<LocationPermission> checkPermission() {
    return SimpleBgLocationPlatform.instance.checkPermission();
  }

  /// Wraps the [SimpleBgLocationPlatform.requestPermission]
  static Future<LocationPermission> requestPermission(
      [BackgroundPermissionRationale? rationale]) {
    return SimpleBgLocationPlatform.instance.requestPermission(rationale);
  }

  static Future<bool> isLocationServiceEnabled() {
    return SimpleBgLocationPlatform.instance.isLocationServiceEnabled();
  }

  static Future<LocationAccuracyPermission> getAccuracyPermission() {
    return SimpleBgLocationPlatform.instance.getAccuracyPermission();
  }

  static Future<Position?> getLastKnowPosition(
      {bool forceLocationManager = false}) {
    return SimpleBgLocationPlatform.instance
        .getLastKnownPosition(forceLocationManager: forceLocationManager);
  }

  static Future<Position?> getCurrentPosition(
      {bool forceLocationManager = false}) {
    return SimpleBgLocationPlatform.instance
        .getCurrentPosition(forceLocationManager: forceLocationManager);
  }

  static Future<SBGLState> ready() {
    return SimpleBgLocationPlatform.instance.ready();
  }

  static Stream<Position> getPositionStream(
      [Function(PositionError)? handleError]) {
    return SimpleBgLocationPlatform.instance.getPositionStream(handleError);
  }

  static Stream<String> getNotificationStream() {
    return SimpleBgLocationPlatform.instance.getNotificationActionStream();
  }

  static void onPosition(Function(Position) success,
      [Function(PositionError)? failure]) {
    final stream = getPositionStream(failure);
    _registerSubscription(stream.listen(success), success);
  }

  static void onNotificationAction(
    Function(String) success,
  ) {
    final stream = getNotificationStream();
    _registerSubscription(stream.listen(success), success);
  }

  static Future<bool> requestPositionUpdate(RequestSettings requestSettings) {
    return SimpleBgLocationPlatform.instance
        .requestPositionUpdate(requestSettings);
  }

  static Future<bool> stopPositionUpdate() {
    return SimpleBgLocationPlatform.instance.stopPositionUpdate();
  }

  static Future<bool> openAppSettings() {
    return SimpleBgLocationPlatform.instance.openAppSettings();
  }

  static Future<bool> openLocationSettings() {
    return SimpleBgLocationPlatform.instance.openLocationSettings();
  }

  /// Calculates the distance between the supplied coordinates in meters.
  ///
  /// The distance between the coordinates is calculated using the Haversine
  /// formula (see https://en.wikipedia.org/wiki/Haversine_formula). The
  /// supplied coordinates [startLatitude], [startLongitude], [endLatitude] and
  /// [endLongitude] should be supplied in degrees.
  static double distance(
    double startLatitude,
    double startLongitude,
    double endLatitude,
    double endLongitude,
  ) {
    const earthRadius = 6378137.0;
    final dLat = _toRadians(endLatitude - startLatitude);
    final dLon = _toRadians(endLongitude - startLongitude);

    final a = pow(sin(dLat / 2), 2) +
        pow(sin(dLon / 2), 2) *
            cos(_toRadians(startLatitude)) *
            cos(_toRadians(endLatitude));
    final c = 2 * asin(sqrt(a));

    return earthRadius * c;
  }

  static _toRadians(double degree) {
    return degree * pi / 180.0;
  }
}
