import '../enums/location_accuracy.dart';

/// An exception thrown when trying to convert a unknown accuracy into
/// the [LocationAccuracy] enum.
///
class InvalidLocationAccuracyException implements Exception {
  const InvalidLocationAccuracyException(this.valueToConvert);

  /// The [valueToConvert] contains the value that was tried to be converted
  /// into a LocationAccuracy.
  final int valueToConvert;

  @override
  String toString() {
    var dumps = <String>[];
    for (final v in LocationAccuracy.values) {
      dumps.add('${v.name}: ${v.index}');
    }

    return 'Unable to convert the value "$valueToConvert" into a '
        "LocationAccuracy.\n"
        "(${dumps.join(', ')})";
  }
}
