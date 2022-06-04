import '../enums/enums.dart';
import '../errors/errors.dart';

extension AllEnumsXonInt on int {
  LocationAccuracyPermission toLocationAccuracyPermission() {
    if (this == LocationAccuracyPermission.precise.index) {
      return LocationAccuracyPermission.precise;
    } else if (this == LocationAccuracyPermission.approximate.index) {
      return LocationAccuracyPermission.approximate;
    } else if (this == LocationAccuracyPermission.denied.index) {
      return LocationAccuracyPermission.denied;
    } else {
      throw InvalidAccuracyPermissionException(this);
    }
  }

  LocationPermission toLocationPermission() {
    if (this == LocationPermission.denied.index) {
      return LocationPermission.denied;
    } else if (this == LocationPermission.deniedForever.index) {
      return LocationPermission.deniedForever;
    } else if (this == LocationPermission.whileInUse.index) {
      return LocationPermission.whileInUse;
    } else if (this == LocationPermission.always.index) {
      return LocationPermission.always;
    } else {
      throw InvalidPermissionException(this);
    }
  }

  LocationAccuracy toLocationAccuracy() {
    if (this == LocationAccuracy.lowest.index) {
      return LocationAccuracy.lowest;
    } else if (this == LocationAccuracy.low.index) {
      return LocationAccuracy.low;
    } else if (this == LocationAccuracy.medium.index) {
      return LocationAccuracy.medium;
    } else if (this == LocationAccuracy.high.index) {
      return LocationAccuracy.high;
    } else if (this == LocationAccuracy.best.index) {
      return LocationAccuracy.best;
    } else if (this == LocationAccuracy.bestForNavigation.index) {
      return LocationAccuracy.bestForNavigation;
    } else {
      throw InvalidLocationAccuracyException(this);
    }
  }
}
