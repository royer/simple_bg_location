//
//  PositionStreamHandler.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-22.
//

import Foundation

class PositionStreamHandler: NSObject, FlutterStreamHandler {
    
    var eventSink: FlutterEventSink?
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        
        eventSink = events
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        eventSink = nil
        return nil
    }
    
}
