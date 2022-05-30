import '../errors/location_accuracy_permission_exception.dart';

/// Represent permission of location accuracy is approved
enum LocationAccuracyPermission {
  precise, // manifest android.permission.ACCESS_FINE_LOCATION
  approximate, // manifest android.permission.ACCESS_COARSE_LOCATION
  denied
}

extension LocationAccuracyPermissionXonInt on int {
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
}
