import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'simple_bg_location_method_channel.dart';
import 'src/enums/enums.dart';
import 'src/models/models.dart';

abstract class SimpleBgLocationPlatform extends PlatformInterface {
  /// Constructs a SimpleBgLocationPlatform.
  SimpleBgLocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static SimpleBgLocationPlatform _instance = MethodChannelSimpleBgLocation();

  /// The default instance of [SimpleBgLocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelSimpleBgLocation].
  static SimpleBgLocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SimpleBgLocationPlatform] when
  /// they register themselves.
  static set instance(SimpleBgLocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Returns a [Future]<`LocationPermission`> indicating if the user allows the
  /// App to access the device's location
  Future<LocationPermission> checkPermission() {
    throw UnimplementedError("checkPermission() has not been implemented.");
  }

  /// Request permission to access the location of the device.
  ///
  /// Returns a [Future]<`LocationPermission`> which when completes indicates
  /// if the user granted permission to access the device's location in this
  /// request.
  /// Throws a [PermissionDefinitionsNotFoundException] when the required
  /// platform specific configuration is missing (e.g. in the
  /// AndroidManifest.xml on Android or the Info.plist on iOS).
  /// A [PermissionRequestInProgressException] is thrown if permissions are
  /// requested while an earlier request has not yet been completed.
  ///
  /// Note: The return value is the same as [checkPermission] return value
  /// in most of the time. but there is some exceptions.
  /// [requestPermission] return value is indicates this time request get
  /// what kind permission. [checkPermission] is the permission already have.
  /// for example:
  /// Now already has 'whileInUse' permission, call requestPermission
  /// to upgrade to 'always' permission, but user do not approve upgrade to
  /// 'always', then [requestPermission] while return 'denied' or 'deniedForever'
  /// indicates this request failed.
  Future<LocationPermission> requestPermission() {
    throw UnimplementedError('requestPermission() has not been implemented.');
  }

  /// Check location service is enabled
  Future<bool> isLocationServiceEnabled() {
    throw UnimplementedError(
        'isLocationServiceEnable() has not been implemented.');
  }

  Future<LocationAccuracyPermission> getAccuracyPermission() {
    throw UnimplementedError(
        'getAccuracyPermission() has not been implemented.');
  }

  Future<Position?> getLastKnownPosition({bool forceLocationManager = false}) {
    throw UnimplementedError(
        'getLastKnownPosition() has not been implemented.');
  }

  /// Open the App settings page
  ///
  /// Returns [true] if app settings page could be opened, otherwise [false]
  /// is returned
  Future<bool> openAppSettings() async {
    throw UnimplementedError('openAppSettings() has not been implemented.');
  }

  /// Opens the location settings page.
  ///
  /// Returns [true] if the location settings page could be opened, otherwise
  /// [false] is returned.
  Future<bool> openLocationSettings() async {
    throw UnimplementedError(
        'openLocationSettings() has not been implemented.');
  }
}
