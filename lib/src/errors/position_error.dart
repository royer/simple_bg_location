import 'package:flutter/services.dart';

/// The error happened when getPositionStream
///
/// ## Error Codes
/// | Code                 | Description                 |
/// |----------------------|-----------------------------|
/// | 'PERMISSION_DENIED'  | Location permission denied  |
/// | 'TIME_OUT'           | timeout                     |
/// | 'CANCELLED'          | request cancelled           |
/// | 'OTHER_ERROR'        | other platform error        |
///
class PositionError {
  late final String code;
  late final String message;

  static const otherPlatformException = "OTHER_ERROR";
  static const permissionDenied = "PERMISSION_DENIED";
  static const timeOut = "TIME_OUT";
  static const requestCancelled = "CANCELLED";

  static const _errorCodes = [
    permissionDenied,
    timeOut,
    requestCancelled,
  ];

  PositionError(PlatformException e) {
    code = _errorCodes.firstWhere(
      (element) => e.code == element,
      orElse: () => otherPlatformException,
    );
    message = e.message ?? '';

    @override
    // ignore: unused_element
    String toString() {
      return 'Position Error: code: $code, message: $message';
    }
  }
}
