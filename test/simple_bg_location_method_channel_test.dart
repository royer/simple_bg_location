import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:simple_bg_location/simple_bg_location_method_channel.dart';

void main() {
  MethodChannelSimpleBgLocation platform = MethodChannelSimpleBgLocation();
  const MethodChannel channel = MethodChannel('simple_bg_location');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
