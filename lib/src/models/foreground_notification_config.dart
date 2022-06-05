// ignore_for_file: constant_identifier_names

import 'dart:convert';

import 'package:flutter/foundation.dart';

import 'package:simple_bg_location/src/models/android_resource.dart';

/// __(Android only)__ The Android operating system requires a persistent
/// notification when running a foreground service.
///
class ForegroundNotificationConfig {
  /// Specifies the name of your custom Android Layout XML file.
  ///
  /// ℹ️ See [Android Custom Notification Layout](https://github.com/transistorsoft/flutter_background_geolocation/wiki/Android-Custom-Notification-Layout)
  /// for setup instructions.
  String? layout;

  /// The title of notification
  ///
  ///Defaults to the application name from `AndroidManifest`
  String? title;

  /// The content of notification
  ///
  /// Defaults to *"Location service activated"*.
  String? text;

  /// the notification small icon. if null plugin will use app launcher icon.
  AndroidResource? smallIcon;

  /// The large icon in notification body.
  ///
  ///
  AndroidResource? largeIcon;

  /// The priority of notification
  ///
  /// The following `priority` values defined as static constants:
  ///
  /// | Value                                                   | Description                                                                                             |
  /// |---------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
  /// | [NOTIFICATION_PRIORITY_DEFAULT] | Notification weighted to top of list; notification-bar icon weighted left                               |
  /// | [NOTIFICATION_PRIORITY_HIGH]    | Notification **strongly** weighted to top of list; notification-bar icon **strongly** weighted to left  |
  /// | [NOTIFICATION_PRIORITY_LOW]     | Notification weighted to bottom of list; notification-bar icon weighted right                           |
  /// | [NOTIFICATION_PRIORITY_MAX]     | Same as `NOTIFICATION_PRIORITY_HIGH`                                                                    |
  /// | [NOTIFICATION_PRIORITY_MIN]     | Notification **strongly** weighted to bottom of list; notification-bar icon **hidden**                  |
  ///
  int priority;

  /// the Channel name of notification
  ///
  /// On Android O+, oreground-service needs to create a "Notification Channel".
  /// The name of this channel can be seen in:
  /// > `Settings->App & Notifications->Your App.`
  ///
  /// Defaults to your application's name from `AndroidManifest`.
  String? channelName;

  /// Your custom action button on notification
  ///
  /// Declare click listeners for `<Button />` elements of a custom notification [layout].
  ///
  /// ℹ️ See [Android Custom Notification Layout](https://github.com/transistorsoft/flutter_background_geolocation/wiki/Android-Custom-Notification-Layout) for setup instructions.
  ///
  /// You can declare your own custom `<Button />` elements and register click-listeners upon them using the [actions] parameter:
  ///
  /// ```xml
  /// <Button
  ///     android:id="@+id/notificationButtonPause" // <-- notificationButtonPause
  ///     style="@style/Widget.AppCompat.Button.Small"
  ///     android:layout_width="60dp"
  ///     android:layout_height="40dp"
  ///     android:text="Foo" />
  /// ```

  List<String>? actions;

  /// When enabled, a WifiLock is acquired when background execution is started.
  /// This allows the application to keep the Wi-Fi radio awake, even when the
  /// user has not used the device in a while (e.g. for background network
  /// communications).
  ///
  /// Wifi lock permissions should be obtained first by using a permissions
  /// library.
  bool enableWifiLock;

  /// When enabled, a Wakelock is acquired when background execution is
  /// started.
  ///
  /// If this is false then the system can still sleep and all location
  /// events will be received at once when the system wakes up again.
  ///
  /// Wake lock permissions should be obtained first by using a permissions
  /// library.
  bool enableWakeLock;

  ForegroundNotificationConfig({
    this.layout,
    this.title,
    this.text,
    this.smallIcon,
    this.largeIcon,
    this.priority = 0,
    this.channelName,
    this.actions,
    this.enableWifiLock = false,
    this.enableWakeLock = false,
  });

  static const int NOTIFICATION_PRIORITY_DEFAULT = 0;
  static const int NOTIFICATION_PRIORITY_HIGH = 1;
  static const int NOTIFICATION_PRIORITY_LOW = -1;
  static const int NOTIFICATION_PRIORITY_MAX = 2;
  static const int NOTIFICATION_PRIORITY_MIN = -2;

  Map<String, dynamic> toMap() {
    return {
      'layout': layout,
      'title': title,
      'text': text,
      'smallIcon': smallIcon?.toMap(),
      'largeIcon': largeIcon?.toMap(),
      'priority': priority,
      'channelName': channelName,
      'actions': actions,
      'enableWifiLock': enableWifiLock,
      'enableWakeLock': enableWakeLock,
    };
  }

  factory ForegroundNotificationConfig.fromMap(Map<String, dynamic> map) {
    return ForegroundNotificationConfig(
      layout: map['layout'],
      title: map['title'],
      text: map['text'],
      smallIcon: map['smallIcon'] != null
          ? AndroidResource.fromMap(map['smallIcon'])
          : null,
      largeIcon: map['largeIcon'] != null
          ? AndroidResource.fromMap(map['largeIcon'])
          : null,
      priority: map['priority']?.toInt() ?? 0,
      channelName: map['channelName'],
      actions: List<String>.from(map['actions']),
      enableWifiLock: map['enableWifiLock'] ?? false,
      enableWakeLock: map['enableWakeLock'] ?? false,
    );
  }

  String toJson() => json.encode(toMap());

  factory ForegroundNotificationConfig.fromJson(String source) =>
      ForegroundNotificationConfig.fromMap(json.decode(source));

  @override
  String toString() {
    return 'ForegroundNotificationConfig(layout: $layout, title: $title, text: $text, smallIcon: $smallIcon, largeIcon: $largeIcon, priority: $priority, channelName: $channelName, actions: $actions, enableWifiLock: $enableWifiLock, enableWakeLock: $enableWakeLock)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is ForegroundNotificationConfig &&
        other.layout == layout &&
        other.title == title &&
        other.text == text &&
        other.smallIcon == smallIcon &&
        other.largeIcon == largeIcon &&
        other.priority == priority &&
        other.channelName == channelName &&
        listEquals(other.actions, actions) &&
        other.enableWifiLock == enableWifiLock &&
        other.enableWakeLock == enableWakeLock;
  }

  @override
  int get hashCode {
    return layout.hashCode ^
        title.hashCode ^
        text.hashCode ^
        smallIcon.hashCode ^
        largeIcon.hashCode ^
        priority.hashCode ^
        channelName.hashCode ^
        actions.hashCode ^
        enableWifiLock.hashCode ^
        enableWakeLock.hashCode;
  }
}
