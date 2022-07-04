//
//  Methods.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-22.
//

import Foundation

enum Methods: String {
    case checkPermission = "checkPermission"
    case requestPermission = "requestPermission"
    case isLocationServiceEnabled = "isLocationServiceEnabled"
    case getAccuracyPermission = "getAccuracyPermission"
    case openAppSettings = "openAppSettings"
    case openLocationSettings = "openLocationSettings"
    case getLastKnownPosition = "getLastKnownPosition"
    case getCurrentPosition = "getCurrentPosition"
    case requestPositionUpdate = "requestPositionUpdate"
    case stopPositionUpdate = "stopPositionUpdate"
    case ready = "ready"
    case isPowerSaveMode = "isPowerSaveMode"
}
