//
//  RequestSettings.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-27.
//

import Foundation
import SwiftLocation

struct RequestSettings {
    let accuracy: LocationAccuracy
    let distanceFilter: Int
    let forceLocationManager: Bool  // __Android__
    let interval: Int
    let minUpdateInterval: Int
    let duration: Int
    let maxUpdateDelay: Int
    let maxUpdates: Int
    let notificationConfig: NSObject?    // __Android ONly__ always nil in iOS
    
    
    func toMap() -> Dictionary<String, Any?> {
        return [
            "accuracy": accuracy.rawValue,
            "distanceFilter": distanceFilter,
            "forceLocationManager": forceLocationManager,
            "interval": interval,
            "minUpdateInterval": minUpdateInterval,
            "duration": duration,
            "maxUpdateDelay": maxUpdateDelay,
            "maxUpdates": maxUpdates,
            "notificationConfig": nil
        ]
    }
    
    static func fromMap(_ map: Dictionary<String, Any?>) -> RequestSettings {
        return RequestSettings(
            accuracy: LocationAccuracy.fromRawValue(val: map["accuracy"] as! Int? ?? 4),
            distanceFilter: map["distanceFilter"] as! Int? ?? 0,
            forceLocationManager: false,
            interval: map["interval"] as! Int? ?? 0,
            minUpdateInterval: map["minUpdateInterval"] as! Int? ?? 0,
            duration: map["duration"] as! Int? ?? 0,
            maxUpdateDelay: map["maxUpdateDelay"] as! Int? ?? 0,
            maxUpdates: map["maxUpdates"] as! Int? ?? 0,
            notificationConfig: nil)
    }
    
    func toGSPLocationOptions() -> GPSLocationOptions {
        let options = GPSLocationOptions()
        
        options.minTimeInterval = Double(interval/1000)
        options.activityType = .fitness
        options.subscription = .continous
        options.accuracy = accuracy.mapToGPSLocationAccuracy()
        options.avoidRequestAuthorization = true
        options.minDistance = Double(self.distanceFilter)
        return options
    }
}
