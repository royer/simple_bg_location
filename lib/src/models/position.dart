import 'dart:convert';

import 'package:flutter/foundation.dart';

/// Contains detailed location information.
@immutable
class Position {
  /// Constructs an instance with the given values for testing. [Position]
  /// instances constructed this way won't actually reflect any real information
  /// from the platform, just whatever was passed in at construction time.
  const Position({
    required this.uuid,
    required this.latitude,
    required this.longitude,
    required this.timestamp,
    this.altitude = -1.0,
    this.altitudeAccuracy = -1.0,
    this.accuracy = -1.0,
    this.heading = -1.0,
    this.headingAccuracy = -1.0,
    this.floor,
    this.speed = -1.0,
    this.speedAccuracy = -1.0,
    this.isMocked = false,
  });

  /// Universally Unique Identifier.
  ///
  /// This uuid is internal database of plugin. some time is helpful for debug.
  //
  final String uuid;

  /// The latitude of this position in degrees normalized to the interval -90.0
  /// to +90.0 (both inclusive).
  final double latitude;

  /// The longitude of the position in degrees normalized to the interval -180
  /// (exclusive) to +180 (inclusive).
  final double longitude;

  /// The time at which this position was determined.
  ///
  /// Timestamp in __`ISO 8601` (UTC) format. Eg: `2018-01-01T12:00:01.123Z'.
  ///
  final DateTime? timestamp;

  /// The altitude of the device in meters.
  ///
  /// The altitude is not available on all devices. In these cases the returned
  /// value is `-1`.
  final double altitude;

  /// Altitude accuracy in meters.
  ///
  /// If this location does not have a `altitudeAccuracy`, then `-1` is returned.
  ///
  /// ## iOS
  ///
  /// When this property contains 0 or a positive number, the value in the
  /// altitude property is plus or minus the specified number of meters. When
  /// this property contains a negative number, the value in the altitude
  /// property is invalid.
  ///
  /// Determining the [altitudeAccuracy] requires a device with GPS capabilities.
  /// Thus, on some devices, this property always contains a negative value.
  ///
  /// ## Android
  ///
  /// We define vertical accuracy at 68% confidence. Specifically, as 1-side of
  /// the 2-sided range above and below the estimated altitude reported by
  /// [altitude], within which there is a 68% probability of finding the true
  /// altitude.
  ///
  /// In the case where the underlying distribution is assumed Gaussian normal,
  /// this would be considered 1 standard deviation.
  ///
  /// For example, if [altitude] returns `150`, and [altitudeAccuracy] returns
  ///  `20` then there is a 68% probability of the true altitude being between
  ///  `130` and `170` meters.
  ///
  final double altitudeAccuracy;

  /// The estimated horizontal accuracy of the position in meters.
  ///
  /// The accuracy is not available on all devices. In these cases the value is
  /// `-1`.
  final double accuracy;

  /// The heading in which the device is traveling in degrees.
  ///
  /// The heading is not available on all devices. In these cases the value is
  /// `-1`
  final double heading;

  /// Heading accuracy in degrees.
  ///
  /// ⚠️ Note:  Only present when location came from GPS.  `-1` otherwise.
  final double headingAccuracy;

  /// The floor specifies the floor of the building on which the device is
  /// located.
  ///
  /// __`[iOS Only]`__ The floor property is only available on iOS and only
  /// works in an environment containing indoor-tracking hardware
  /// (eg: bluetooth-beacons). In all other cases this value will be null.
  ///
  final int? floor;

  /// The speed at which the devices is traveling in meters per second over
  /// ground.
  ///
  /// The speed is not available on all devices. In these cases the value is
  /// `-1`.
  final double speed;

  /// The estimated speed accuracy of this position, in meters per second.
  ///
  /// The speedAccuracy is not available on all devices. In these cases the
  /// value is `-1`.
  final double speedAccuracy;

  /// Will be true on Android (starting from API lvl 18) when the location came
  /// from the mocked provider.
  ///
  /// On iOS this value will always be false.
  final bool isMocked;

