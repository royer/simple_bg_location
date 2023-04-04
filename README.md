# Flutter Simple Background Location Plugin

[![License](https://img.shields.io/github/license/royer/simple_bg_location?style=flat-square&logo=github)](https://github.com/royer/simple_bg_location/blob/master/LICENSE)

The Flutter simple_bg_location plugin provides a basic location API for Android and iOS, specially supporting the recording of location updates in the background on Android devices, even if the user exits the app using the system back button.

Simple_bg_location use FusedLocationProvider or if not available then the LocationManager on Android and CLLocationManager on iOS.

![demo](https://github.com/royer/simple_bg_location/blob/master/full_demo.gif)
## Features

* Records location updates in the background.
* Can check background location permission separately.
* Gets the last known location.
* Gets the current location.
* Checks if location services are enabled.
* Checks if Power Save mode is on or off on Android devices.
* Custom Notification icon and two custom action.(Android only)
* Calculates the distance (in meters) between two geo-locations.
  


## Getting Started

<details>
<summary>Android</summary>

**AndroidX**

The Simple Background Location Plugin requires the AndroidX. Make sure your Android project support AndroidX. Detailed instructions can be found [here](https://flutter.dev/docs/development/packages-and-plugins/androidx-compatibility).

1. Make sure your "android/gradle.properties" file has:
   
    >```
    >android.useAndroidX=true
    >android.enableJetifier=true
    >```

**SdkVersion**

The Simple Background Location Plugin requires the `minSdkVersion` >= 21 and `compileSdkVersion` >= 33.


2. Make sure your "android/app/build.gradle" file to 21:
   
   >```
   > android {
   >    compileSdkVersion 33
   >    ...
   >}
   >...
   >defaultConfig {
   >    ... 
   >    minSdkVersion 21
   >    ...
   >}


**Permissions**

If your App only need approximate accuracy, add `ACCESS_COARSE_LOCATION` in AndroidManifest.xml file (located under android/app/src/main) as children of the `<manifest>` tag.

>```xml
><uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
>```

If you need precise accuracy, add both `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`.

>```xml
><uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
><uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
>```

Simple Background Location Plugin use [foreground service type](https://developer.android.com/guide/topics/manifest/service-element#foregroundservicetype). This already meets most use cases and does not require requesting background permission. Even if the user exits the application using the system back button, the service of the Simple Background Location Plugin continues to record location information and saves it in memory. When the user restarts the application, all location records will be passed back to your application through the `ready()` function.

Since Android 10(API level 29), if you need background permission, you must declare the  `ACCESS_BACKGROUND_LOCATION` permission in manifest.

>```xml
><uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
>```

To reiterate, in the current version, there is **NO NEED** to apply for background permission. It will only be necessary to obtain background permission when future versions provide features such as Geofencing.

More details about [location permission.](https://developer.android.com/training/location/permissions) 

</details>
<details>
<summary>iOS</summary>

**Permissions**

Edit `info.plist` directly(located under ios/Runner)
```xml
<dict>
    ...
	<key>NSLocationWhenInUseUsageDescription</key>
	<string>Why need WhenInUse description</string>
	<key>NSLocationAlwaysUsageDescription</key>
	<string>Why need background description</string>
    ...
</dict>
```
</details>


## Usage
```dart
import 'package:simple_bg_location/simple_bg_location.dart';
```

The main steps to use this plugin are: 
1. Register the position update event listener(or subscribe a stream). 
2. Call ready() to initialize the plugin and receive the current state of the plugin.

⚠️ Do not execute *any* API method which will require accessing location services until the callback to **`ready()`** executes.(eg: `getCurrentPosition`, `requestPositionUpdate`...)

## Example

### Request Position update
```dart
  void _startPositionUpdate() async {
    if (!(await SimpleBgLocation.isLocationServiceEnabled())) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Location service disabled',
      ));
      setState(() {});
      SimpleBgLocation.openLocationSettings();
      return;
    }
    var permission = await SimpleBgLocation.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await SimpleBgLocation.requestPermission();
      if (permission == LocationPermission.denied) {
        events.add(const LocationEventItem(
          LocationEventType.log,
          'Permission denied',
        ));
        setState(() {});

        return;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Permission denied forever',
      ));
      setState(() {});
      // Do not call openAppSetting directly in the formal product.
      // Instead, you should ask the user if they are willing,
      // and do not ask again after the user has refused multiple times.
      SimpleBgLocation.openAppSettings();

      return;
    }

    if ((await SimpleBgDeviceInfo.isPowerSaveMode())) {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Power save mode enabled!',
        detail: '''Track recording may not work properly in Power Save Mode. 
            If track does not record properly, disable Power Save Mode.''',
      ));
      return;
    }

    final requestSettings = RequestSettings.good();
    requestSettings.notificationConfig = ForegroundNotificationConfig(
      notificationId: 100,
      title: "Simple BG Location",
      text: "distance: {distance}",
      priority: ForegroundNotificationConfig.NOTIFICATION_PRIORITY_DEFAULT,
      actions: ['Action1', 'Action2', 'cancel'],
    );

    final success =
        await SimpleBgLocation.requestPositionUpdate(requestSettings);
    if (success) {
      isTracking = true;
    } else {
      events.add(const LocationEventItem(
        LocationEventType.log,
        'Error',
        detail: 'Request position update failed',
      ));
    }
    setState(() {});
  }

