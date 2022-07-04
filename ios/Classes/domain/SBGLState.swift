//
//  SBGLState.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-22.
//

import Foundation

struct SBGLState {
    let isTracking: Bool
    let requestSettings: RequestSettings?
    let positions: Array<Position>?
    
    func toMap() -> Dictionary<String, Any?> {
        return [
            "isTracking": NSNumber(value: isTracking),
            "requestSettings": nil,
            "positions": nil
        ]
    }
}
