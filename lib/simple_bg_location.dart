import 'src/enums/enums.dart';

import 'simple_bg_location_platform_interface.dart';

class SimpleBgLocation {
  Future<String?> getPlatformVersion() {
    return SimpleBgLocationPlatform.instance.getPlatformVersion();
  }

  Future<LocationPermission> checkPermission() {
    return SimpleBgLocationPlatform.instance.checkPermission();
  }
}
