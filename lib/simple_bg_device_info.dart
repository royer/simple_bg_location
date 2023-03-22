import 'package:simple_bg_location/simple_bg_location_platform_interface.dart';

/// Get Device information
class SimpleBgDeviceInfo {
  /// see [SimpleBgLocationPlatform.isPowerSaveMode]
  static Future<bool> isPowerSaveMode() {
    return SimpleBgLocationPlatform.instance.isPowerSaveMode();
  }
}
