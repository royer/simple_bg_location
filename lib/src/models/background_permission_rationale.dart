/// __Android Only__ Explain why you need background location permission
class BackgroundPermissionRationale {
  /// the explain
  final String title;

  /// more detail explain
  final String message;

  const BackgroundPermissionRationale({
    required this.title,
    required this.message,
  });

  Map<String, dynamic> toMap() {
    return {
      'title': title,
      'message': message,
    };
  }
}
