import 'simple_bg_location_platform_interface.dart';
import 'src/models/models.dart';
import 'src/enums/enums.dart';

export 'src/errors/errors.dart';
export 'src/models/models.dart';
export 'src/enums/enums.dart';

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

  Future<Position?> getLastKnowPosition({bool forceLocationManager = false}) {
    return SimpleBgLocationPlatform.instance
        .getLastKnownPosition(forceLocationManager: forceLocationManager);
  }

  Future<bool> openAppSettings() {
    return SimpleBgLocationPlatform.instance.openAppSettings();
  }

  Future<bool> openLocationSettings() {
    return SimpleBgLocationPlatform.instance.openLocationSettings();
  }
}
