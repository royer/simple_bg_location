//
//  ErrorCodes.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-23.
//

import Foundation

struct ErrorCode {
    let code: String
    let description: String
}

struct ErrorCodes {
    static let permissionDenied =
    ErrorCode(
        code: "PERMISSION_DENIED",
        description: "User denied permissions to access the device's location.")
    static let locationServicesDisabled =
    ErrorCode(
        code: "LOCATION_SERVICES_DISABLED",
        description: "Neither GPS nor Network location service is enabled.")
    static let errorWhileAcquiringPosition =
    ErrorCode(
        code: "ERROR_WHILE_ACQUIRING_POSITION",
        description: "An unexpected error occurred while trying to acquire the device's position.")
    static let missedCallReadyMethod =
    ErrorCode(
        code: "MISSED_CALL_READY",
        description: "Missed call ready method before call requestPositionUpdate"
    )
}
