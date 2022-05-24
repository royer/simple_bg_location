import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'simple_bg_location_platform_interface.dart';

const _pluginPath = "com.royzed.simple_bg_location";

/// An implementation of [SimpleBgLocationPlatform] that uses method channels.
class MethodChannelSimpleBgLocation extends SimpleBgLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('$_pluginPath/methods');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
