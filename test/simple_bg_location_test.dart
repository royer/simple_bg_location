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

  @override
  Future<LocationPermission> requestPermission() =>
      Future.value(LocationPermission.always);

  @override
  Future<bool> openAppSettings() {
    return Future.value(true);
  }

  @override
  Future<bool> openLocationSettings() {
    return Future.value(true);
  }

  @override
  Future<bool> isLocationServiceEnabled() {
    // TODO: implement isLocationServiceEnable
    throw UnimplementedError();
  }
  
  @override
  Future<LocationAccuracyPermission> getAccuracyPermission() {
    // TODO: implement getAccuracyPermission
    throw UnimplementedError();
  }
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

  test('requestPermission', () async {
    SimpleBgLocation simpleBgLocationPlugin = SimpleBgLocation();
    MockSimpleBgLocationPlatform fakePlatform = MockSimpleBgLocationPlatform();
    SimpleBgLocationPlatform.instance = fakePlatform;

    expect(await simpleBgLocationPlugin.requestPermission(),
        LocationPermission.always);
  });
}