```

[Full Example](https://github.com/royer/simple_bg_location/tree/master/full_example)

## Mainly Api quick view

### Prepare use plugin

* `onPosition`
  ```dart
  void onPosition(Function(Position) success, [Function(PositionError)? failure])
  ```
  Directly register a callback function to receive Location update events. Alternatively, if you prefer to use a stream, you can call `getPositionStream` to obtain a `Stream<Position>`, and manage the subscription yourself. 
  ```dart
  Stream<Position> getPositionStream([Function(PositionError)? handleError])
  ```

* `ready()`
  ```dart
  Future<SBGLState> ready()
  ```
  Before call other location service API such as `requestPositionUpdate`  must call ready() to get current plugin state.

  In android when user use back button quit app, flutter will shutdown all
  dart code. use ready() notify plugin and check whether the last position
  update is sill tracking, and get positions updates in plugin cache.

  ⚠️No position updated event will send if miss called ready()

* `onNotificationAction` (Android Only)
  ```dart
  void onNotificationAction(Function(String) success)
  ```
  For custom notification action events. or `getNotificationStream` to obtain a `Stream<String>`.
  ```dart
  Stream<String> getNotificationStream()
  ```
### Permission
* `checkPermission`
  ```dart
  Future<LocationPermission> checkPermission({bool onlyCheckBackground = false})
  ```
* `requestPermission`
  ```dart
  Future<LocationPermission> requestPermission([BackgroundPermissionRationale? rationale])
  ```
* `getAccuracyPermission`
  ```dart
  Future<LocationAccuracyPermission> getAccuracyPermission()
  ```

### Position

* `requestPositionUpdate`
  ```dart
  Future<bool> requestPositionUpdate(RequestSettings requestSettings)
  ```
* `stopPositionUpdate`
  ```dart
  Future<bool> stopPositionUpdate()
  ```
* `getLastKnowPosition`
  ```dart
  Future<Position?> getLastKnowPosition({bool forceLocationManager = false})
  ```
* `getCurrentPosition`
  ```dart
  Future<Position?> getCurrentPosition({bool forceLocationManager = false})
  ```
### Device information

* `isLocationServiceEnabled`
  ```dart
  Future<bool> isLocationServiceEnabled()
  ```
  Check location service is enable or not.

* `isPowerSaveMode`
  ```dart
  Future<bool> SimpleBgDeviceInfo.isPowerSaveMode()
  ```

### Utility

* `openAppSettings`
  ```dart
  Future<bool> openAppSettings()
  ```

* `openLocationSettings`
  ```dart
  Future<bool> openLocationSettings()
  ```

* `distance`
  ```dart
  double distance(double startLatitude, double startLongitude, double endLatitude, double endLongitude)
  ```

## Issues

Please file any issues, bugs or feature requests as an issue on our [GitHub](https://github.com/royer/simple_bg_location/issues) page.


