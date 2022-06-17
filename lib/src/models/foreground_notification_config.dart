// ignore_for_file: constant_identifier_names

import 'dart:convert';

import 'package:flutter/foundation.dart';

import 'package:simple_bg_location/src/models/android_resource.dart';

/// __(Android only)__ The Android operating system requires a persistent
/// notification when running a foreground service.
///
class ForegroundNotificationConfig {
  /// The unique notification ID
  ///
  /// default is `198964`
  final int notificationId;

  /// The title of notification
  ///
  ///Defaults to the application name from `AndroidManifest`
  String? title;

  /// The content of notification
  ///
  /// Defaults to *"traced: {distance}  elapsed time: {elapsed}"*.
  ///
  /// ## Template Tags
  /// A limited number of template-tags are support. format: __`{tagName}`__
  /// | template Tag     | Description
  /// |------------------|---------------------------------------------------|
  /// | __`{distance}`__ | distance traced since start position update listening
  /// | __`{elapsed}`__  | elapsed time since start position update listening
  ///
  String? text;

  /// the notification small icon. if null plugin will use app launcher icon.
  AndroidResource? smallIcon;

  /// The large icon in notification body.
  ///
  /// __DO NOT use Application Launch Icon__
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
  ///
  final int priority;

  /// The Notification channel id
  ///
  /// Since Android 8.0 (API level 26) and higher, channel ID is required.
  /// default is 'com.royzed.simple_bg_location.channel_1989.6.4'
  final String channelId;

  /// the Channel name of notification
  ///
  /// On Android O+, foreground-service needs to create a "Notification Channel".
  /// The name of this channel can be seen in:
  /// > `Settings->App & Notifications->Your App.`
  ///
  /// Defaults is "Position Update".
  String? channelName;

  /// the channel description of notification
  ///
  /// default is "Notify user location service is running."
  String? channelDescription;

  /// Your custom up to three action buttons on notification
  ///
  /// __`"cancel"`__ action name is special action. if you provide "cancel"
  /// unlike other action, plugin will not send NotificationAction event, instead
  /// plugin will cancel current position update, and your location stream will
  /// got a [PositionError] which errorCode is __"CANCELED"__.
  ///
  ///If you provide a [layout], this actions is Button's id.
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

  ForegroundNotificationConfig({
    this.notificationId = defaultNotificationId,
    this.title,
    this.text,
    this.smallIcon,
    this.largeIcon,
    this.priority = NOTIFICATION_PRIORITY_DEFAULT,
    this.channelId = defaultChannelId,
    this.channelName,
    this.channelDescription,
    this.actions,
  })  : assert(actions == null || actions.length <= 3,
            'notification actions is up to three.'),
        assert(channelId.isNotEmpty, "channelId can not empty string."),
        assert(priority >= -1 && priority <= 2, 'priority must be in [-1, 2]');

  static const int NOTIFICATION_PRIORITY_DEFAULT = 0;
  static const int NOTIFICATION_PRIORITY_HIGH = 1;
  static const int NOTIFICATION_PRIORITY_LOW = -1;
  static const int NOTIFICATION_PRIORITY_MAX = 2;

  static const int defaultNotificationId = 198964;
  static const String defaultChannelId =
      'com.royzed.simple_bg_location.channel_1989.6.4';

  Map<String, dynamic> toMap() {
    return {
      'notificationId': notificationId,
      'title': title,
      'text': text,
      'smallIcon': smallIcon?.toMap(),
      'largeIcon': largeIcon?.toMap(),
      'priority': priority,
      'channelId': channelId,
      'channelName': channelName,
      'channelDescription': channelDescription,
      'actions': actions,
    };
  }

  factory ForegroundNotificationConfig.fromMap(Map<String, dynamic> map) {
    return ForegroundNotificationConfig(
      notificationId: map['notificationId'] ?? defaultNotificationId,
      title: map['title'],
      text: map['text'],
      smallIcon: map['smallIcon'] != null
          ? AndroidResource.fromMap(map['smallIcon'])
          : null,
      largeIcon: map['largeIcon'] != null
          ? AndroidResource.fromMap(map['largeIcon'])
          : null,
      priority: map['priority']?.toInt() ?? 0,
      channelId: map['channelId'] ?? defaultChannelId,
      channelName: map['channelName'],
      channelDescription: map['channelDescription'],
      actions: List<String>.from(map['actions']),
    );
  }

  String toJson() => json.encode(toMap());

  factory ForegroundNotificationConfig.fromJson(String source) =>
      ForegroundNotificationConfig.fromMap(json.decode(source));

  @override
  String toString() {
    return 'ForegroundNotificationConfig(notificationId: $notificationId, title: $title, text: $text, smallIcon: $smallIcon, largeIcon: $largeIcon, priority: $priority, channelId: $channelId, channelName: $channelName, channelDescription: $channelDescription, actions: $actions)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is ForegroundNotificationConfig &&
        other.notificationId == notificationId &&
        other.title == title &&
        other.text == text &&
        other.smallIcon == smallIcon &&
        other.largeIcon == largeIcon &&
        other.priority == priority &&
        other.channelId == channelId &&
        other.channelName == channelName &&
        other.channelDescription == channelDescription &&
        listEquals(other.actions, actions);
  }

  @override
  int get hashCode {
    return notificationId.hashCode ^
        title.hashCode ^
        text.hashCode ^
        smallIcon.hashCode ^
        largeIcon.hashCode ^
        priority.hashCode ^
        channelId.hashCode ^
        channelName.hashCode ^
        channelDescription.hashCode ^
        actions.hashCode;
  }
}
