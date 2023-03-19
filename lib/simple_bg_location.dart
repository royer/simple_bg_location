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

  /// Wraps the [SimpleBgLocationPlatform.checkPermission]
  ///
  /// Returns a [Future]<`LocationPermission`> indicating if the user allows the
  /// App to access the device's location
  ///
  /// If [onlyCheckBackground] is true, only check background location permission.
  /// even if the App already has the 'whileInUse' permission, it still will return
  /// 'denied' or 'deniedForever' if the App doesn't have the 'always' permission.
  ///
  /// Throws a [PermissionDefinitionsNotFoundException] when there is no permission
  /// description in the AndroidManifest.xml on Android or the Info.plist on iOS.
  static Future<LocationPermission> checkPermission(
      {bool onlyCheckBackground = false}) {
    return SimpleBgLocationPlatform.instance
        .checkPermission(onlyCheckBackground: onlyCheckBackground);
  }

  /// Wraps the [SimpleBgLocationPlatform.requestPermission]
  ///
  /// Request permission to access the location of the device.
  ///
  /// Returns a [Future]<`LocationPermission`> which when completes indicates
  /// if the user granted permission to access the device's location in this
  /// request.
  ///
  /// If [rationale] is not null, it will be shown a default rationale dialog
  /// when request background permission denied and [shouldShowRequestPermissionRationale]
  /// return true.(Only for Android 11 or higher)
  ///
  /// Throws a [PermissionDefinitionsNotFoundException] when the required
  /// platform specific configuration is missing (e.g. in the
  /// AndroidManifest.xml on Android or the Info.plist on iOS).
  ///
  /// A [PermissionRequestInProgressException] is thrown if permissions are
  /// requested while an earlier request has not yet been completed.
  ///
  /// ⚠️ Note: The return value is the same as [checkPermission] return value
  /// in most of the time. but there is some exceptions.
  /// [requestPermission] return value is indicates this time request get
  /// what kind permission. [checkPermission] is the permission already have.
  /// for example:
  /// Now already has 'whileInUse' permission, call requestPermission
  /// to upgrade to 'always' permission, but user do not approve upgrade to
  /// 'always', then [requestPermission] while return 'denied' or 'deniedForever'
  /// indicates this request failed.  ///
  static Future<LocationPermission> requestPermission(
      [BackgroundPermissionRationale? rationale]) {
    return SimpleBgLocationPlatform.instance.requestPermission(rationale);
  }

  /// Wraps the [SimpleBgLocationPlatform.shouldShowRequestPermissionRationale]
  ///
  /// Should show request permission rationale for background location. Only for Android.
  /// iOS will always return false.
  ///
  /// On Android 11 or higher, If your App hasn't been granted background location, and
  /// shouldShowRequestPermissionRationale returns false, you should show an educational
  /// UI to the user explaining why your App needs background location access.
  ///
  /// Note: The Plugin provide a default rationale UI which is [BackgroundPermissionRationale]
  /// when call [requestPermission] with a not null rationale parameter. If you show your own
  /// rationale UI, don't set this parameter
  static Future<bool> shouldShowRequestPermissionRationale() {
    return SimpleBgLocationPlatform.instance
        .shouldShowRequestPermissionRationale();
  }

  /// Wraps the [SimpleBgLocationPlatform.isLocationServiceEnabled]
  ///
  /// Check device location service is enabled or not.
  static Future<bool> isLocationServiceEnabled() {
    return SimpleBgLocationPlatform.instance.isLocationServiceEnabled();
  }

  /// Wraps the [SimpleBgLocationPlatform.getAccuracyPermission]
  ///
  /// Check which accuracy permission that user approved.
  static Future<LocationAccuracyPermission> getAccuracyPermission() {
    return SimpleBgLocationPlatform.instance.getAccuracyPermission();
  }

  /// Wraps the [SimpleBgLocationPlatform.getLastKnowPosition]
  ///
  /// Get the best most recent location currently available.
  ///
  /// If a location is not available, which should happen very rarely, `null` will
  /// be return. The best accuracy available while respecting the location
  /// permissions will be returned.
  ///
  ///- Throws a [PermissionDeniedException] when user has not approved access.
  ///- Throws a [LocationServiceDisabledException] when the user allowed access,
  /// but the location services of the device are disabled.
  static Future<Position?> getLastKnowPosition(
      {bool forceLocationManager = false}) {
    return SimpleBgLocationPlatform.instance
        .getLastKnownPosition(forceLocationManager: forceLocationManager);
  }

  /// Wraps the [SimpleBgLocationPlatform.getCurrentPosition]
  ///
  /// Get a single current location fix on the device.
  ///
  /// Unlike [getLastKnownPosition] that returns a cached location, this method
  /// could cause active location computation on the device. If the device
  /// location can be determined within reasonable time(tens of seconds),
  /// otherwise `null` will be return
  ///
  ///- Throws a [PermissionDeniedException] when user has not approved access.
  ///- Throws a [LocationServiceDisabledException] when the user allowed access,
  /// but the location services of the device are disabled.
  static Future<Position?> getCurrentPosition(
      {bool forceLocationManager = false}) {
    return SimpleBgLocationPlatform.instance
        .getCurrentPosition(forceLocationManager: forceLocationManager);
  }

  /// Wraps the [SimpleBgLocationPlatform.ready]
  /// Initialize plugin for prepare requestPositionUpdate
  ///
  /// Before call [requestPositionUpdate] must call ready() to get current plugin
  /// state.
  /// In android when user use back button quit app, flutter will shutdown all
  /// dart code. use ready() notify plugin and check whether the last position
  /// update is sill tracking, and get positions updates in plugin cache.
  ///
  /// ⚠️ No position updated event will send if miss called ready()
  static Future<SBGLState> ready() {
    return SimpleBgLocationPlatform.instance.ready();
  }

  /// Wraps the [SimpleBgLocationPlatform.getPositionStream]
  ///
  /// The position update stream.
  ///
  /// Fires whenever the location changed inside the bounds of the
  /// [RequestSettings.accuracy]
  ///
  static Stream<Position> getPositionStream(
      [Function(PositionError)? handleError]) {
    return SimpleBgLocationPlatform.instance.getPositionStream(handleError);
  }

  /// Wraps the [SimpleBgLocationPlatform.getNotificationActionStream]
  ///
  /// __Android Only__ The custom notification action button click event stream.
  static Stream<String> getNotificationStream() {
    return SimpleBgLocationPlatform.instance.getNotificationActionStream();
  }

  /// register a position update listener.
  static void onPosition(Function(Position) success,
      [Function(PositionError)? failure]) {
    final stream = getPositionStream(failure);
    _registerSubscription(stream.listen(success), success);
  }

  /// __Android Only__ register a notification action listener.
  static void onNotificationAction(
    Function(String) success,
  ) {
    final stream = getNotificationStream();
    _registerSubscription(stream.listen(success), success);
  }

  /// Wraps the [SimpleBgLocationPlatform.requestPositionUpdate]
  ///
  /// Request location change listen.
  ///
  /// All position updates will send to [getPositionStream] stream or the callback register
  /// by [onPosition].
  ///
  /// The [requestSettings] parameter is used to configure the location update
  /// behavior and customer notification on Android. more detail see [RequestSettings]
  static Future<bool> requestPositionUpdate(RequestSettings requestSettings) {
    return SimpleBgLocationPlatform.instance
        .requestPositionUpdate(requestSettings);
  }

  /// Wraps the [SimpleBgLocationPlatform.stopPositionUpdate]
  ///
  /// Stop listen location change.
  static Future<bool> stopPositionUpdate() {
    return SimpleBgLocationPlatform.instance.stopPositionUpdate();
  }

  /// Wraps the [SimpleBgLocationPlatform.openAppSettings]
  ///
  /// Open the App settings page
  ///
  /// Returns [true] if app settings page could be opened, otherwise [false]
  /// is returned
  static Future<bool> openAppSettings() {
    return SimpleBgLocationPlatform.instance.openAppSettings();
  }

  /// Wraps the [SimpleBgLocationPlatform.openLocationSettings]
  ///
  /// Opens the location settings page.
  ///
  /// Returns [true] if the location settings page could be opened, otherwise
  /// [false] is returned.
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
