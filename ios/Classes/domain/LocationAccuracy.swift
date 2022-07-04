//
//  LocationAccuracy.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-27.
//

import Foundation
import SwiftLocation

enum LocationAccuracy: Int {
    case lowest = 0, low, medium, high, best, bestForNavigation
    
    static func fromRawValue(val: Int) -> LocationAccuracy {
        switch val {
            case 0:
                return .lowest
            case 1:
                return .low
            case 2:
                return .medium
            case 3:
                return .high
            case 4:
                return .best
            case 5:
                return .bestForNavigation
            default:
                return .bestForNavigation
        }
    }
    
    func mapToGPSLocationAccuracy() -> GPSLocationOptions.Accuracy {
        switch (self) {
            case .lowest:
                return GPSLocationOptions.Accuracy.city
            case .low:
                return GPSLocationOptions.Accuracy.block
            case .medium:
                return GPSLocationOptions.Accuracy.house
            case .high:
                return GPSLocationOptions.Accuracy.room
            case .best:
                return GPSLocationOptions.Accuracy.room
            case .bestForNavigation:
                return GPSLocationOptions.Accuracy.room
        }
    }
}
