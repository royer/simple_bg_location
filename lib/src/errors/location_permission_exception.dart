import '../enums/location_permission.dart';

/// An exception thrown when trying to convert a unknown permission into
/// the LocationPermission enum.
///
class InvalidPermissionException implements Exception {
  /// Constructs the [InvalidPermissionException]
  const InvalidPermissionException(this.valueToConvert);

  /// The [valueToConvert] contains the value that was tried to be converted
  /// into a LocationPermission.
  final int valueToConvert;

  @override
  String toString() {
    var dumps = <String>[];
    for (final v in LocationPermission.values) {
      dumps.add('${v.name}: ${v.index}');
    }

    return 'Unable to convert the value "$valueToConvert" into a '
        'LocationPermission.\n'
        "(${dumps.join(', ')})";
  }
}
