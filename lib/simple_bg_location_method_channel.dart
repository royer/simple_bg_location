import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:simple_bg_location/src/errors/location_services_disabled_exception.dart';
import 'src/errors/errors.dart';
import 'src/enums/enums.dart';

import 'simple_bg_location_platform_interface.dart';

const _pluginPath = "com.royzed.simple_bg_location";

class Methods {
  static const checkPermission = 'checkPermission';
  static const requestPermission = "requestPermission";
  static const isLocationServiceEnabled = "isLocationServiceEnabled";
  static const getAccuracyPermission = "getAccuracyPermission";
  static const openAppSettings = "openAppSettings";
  static const openLocationSettings = "openLocationSettings";
}

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('$_pluginPath/methods');

  @override
  Future<LocationPermission> checkPermission() async {
    try {
      final int permission =
          await methodChannel.invokeMethod(Methods.checkPermission);

      return permission.toLocationPermission();
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<bool> isLocationServiceEnabled() async {
    try {
      return (await methodChannel
          .invokeMethod(Methods.isLocationServiceEnabled));
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<LocationAccuracyPermission> getAccuracyPermission() async {
    try {
      final int permission =
          await methodChannel.invokeMethod(Methods.getAccuracyPermission);
      return permission.toLocationAccuracyPermission();
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<LocationPermission> requestPermission() async {
    try {
      final int permission =
          await methodChannel.invokeMethod(Methods.requestPermission);
      return permission.toLocationPermission();
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<bool> openAppSettings() async {
    try {
      return await methodChannel.invokeMethod(Methods.openAppSettings);
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<bool> openLocationSettings() async {
    try {
      return await methodChannel.invokeMethod(Methods.openLocationSettings);
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  Exception _handlePlatformException(PlatformException exception) {
    switch (exception.code) {
      case 'PERMISSION_DEFINITIONS_NOT_FOUND':
        return PermissionDefinitionsNotFoundException(exception.message);
      case 'PERMISSION_DENIED':
        return PermissionDeniedException(exception.message);
      case "LOCATION_SERVICES_DISABLED":
        return LocationServiceDisabledException(exception.message);
      default:
        return exception;
    }
  }
}
