package com.royzed.simple_bg_location.permission

data class BackgroundPermissionRationale(
    val title: String,
    val message: String,
) {
    companion object {
        @JvmStatic
        fun fromMap(map: Map<String, Any?>?): BackgroundPermissionRationale? {
            if (map == null) {
                return null
            }

            return BackgroundPermissionRationale(
                title = map["title"] as String? ?: "",
                message = map["message"] as String? ?: ""
            )
        }
    }
}
