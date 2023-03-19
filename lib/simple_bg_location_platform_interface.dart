import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'simple_bg_location_method_channel.dart';
import 'src/enums/enums.dart';
import 'src/errors/errors.dart';
import 'src/models/models.dart';

abstract class SimpleBgLocationPlatform extends PlatformInterface {
  /// Constructs a SimpleBgLocationPlatform.
  SimpleBgLocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static SimpleBgLocationPlatform _instance = MethodChannelSimpleBgLocation();

  /// The default instance of [SimpleBgLocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelSimpleBgLocation].
  static SimpleBgLocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SimpleBgLocationPlatform] when
  /// they register themselves.
  static set instance(SimpleBgLocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Returns a [Future]<`LocationPermission`> indicating if the user allows the
  /// App to access the device's location
  ///
  /// If [onlyCheckBackground] is true, only check background location permission.
  /// even if the App already has the 'whileInUse' permission, it still will return
  /// 'denied' or 'deniedForever' if the App doesn't have the 'always' permission.
  ///
  /// Throws a [PermissionDefinitionsNotFoundException] when there is no permission
  /// description in the AndroidManifest.xml on Android or the Info.plist on iOS.
  Future<LocationPermission> checkPermission(
      {bool onlyCheckBackground = false}) {
    throw UnimplementedError("checkPermission() has not been implemented.");
  }

  /// Request permission to access the location of the device.
  ///
  /// Returns a [Future]<`LocationPermission`> which when completes indicates
  /// if the user granted permission to access the device's location in this
  /// request.
  ///
  /// If [rationale] is not null, it will be shown a default rationale dialog
  /// when request background permission denied and [shouldShowRequestPermissionRationale]
  /// return true.(Only for Android 11 or higher)
  ///
  /// Throws a [PermissionDefinitionsNotFoundException] when the required
  /// platform specific configuration is missing (e.g. in the
  /// AndroidManifest.xml on Android or the Info.plist on iOS).
  ///
  /// A [PermissionRequestInProgressException] is thrown if permissions are
  /// requested while an earlier request has not yet been completed.
  ///
  /// ⚠️ Note: The return value is the same as [checkPermission] return value
  /// in most of the time. but there is some exceptions.
  /// [requestPermission] return value is indicates this time request get
  /// what kind permission. [checkPermission] is the permission already have.
  /// for example:
  /// Now already has 'whileInUse' permission, call requestPermission
  /// to upgrade to 'always' permission, but user do not approve upgrade to
  /// 'always', then [requestPermission] while return 'denied' or 'deniedForever'
  /// indicates this request failed.
  Future<LocationPermission> requestPermission(
      [BackgroundPermissionRationale? rationale]) {
    throw UnimplementedError('requestPermission() has not been implemented.');
  }

  /// Should show request permission rationale for background location. Only for Android.
  /// iOS will always return false.
  ///
  /// On Android 11 or higher, If your App hasn't been granted background location, and
  /// shouldShowRequestPermissionRationale returns false, you should show an educational
  /// UI to the user explaining why your App needs background location access.
  ///
  /// Note: The Plugin provide a default rationale UI which is [BackgroundPermissionRationale]
  /// when call [requestPermission] with a not null rationale parameter. If you show your own
  /// rationale UI, don't set this parameter
  Future<bool> shouldShowRequestPermissionRationale() {
    throw UnimplementedError(
        'shouldShowRequestPermissionRationale() has not been implemented.');
  }

  /// Check device location service is enabled
  Future<bool> isLocationServiceEnabled() {
    throw UnimplementedError(
        'isLocationServiceEnable() has not been implemented.');
  }

  /// Check which accuracy permission that user approved.
  Future<LocationAccuracyPermission> getAccuracyPermission() {
    throw UnimplementedError(
        'getAccuracyPermission() has not been implemented.');
  }

  /// Get the best most recent location currently available.
  ///
  /// If a location is not available, which should happen very rarely, `null` will
  /// be return. The best accuracy available while respecting the location
  /// permissions will be returned.
  ///
  ///- Throws a [PermissionDeniedException] when user has not approved access.
  ///- Throws a [LocationServiceDisabledException] when the user allowed access,
  /// but the location services of the device are disabled.
  Future<Position?> getLastKnownPosition({bool forceLocationManager = false}) {
    throw UnimplementedError(
        'getLastKnownPosition() has not been implemented.');
  }

  /// Get a single current location fix on the device.
  ///
  /// Unlike [getLastKnownPosition] that returns a cached location, this method
  /// could cause active location computation on the device. If the device
  /// location can be determined within reasonable time(tens of seconds),
  /// otherwise `null` will be return
  ///
  ///- Throws a [PermissionDeniedException] when user has not approved access.
  ///- Throws a [LocationServiceDisabledException] when the user allowed access,
  /// but the location services of the device are disabled.
  Future<Position?> getCurrentPosition({bool forceLocationManager = false}) {
    throw UnimplementedError('getCurrentPosition() has not been implemented.');
  }

  /// The position update stream.
  ///
  /// Fires whenever the location changed inside the bounds of the
  /// [RequestSettings.accuracy]
  ///
  Stream<Position> getPositionStream([Function(PositionError)? failure]) {
    throw UnimplementedError('getPositionStream() has not been implemented.');
  }

  /// __Android Only__ The custom notification action button click event stream.
  Stream<String> getNotificationActionStream() {
    throw UnimplementedError(
        'getNotificationActionStream() has not been implemented.');
  }

  /// Initialize plugin for prepare requestPositionUpdate
  ///
  /// Before call requestPositionUpdate must call ready() to get current plugin
  /// state.
  /// In android when user use back button quit app, flutter will shutdown all
  /// dart code. use ready() notify plugin and check whether the last position
  /// update is sill tracking, and get positions updates in plugin cache.
  ///
  /// No position updated event will send if miss called ready()
  Future<SBGLState> ready() {
    throw UnimplementedError('getPositionStream() has not been implemented.');
  }

  /// Request location change listen.
  ///
  /// All position updates will send to [getPositionStream] stream.
  ///
  /// The [requestSettings] parameter is used to configure the location update
  /// behavior and customer notification on Android. more detail see [RequestSettings]
  Future<bool> requestPositionUpdate(RequestSettings requestSettings) {
    throw UnimplementedError(
        'requestPositionUpdate() has not been implemented');
  }

  /// Stop listen location change.
  Future<bool> stopPositionUpdate() {
    throw UnimplementedError('stopPositionUpdate() has not been implemented');
  }

  /// Open the App settings page
  ///
  /// Returns [true] if app settings page could be opened, otherwise [false]
  /// is returned
  Future<bool> openAppSettings() async {
    throw UnimplementedError('openAppSettings() has not been implemented.');
  }

  /// Opens the location settings page.
  ///
  /// Returns [true] if the location settings page could be opened, otherwise
  /// [false] is returned.
  Future<bool> openLocationSettings() async {
    throw UnimplementedError(
        'openLocationSettings() has not been implemented.');
  }

  /// Check device Power Save mode
  ///
  /// Return `true` Power Save Mode is on.
  ///
  /// Note: on Android: It only available Android 5.0 (API level 21), in early
  /// version always return false.
  Future<bool> isPowerSaveMode() async {
    throw UnimplementedError('isPowerSaveMode() has not been implemented.');
  }
}
