//
//  NotificationActionStreamHandler.swift
//  simple_bg_location
//
//  Created by Roy Wang on 2022-06-22.
//

import Foundation

// This is Andoid Obly. iOS do nothing
class NotificationStreamHandler : NSObject, FlutterStreamHandler {
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil
    }
    
    
}
