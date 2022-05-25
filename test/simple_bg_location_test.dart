import 'package:flutter_test/flutter_test.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location/simple_bg_location_platform_interface.dart';
import 'package:simple_bg_location/simple_bg_location_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'package:simple_bg_location/src/enums/enums.dart';

class MockSimpleBgLocationPlatform
    with MockPlatformInterfaceMixin
    implements SimpleBgLocationPlatform {
  @override
  Future<LocationPermission> checkPermission() =>
      Future.value(LocationPermission.always);
}

void main() {
  final SimpleBgLocationPlatform initialPlatform =
      SimpleBgLocationPlatform.instance;

  test('$MethodChannelSimpleBgLocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSimpleBgLocation>());
  });

  test('checkPermission', () async {
    SimpleBgLocation simpleBgLocationPlugin = SimpleBgLocation();
    MockSimpleBgLocationPlatform fakePlatform = MockSimpleBgLocationPlatform();
    SimpleBgLocationPlatform.instance = fakePlatform;

    expect(await simpleBgLocationPlugin.checkPermission(),
        LocationPermission.always);
  });
}
