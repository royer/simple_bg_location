import 'package:flutter_test/flutter_test.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location/simple_bg_location_platform_interface.dart';
import 'package:simple_bg_location/simple_bg_location_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'package:simple_bg_location/src/enums/enums.dart';

final Position fakePosition = Position(
  uuid: '',
  latitude: 30.00,
  longitude: 30.00,
  timestamp: DateTime(2022, 7, 1),
);

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
    return Future.value(true);
  }

  @override
  Future<LocationAccuracyPermission> getAccuracyPermission() {
    return Future.value(LocationAccuracyPermission.precise);
  }

  @override
  Future<Position?> getLastKnownPosition({bool forceLocationManager = false}) {
    return Future.value(fakePosition);
  }

  @override
  Future<Position?> getCurrentPosition({forceLocationManager = false}) {
    return Future.value(fakePosition);
  }

  @override
  Stream<Position> getPositionStream([Function(PositionError)? handleError]) {
    return Stream<Position>.periodic(
        const Duration(milliseconds: 50),
        (x) => fakePosition.copyWith(
              uuid: x.toString(),
              latitude: 30.0 + x / 10,
              longitude: 30.0 + x / 10,
              timestamp: DateTime.now(),
            )).take(5);
  }

  @override
  Future<bool> requestPositionUpdate(RequestSettings requestSettings) {
    // TODO: implement requestPositionUpdate
    throw UnimplementedError();
  }

  @override
  Future<bool> stopPositionUpdate() {
    // TODO: implement stopPositionUpdate
    throw UnimplementedError();
  }

  @override
  Future<SBGLState> ready() {
    // TODO: implement ready
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
    MockSimpleBgLocationPlatform fakePlatform = MockSimpleBgLocationPlatform();
    SimpleBgLocationPlatform.instance = fakePlatform;

    expect(await SimpleBgLocation.checkPermission(), LocationPermission.always);
  });

  test('requestPermission', () async {
    SimpleBgLocation simpleBgLocationPlugin = SimpleBgLocation();
    MockSimpleBgLocationPlatform fakePlatform = MockSimpleBgLocationPlatform();
    SimpleBgLocationPlatform.instance = fakePlatform;

    expect(
        await SimpleBgLocation.requestPermission(), LocationPermission.always);
  });
}