  Position copyWith({
    String? uuid,
    double? latitude,
    double? longitude,
    DateTime? timestamp,
    double? altitude,
    double? altitudeAccuracy,
    double? accuracy,
    double? heading,
    double? headingAccuracy,
    int? floor,
    double? speed,
    double? speedAccuracy,
    bool? isMocked,
  }) {
    return Position(
      uuid: uuid ?? this.uuid,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      timestamp: timestamp ?? this.timestamp,
      altitude: altitude ?? this.altitude,
      altitudeAccuracy: altitudeAccuracy ?? this.altitudeAccuracy,
      accuracy: accuracy ?? this.accuracy,
      heading: heading ?? this.heading,
      headingAccuracy: headingAccuracy ?? this.headingAccuracy,
      floor: floor ?? this.floor,
      speed: speed ?? this.speed,
      speedAccuracy: speedAccuracy ?? this.speedAccuracy,
      isMocked: isMocked ?? this.isMocked,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'uuid': uuid,
      'latitude': latitude,
      'longitude': longitude,
      'timestamp': timestamp?.millisecondsSinceEpoch,
      'altitude': altitude,
      'altitudeAccuracy': altitudeAccuracy,
      'accuracy': accuracy,
      'heading': heading,
      'headingAccuracy': headingAccuracy,
      'floor': floor,
      'speed': speed,
      'speedAccuracy': speedAccuracy,
      'isMocked': isMocked,
    };
  }

  factory Position.fromMap(Map<String, dynamic> map) {
    return Position(
      uuid: map['uuid'] ?? '',
      latitude: map['latitude']?.toDouble() ?? 0.0,
      longitude: map['longitude']?.toDouble() ?? 0.0,
      timestamp: map['timestamp'] != null
          ? DateTime.fromMillisecondsSinceEpoch(map['timestamp'])
          : null,
      altitude: map['altitude']?.toDouble() ?? -1.0,
      altitudeAccuracy: map['altitudeAccuracy']?.toDouble() ?? -1.0,
      accuracy: map['accuracy']?.toDouble() ?? -1.0,
      heading: map['heading']?.toDouble() ?? -1.0,
      headingAccuracy: map['headingAccuracy']?.toDouble() ?? -1.0,
      floor: map['floor']?.toInt(),
      speed: map['speed']?.toDouble() ?? -1.0,
      speedAccuracy: map['speedAccuracy']?.toDouble() ?? -1.0,
      isMocked: map['isMocked'] ?? false,
    );
  }

  String toJson() => json.encode(toMap());

  factory Position.fromJson(String source) =>
      Position.fromMap(json.decode(source));

  @override
  String toString() {
    return 'Position(uuid: $uuid, latitude: $latitude, longitude: $longitude, timestamp: $timestamp, altitude: $altitude, altitudeAccuracy: $altitudeAccuracy, accuracy: $accuracy, heading: $heading, headingAccuracy: $headingAccuracy, floor: $floor, speed: $speed, speedAccuracy: $speedAccuracy, isMocked: $isMocked)';
  }

  String toStringShortVersion() {
    return 'Position(lat: $latitude, lng: $longitude, altitude: $altitude, speed: $speed, timestamp: ${timestamp?.toIso8601String()} uuid: $uuid)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is Position &&
        other.uuid == uuid &&
        other.latitude == latitude &&
        other.longitude == longitude &&
        other.timestamp == timestamp &&
        other.altitude == altitude &&
        other.altitudeAccuracy == altitudeAccuracy &&
        other.accuracy == accuracy &&
        other.heading == heading &&
        other.headingAccuracy == headingAccuracy &&
        other.floor == floor &&
        other.speed == speed &&
        other.speedAccuracy == speedAccuracy &&
        other.isMocked == isMocked;
  }

  @override
  int get hashCode {
    return uuid.hashCode ^
        latitude.hashCode ^
        longitude.hashCode ^
        timestamp.hashCode ^
        altitude.hashCode ^
        altitudeAccuracy.hashCode ^
        accuracy.hashCode ^
        heading.hashCode ^
        headingAccuracy.hashCode ^
        floor.hashCode ^
        speed.hashCode ^
        speedAccuracy.hashCode ^
        isMocked.hashCode;
  }
}
