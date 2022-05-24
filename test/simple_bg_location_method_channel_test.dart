import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:simple_bg_location/simple_bg_location_method_channel.dart';
import 'package:simple_bg_location/src/enums/enums.dart';
import 'package:simple_bg_location/src/errors/errors.dart';

void main() {
  MethodChannelSimpleBgLocation platform = MethodChannelSimpleBgLocation();
  const MethodChannel channel =
      MethodChannel('com.royzed.simple_bg_location/methods');

  TestWidgetsFlutterBinding.ensureInitialized();

  group('normal return test group', () {
    setUp(() {
      channel.setMockMethodCallHandler((MethodCall methodCall) async {
        if (methodCall.method == Methods.getPlatformVersion) return '42';
        if (methodCall.method == Methods.checkPermission) {
          return LocationPermission.always.index;
        }
      });
    });

    tearDown(() {
      channel.setMockMethodCallHandler(null);
    });

    test('getPlatformVersion', () async {
      expect(await platform.getPlatformVersion(), '42');
    });

    test('checkPermission', () async {
      expect(await platform.checkPermission(), LocationPermission.always);
    });
  });

  group('exception test', () {
    setUp(() {
      channel.setMockMethodCallHandler((MethodCall methodCall) async {
        if (methodCall.method == Methods.getPlatformVersion) return '42';
        if (methodCall.method == Methods.checkPermission) return 25;
      });
    });

    tearDown(() {
      channel.setMockMethodCallHandler(null);
    });

    test(
      'should throw InvalidLocationPermissionException',
      () async {
        expect(() async => await platform.checkPermission(),
            throwsA(const TypeMatcher<InvalidPermissionException>()));
      },
    );
  });
}
