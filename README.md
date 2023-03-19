# Flutter Simple Background Location Plugin

The Flutter simple_bg_location plugin provides a basic location API for Android and iOS, specially supporting the recording of location updates in the background on Android devices, even if the user exits the app using the system back button.

Simple_bg_location use FusedLocationProvider or if not available then the LocationManager on Android and CLLocationManager on iOS.
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
  void _requestPositionUpdate(BuildContext context,
      {bool forBackground = false}) async {
    final accuracy = context.read<SettingsCubit>().state.accuracy;
    final forceLocationManager =
        context.read<SettingsCubit>().state.forceLocationManager;
    final locationCubit = context.read<LocationCubit>();

    // use RequestUiFlowCubit just for avoid dart compile lint warning for
    // DON'T use BuildContext across asynchronous gaps.
    // https://dart-lang.github.io/linter/lints/use_build_context_synchronously.html
    final uiFlowCubit = context.read<RequestUiFlowCubit>();

    final permission = await SimpleBgLocation.checkPermission(
        onlyCheckBackground: forBackground);

    if (permission == LocationPermission.denied) {
      if (forBackground) {
        final shouldShowRationale =
            await SimpleBgLocation.shouldShowRequestPermissionRationale();
        if (shouldShowRationale) {
          uiFlowCubit.startShowBackgroundRationale(
              forBackground: forBackground);
          return;
        }
      }
      uiFlowCubit.startRequestPermission(forBackground: forBackground);
      return;
    } else if (permission == LocationPermission.deniedForever) {
      // todo: show dialog to open app setting
      return;
    } else if (permission == LocationPermission.whileInUse && forBackground) {
      // we need background permission, but we have only whileInUse permission
      // call requestPermission again.
      uiFlowCubit.startRequestPermission(forBackground: forBackground);
      return;
    } else {
      if ((await SimpleBgDeviceInfo.isPowerSaveMode())) {
        uiFlowCubit.startShowPowerSavedModeWarning(
            forBackground: forBackground);
        return;
      } else {
        _callRequestPositionUpdate(
            accuracy, forceLocationManager, locationCubit, uiFlowCubit);
      }
    }
  }

```

Full Example()

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter development, view the
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

