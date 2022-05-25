import 'src/enums/enums.dart';

import 'simple_bg_location_platform_interface.dart';

class SimpleBgLocation {
  Future<LocationPermission> checkPermission() {
    return SimpleBgLocationPlatform.instance.checkPermission();
  }
}
