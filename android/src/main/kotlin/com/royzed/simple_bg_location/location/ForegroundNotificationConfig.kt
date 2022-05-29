package com.royzed.simple_bg_location.location

class ForegroundNotificationConfig(
    val title: String,
    val text: String,
    val channelName: String = "Location",
    val channelDescription: String = "Tracking location in the background",
    val icon: AndroidResource = AndroidResource("ic_launcher", "mipmap"),

    ) {

}