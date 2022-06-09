import 'simple_bg_location_platform_interface.dart';
import 'src/errors/errors.dart';
import 'src/models/models.dart';
import 'src/enums/enums.dart';

export 'src/errors/errors.dart';
export 'src/models/models.dart';
export 'src/enums/enums.dart';

class SimpleBgLocation {
  static Future<LocationPermission> checkPermission() {
    return SimpleBgLocationPlatform.instance.checkPermission();
  }

  static Future<LocationPermission> requestPermission() {
    return SimpleBgLocationPlatform.instance.requestPermission();
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

  static void onPosition(Function(Position) success,
      [Function(PositionError)? failure]) {
    return SimpleBgLocationPlatform.instance.onPosition(success, failure);
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
}
