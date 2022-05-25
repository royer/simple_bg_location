import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'simple_bg_location_method_channel.dart';
import 'src/enums/enums.dart';

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
  /// Returns a [Future] which when completes indicates if the user granted
  /// permission to access the device's location.
  /// Throws a [PermissionDefinitionsNotFoundException] when the required
  /// platform specific configuration is missing (e.g. in the
  /// AndroidManifest.xml on Android or the Info.plist on iOS).
  /// A [PermissionRequestInProgressException] is thrown if permissions are
  /// requested while an earlier request has not yet been completed.
  Future<LocationPermission> requestPermission() {
    throw UnimplementedError('requestPermission() has not been implemented.');
  }
}
