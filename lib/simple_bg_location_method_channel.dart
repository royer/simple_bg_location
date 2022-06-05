import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:simple_bg_location/src/extensions/extensions.dart';
import 'src/errors/errors.dart';
import 'src/enums/enums.dart';
import 'src/models/models.dart';
import 'simple_bg_location_platform_interface.dart';

const _pluginPath = "com.royzed.simple_bg_location";
const _methodChannelName = '$_pluginPath/methods';
const _eventChannelPath = "$_pluginPath/events";

class Methods {
  static const checkPermission = 'checkPermission';
  static const requestPermission = "requestPermission";
  static const isLocationServiceEnabled = "isLocationServiceEnabled";
  static const getAccuracyPermission = "getAccuracyPermission";
  static const openAppSettings = "openAppSettings";
  static const openLocationSettings = "openLocationSettings";
  static const getLastKnownPosition = "getLastKnownPosition";
  static const getCurrentPosition = "getCurrentPosition";
}

class Events {
  static const position = "position";
}

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(_methodChannelName);

  static const _eventChannelPosition =
      EventChannel('$_eventChannelPath/${Events.position}');

  Stream<Position>? _positionStream;

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
  Future<Position?> getLastKnownPosition(
      {bool forceLocationManager = false}) async {
    try {
      final params = <String, dynamic>{
        'forceLocationManager': forceLocationManager
      };

      final result = await methodChannel.invokeMethod(
          Methods.getLastKnownPosition, params);
      final positionMap =
          result != null ? Map<String, dynamic>.from(result) : null;
      return positionMap != null ? Position.fromMap(positionMap) : null;
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<Position?> getCurrentPosition(
      {bool forceLocationManager = false}) async {
    try {
      final params = <String, dynamic>{
        'forceLocationManager': forceLocationManager
      };

      final result = await methodChannel.invokeMapMethod<String, dynamic>(
          Methods.getCurrentPosition, params);

      return result != null ? Position.fromMap(result) : null;
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Stream<Position> getPositionStream(
    RequestSettings? requestSettings,
  ) {
    if (_positionStream != null) {
      return _positionStream!;
    }

    var originalStream =
        _eventChannelPosition.receiveBroadcastStream(requestSettings?.toMap());

    var positionStream = _wrapStream(originalStream);

    _positionStream = positionStream
        .map<Position>(
            (dynamic e) => Position.fromMap(e.cast<String, dynamic>()))
        .handleError((error) {
      if (error is PlatformException) {
        error = _handlePlatformException(error);
      }
      throw error;
    });

    return _positionStream!;
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

  Stream<dynamic> _wrapStream(Stream<dynamic> incoming) {
    return incoming.asBroadcastStream(onCancel: (sub) {
      sub.cancel();
      _positionStream = null;
    });
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
