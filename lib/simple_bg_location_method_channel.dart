import 'dart:async';
import 'dart:developer' as dev;
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

@visibleForTesting
class Methods {
  static const checkPermission = 'checkPermission';
  static const requestPermission = "requestPermission";
  static const isLocationServiceEnabled = "isLocationServiceEnabled";
  static const getAccuracyPermission = "getAccuracyPermission";
  static const openAppSettings = "openAppSettings";
  static const openLocationSettings = "openLocationSettings";
  static const getLastKnownPosition = "getLastKnownPosition";
  static const getCurrentPosition = "getCurrentPosition";
  static const requestPositionUpdate = 'requestPositionUpdate';
  static const stopPositionUpdate = "stopPositionUpdate";
  static const ready = 'ready';
  static const isPowerSaveMode = "isPowerSaveMode";
}

@visibleForTesting
class Events {
  static const position = "position";
  static const notificationAction = 'notificationAction';
}

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(_methodChannelName);

  static const _eventChannelPosition =
      EventChannel('$_eventChannelPath/${Events.position}');
  static const _eventChannelNotifyAction =
      EventChannel('$_eventChannelPath/${Events.notificationAction}');

  static Stream<Position>? _positionStream;
  static Stream<String>? _notifyActionStream;

  @override
  Future<LocationPermission> checkPermission({bool onlyCheckBackground = false}) async {
    try {
      final params = <String, dynamic>{
        'onlyCheckBackground': onlyCheckBackground
      };      
      final int permission =
          await methodChannel.invokeMethod(Methods.checkPermission, params);

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
  Future<LocationPermission> requestPermission(
      [BackgroundPermissionRationale? rationale]) async {
    try {
      final param = (rationale != null) ? rationale.toMap() : null;
      final int permission =
          await methodChannel.invokeMethod(Methods.requestPermission, param);
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
  Stream<Position> getPositionStream([Function(PositionError)? failure]) {
    if (_positionStream != null) {
      return _positionStream!;
    }

    var originalStream = _eventChannelPosition
        .receiveBroadcastStream()
        .handleError((dynamic error) {
      if (failure != null) {
        failure(
            PositionError.fromPlatformException(error as PlatformException));
      } else {
        dev.log(
            'getPositionStream error!!! Uncaught position error: $error. You should provide a failure callback.');
        throw error;
      }
    });

    var positionStream =
        originalStream.asBroadcastStream(onCancel: (controller) {
      controller.cancel();
      _positionStream = null;
    });

    _positionStream = positionStream.map<Position>(
        (dynamic e) => Position.fromMap(e.cast<String, dynamic>()));

    return _positionStream!;
  }

  @override
  Stream<String> getNotificationActionStream([Function(dynamic p1)? failure]) {
    if (_notifyActionStream != null) {
      return _notifyActionStream!;
    }

    var originalStream = _eventChannelNotifyAction
        .receiveBroadcastStream()
        .handleError((dynamic error) {
      if (failure != null) {
        failure(error);
      } else {
        dev.log(
            'NotificationActionStream has error!!!. Uncaught error: $error. You shouid provide a failure callback');
        throw error;
      }
    });

    _notifyActionStream =
        originalStream.asBroadcastStream(onCancel: (controller) {
      controller.cancel();
      _notifyActionStream = null;
    }).map<String>((dynamic e) {
      if (e is String) {
        return e;
      } else {
        return e.toString();
      }
    });

    return _notifyActionStream!;
  }

  @override
  Future<SBGLState> ready() async {
    try {
      var result = (await methodChannel
          .invokeMapMethod<String, dynamic>(Methods.ready))!;
      return SBGLState.fromMap(result);
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<bool> requestPositionUpdate(RequestSettings requestSettings) async {
    try {
      return await methodChannel.invokeMethod(
          Methods.requestPositionUpdate, requestSettings.toMap());
    } on PlatformException catch (e) {
      final error = _handlePlatformException(e);
      throw error;
    }
  }

  @override
  Future<bool> stopPositionUpdate() async {
    try {
      return await methodChannel.invokeMethod(Methods.stopPositionUpdate);
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

  @override
  Future<bool> isPowerSaveMode() async {
    return await methodChannel.invokeMethod(Methods.isPowerSaveMode);
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
