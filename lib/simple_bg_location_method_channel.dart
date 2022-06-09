import 'dart:async';
import 'dart:developer' as dev;
import 'package:collection/collection.dart';
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
}

@visibleForTesting
class Events {
  static const position = "position";
}

class _Subscription {
  final StreamSubscription<dynamic> subscription;
  final Function callback;
  _Subscription(this.subscription, this.callback);
}

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(_methodChannelName);

  static const _eventChannelPosition =
      EventChannel('$_eventChannelPath/${Events.position}');

  static Stream<Position>? _positionStream;

  static void _registerSubscription(
      StreamSubscription<dynamic> sub, Function callback) {
    _subscriptions.add(_Subscription(sub, callback));
  }

  static List<_Subscription> _subscriptions = [];

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
  Stream<Position> getPositionStream([Function(PositionError)? failure]) {
    if (_positionStream != null) {
      return _positionStream!;
    }

    var originalStream = _eventChannelPosition
        .receiveBroadcastStream()
        .handleError((dynamic error) {
      if (failure != null) {
        failure(PositionError(error as PlatformException));
      } else {
        dev.log(
            'getPositionStream error!!! Uncaught position error: $error. You should provide a failure callback as 2nd argument.');
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
  void onPosition(Function(Position) success,
      [Function(PositionError)? failure]) {
    final stream = getPositionStream(failure);
    _registerSubscription(stream.listen(success), success);
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
