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
        if (methodCall.method == Methods.checkPermission) {
          return LocationPermission.always.index;
        } else if (methodCall.method == Methods.requestPermission) {
          return LocationPermission.always.index;
        }
      });
    });

    tearDown(() {
      channel.setMockMethodCallHandler(null);
    });

    test('checkPermission', () async {
      expect(await platform.checkPermission(), LocationPermission.always);
    });

    test('requestPermission', () async {
      expect(await platform.requestPermission(), LocationPermission.always);
    });
  });

  group('exception test', () {
    setUp(() {
      channel.setMockMethodCallHandler((MethodCall methodCall) async {
        if (methodCall.method == Methods.checkPermission) return 25;
        if (methodCall.method == Methods.requestPermission) return 25;
      });
    });

    tearDown(() {
      channel.setMockMethodCallHandler(null);
    });

    test(
      'should throw InvalidLocationPermissionException on checkPermission',
      () async {
        expect(() async => await platform.checkPermission(),
            throwsA(const TypeMatcher<InvalidPermissionException>()));
      },
    );

    test(
      'should throw InvalidLocationPermissionException on requestPermission',
      () async {
        expect(() async => await platform.requestPermission(),
            throwsA(const TypeMatcher<InvalidPermissionException>()));
      },
    );
  });
}
