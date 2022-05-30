import 'src/enums/enums.dart';

import 'simple_bg_location_platform_interface.dart';

export 'src/errors/errors.dart';

class SimpleBgLocation {
  Future<LocationPermission> checkPermission() {
    return SimpleBgLocationPlatform.instance.checkPermission();
  }

  Future<LocationPermission> requestPermission() {
    return SimpleBgLocationPlatform.instance.requestPermission();
  }

  Future<bool> isLocationServiceEnabled() {
    return SimpleBgLocationPlatform.instance.isLocationServiceEnabled();
  }

  Future<LocationAccuracyPermission> getAccuracyPermission() {
    return SimpleBgLocationPlatform.instance.getAccuracyPermission();
  }

  Future<bool> openAppSettings() {
    return SimpleBgLocationPlatform.instance.openAppSettings();
  }

  Future<bool> openLocationSettings() {
    return SimpleBgLocationPlatform.instance.openLocationSettings();
  }
}
