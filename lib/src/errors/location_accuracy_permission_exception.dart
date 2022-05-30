import '../enums/location_accuracy_permission.dart';

/// An exception thrown when trying to convert a unknown accuracy permission into
/// the [LocationAccuracyPermission] enum.
///
class InvalidAccuracyPermissionException implements Exception {
  /// Constructs the [InvalidAccuracyPermissionException]
  const InvalidAccuracyPermissionException(this.valueToConvert);

  /// The [valueToConvert] contains the value that was tried to be converted
  /// into a LocationPermission.
  final int valueToConvert;

  @override
  String toString() {
    // ignore: lines_longer_than_80_chars
    return 'Unable to convert the value "$valueToConvert" into a '
        'LocationPermission.\n'
        '(${LocationAccuracyPermission.precise.toString()}: ${LocationAccuracyPermission.precise.index}; '
        '${LocationAccuracyPermission.precise.toString()}: ${LocationAccuracyPermission.precise.index}; '
        '${LocationAccuracyPermission.denied.toString()}: ${LocationAccuracyPermission.denied.index}; '
        ')';
  }
}
