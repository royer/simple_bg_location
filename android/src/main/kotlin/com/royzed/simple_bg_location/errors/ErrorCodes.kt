package com.royzed.simple_bg_location.errors

enum class ErrorCodes(val code: String, val description: String) {
    permissionDefinitionsNotFound(
        "PERMISSION_DEFINITIONS_NOT_FOUND",
        "No location permissions are defined in the manifest. "+
                "Make sure at least ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION "+
                "are defined in the manifest."
    ),
    permissionDenied(
        "PERMISSION_DENIED",
        "User denied permissions to access the device's location."
    ),
    otherRequestInProgress(
        "OTHER_REQUEST_INPROGRESS",
        "previous request is still waiting answer."
    ),
    activityMissing(
        "ACTIVITY_MISSING",
        "Activity is missing. This might happen when running a certain function from " +
                "the background that requires a UI element (e.g. requesting permissions or "+
                "enabling the location services)."
    ),
    locationServicesDisabled(
        "LOCATION_SERVICES_DISABLED",
        "Neither GPS nor Network location service is enabled."

    )

}