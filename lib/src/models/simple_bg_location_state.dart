import 'dart:io';

import 'package:simple_bg_location/simple_bg_location.dart';

class SBGLState {
  /// Whether the plugin is current stopped or started position update
  final bool isTracking;

  /// The requestSettings of this tracking. `null` if [isTracking] is false.
  ///
  /// if not null, in Android it is [AndroidRequestSettings], in iOS it is
  /// IOSRequestSettings
  final RequestSettings? requestSettings;

  /// All positions of this tracking.
  ///
  ///
  final List<Position>? positions;

  const SBGLState(
      {required this.isTracking, this.requestSettings, this.positions});

  factory SBGLState.fromMap(Map<String, dynamic> map) {
    return SBGLState(
      isTracking: map['isTracking'] ?? false,
      requestSettings: map['requestSettings'] != null
          ? RequestSettings.fromMap(map['requestSettings'])
          : null,
      positions: map['positions'] != null
          ? (map['positions'] as List).map((e) => Position.fromMap(e)).toList()
          : null,
    );
  }

  @override
  String toString() {
    return "SBGLState(isTracking: $isTracking, requestSettings: $requestSettings, positions number: ${positions?.length})";
  }
}
