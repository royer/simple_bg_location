package com.royzed.simple_bg_location.errors

enum class ErrorCodes(val code: String, val description: String) {
    permissionDefinitionsNotFound(
        "PERMISSION_DEFINITIONS_NOT_FOUND",
        "No location permissions are defined in the manifest. "+
                "Make sure at least ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION "+
                "are defined in the manifest."
    ),
    permissioDenied(
        "PERMISSION_DENIED",
        "User denied permissions to access the device's location."
    )
}