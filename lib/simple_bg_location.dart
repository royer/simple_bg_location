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
}
