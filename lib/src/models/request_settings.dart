import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:simple_bg_location/simple_bg_location.dart';

/// For request position update settings
///
/// Represents the abstract [RequestSettings] class with which you can
/// configure platform specific settings.
class RequestSettings {
  /// The desired accuracy that should be used to determine the location data.
  ///
  /// The default value is [LocationAccuracy.best]
  LocationAccuracy accuracy;

  /// The minimum distance (measured in meters) a device must move
  /// horizontally before an update event is generated.
  ///
  /// Supply 0 when you want to be notified of all movements. The default is 0.
  int distanceFilter;

  // /// The [timeLimit] parameter allows you to specify a timeout interval (by
  // /// default no time limit is configured).
  // ///
  // /// Throws a [TimeoutException] when no location is received within the
  // /// supplied [timeLimit] duration.
  // final Duration? timeLimit;

  RequestSettings({
    this.accuracy = LocationAccuracy.medium,
    this.distanceFilter = 100,
  });

  Map<String, dynamic> toMap() {
    return {
      'accuracy': accuracy.index,
      'distanceFilter': distanceFilter,
    };
  }

  String toJson() => json.encode(toMap());


  @override
  String toString() =>
      'RequestSettings(accuracy: $accuracy, distanceFilter: $distanceFilter)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is RequestSettings &&
        other.accuracy == accuracy &&
        other.distanceFilter == distanceFilter;
  }

  @override
  int get hashCode => accuracy.hashCode ^ distanceFilter.hashCode;
}
