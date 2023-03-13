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
  static const locationServiceDisabled = "LOCATION_SERVICES_DISABLED";
  static const timeOut = "TIME_OUT";
  static const canceled = "CANCELED";

  static const _errorCodes = [
    permissionDenied,
    locationServiceDisabled,
    timeOut,
    canceled,
  ];

  PositionError(this.code, this.message);

  PositionError.fromPlatformException(PlatformException e) {
    code = _errorCodes.firstWhere(
      (element) => e.code == element,
      orElse: () => otherPlatformException,
    );
    message = e.message ?? '';
  }

  @override
  String toString() {
    return 'Position Error: code: $code, message: $message';
  }
}
