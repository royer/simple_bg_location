import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:simple_bg_location/src/errors/location_permission_exception.dart';
import 'src/enums/enums.dart';

import 'simple_bg_location_platform_interface.dart';

const _pluginPath = "com.royzed.simple_bg_location";

class Methods {
  static const getPlatformVersion = 'getPlatformVersion';
  static const checkPermission = 'checkPermission';
}

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('$_pluginPath/methods');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>(Methods.getPlatformVersion);
    return version;
  }

  @override
  Future<LocationPermission> checkPermission() async {
    try {
      final int permission =
          await methodChannel.invokeMethod(Methods.checkPermission);

      return permission.toLocationPermission();
    } on PlatformException catch (e) {
      final error = e;
      throw error;
    }
  }
}
