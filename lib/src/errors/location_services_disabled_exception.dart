/// An exception thrown when trying to access the  device's location
/// information while location service is disabled.
class LocationServiceDisabledException implements Exception {
  /// Constructs the [PermissionDeniedException]
  const LocationServiceDisabledException(this.message);

  /// A [message] describing more details on the denied permission.
  final String? message;

  @override
  String toString() {
    if (message == null || message!.isEmpty) {
      return 'System location service is disabled.';
    }
    return message!;
  }
}
