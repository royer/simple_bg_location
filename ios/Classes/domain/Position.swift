//
//  Position.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-22.
//

import Foundation
import CoreLocation

struct Position {
    let uuid: String
    let latitude: Double
    let longitude: Double
    let timestamp: Int
    let altitude: Double?
    let altitudeAccuracy: Double?
    let accuracy: Double?
    let heading: Double?
    let headingAccuracy: Double?
    let floor: Int?
    let speed: Double?
    let speedAccuracy: Double?
    let isMocked: Bool
    
    func toMap() -> Dictionary<String, Any?> {
        return [
            "uuid": uuid,
            "latitude": latitude,
            "longitude": longitude,
            "timestamp": timestamp,
            "altitude": altitude,
            "altitudeAccuracy": altitudeAccuracy,
            "accuracy": accuracy,
            "heading": heading,
            "headingAccuracy": headingAccuracy,
            "floor": floor,
            "speed": speed,
            "speedAccuracy": speedAccuracy,
            "isMocked": isMocked
        ];
    }
    
    static func fromLocation(_ location: CLLocation?) -> Position? {
        if (location == nil) {
            return nil
        }
        let uuid = UUID()
        var headingAccuracy: Double = -1.0
        if #available(iOS 13.4, *) {
            headingAccuracy = Double(location!.courseAccuracy)
        }
        
        return Position(
            uuid: uuid.uuidString,
            latitude: location!.coordinate.latitude,
            longitude: location!.coordinate.longitude,
            timestamp: Int(location!.timestamp.timeIntervalSince1970)*1000,
            altitude: location!.altitude,
            altitudeAccuracy: Double(location!.verticalAccuracy),
            accuracy: location!.horizontalAccuracy,
            heading: location!.course,
            headingAccuracy: headingAccuracy,
            floor: location!.floor?.level,
            speed: location!.speed,
            speedAccuracy: location!.speedAccuracy,
            isMocked: false
        )
    }
}
