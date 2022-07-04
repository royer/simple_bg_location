import Flutter
import UIKit
import SwiftLocation
import CoreLocation

public class SwiftSimpleBgLocationPlugin: NSObject, FlutterPlugin {
    
    var positionStream: PositionStreamHandler?
    
    var notificationActionStream: NotificationStreamHandler?
    
    var requestSettings: RequestSettings?
    var isReady: Bool = false
    var isTracking: Bool = false
    var positions: Array<Position> = []
    var locationRequest: GPSLocationRequest?
    
    
    static let PLUGIN_PATH = "com.royzed.simple_bg_location"
    static let METHOD_CHANNEL_NAME = "\(PLUGIN_PATH)/methods"
    static let EVENT_CHANNEL_PATH = "\(PLUGIN_PATH)/events"
    
    init(_ messenger: FlutterBinaryMessenger, _ registrar: FlutterPluginRegistrar) {
        super.init()
        
        let eventChannel = FlutterEventChannel(name: "\(SwiftSimpleBgLocationPlugin.EVENT_CHANNEL_PATH)/\(Events.position)", binaryMessenger: messenger)
        
        positionStream = PositionStreamHandler()
        eventChannel.setStreamHandler(positionStream)
        
        // NotificationActionStream, android only
        let notficationActionEventChannel = FlutterEventChannel(name: "\(SwiftSimpleBgLocationPlugin.EVENT_CHANNEL_PATH)/\(Events.notificationAction)", binaryMessenger: messenger)
        notificationActionStream = NotificationStreamHandler()
        notficationActionEventChannel.setStreamHandler(notificationActionStream)
    }
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: METHOD_CHANNEL_NAME, binaryMessenger: registrar.messenger())
      let instance = SwiftSimpleBgLocationPlugin(registrar.messenger(), registrar)
      
    
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      //print("method(%s) called.", call.method)
      
      switch call.method {
          case Methods.ready.rawValue:
              onReady(result)
          case Methods.checkPermission.rawValue: do {
              let authStatus = SwiftLocation.authorizationStatus
              let permission = SwiftSimpleBgLocationPlugin.mapAuthStatusToLocationPermission(authStatus)
              result(permission.rawValue)
          }
          case Methods.requestPermission.rawValue:
              onRequestPermission(result)
          case Methods.isLocationServiceEnabled.rawValue:
              onIsLocationServiceEnabled(result)
          case Methods.getAccuracyPermission.rawValue:
              onGetAccuracyPermission(result)
          case Methods.getLastKnownPosition.rawValue:
              onGetLastKnownPosition(result)
          case Methods.getCurrentPosition.rawValue:
              onGetCurrentPosition(result)
          case Methods.requestPositionUpdate.rawValue:
              onRequestPositionUpdate(call, result)
          case Methods.stopPositionUpdate.rawValue:
              onStopPositionUpdate(result)
          case Methods.isPowerSaveMode.rawValue:
              onIsPowerSaveModeEnable(result)
          case Methods.openAppSettings.rawValue, Methods.openLocationSettings.rawValue:
              openSettings(result)
          default:
              result(1)
      }
  }
    
    func onReady(_ result: @escaping FlutterResult) {
        print("onReady: isTracking: \(isTracking)")
        isReady = true
        result(
          SBGLState(
              isTracking: isTracking,
              requestSettings: requestSettings,
              positions: positions)
          .toMap()
        )

    }
    
    func onRequestPermission(_ result: @escaping FlutterResult) {
        
        let authStatus = SwiftLocation.authorizationStatus
        
        if (authStatus == .notDetermined) {
            SwiftLocation.requestAuthorization() { newStatus in
                let permission = SwiftSimpleBgLocationPlugin.mapAuthStatusToLocationPermission(newStatus)
                result(permission.rawValue)
            }
        } else {
            var permission: LocationPermission
            if (authStatus == .denied) {
                permission = LocationPermission.deniedForever
            } else {
                permission = SwiftSimpleBgLocationPlugin.mapAuthStatusToLocationPermission(authStatus)
            }
            result(permission.rawValue)
        }
    }
    
    static func mapAuthStatusToLocationPermission(_ status: CLAuthorizationStatus)
        -> LocationPermission {
            switch status {
                case .denied:
                    return LocationPermission.denied
                case .authorizedWhenInUse:
                    return LocationPermission.whileInUse
                case .authorizedAlways:
                    return LocationPermission.always
                case .restricted:
                    return LocationPermission.whileInUse
                case .notDetermined:
                    return LocationPermission.denied
                @unknown default:
                    return LocationPermission.denied
            }
    }
    
    func onIsLocationServiceEnabled(_ result: @escaping FlutterResult) {
        if CLLocationManager.locationServicesEnabled() {
            result(NSNumber(true))
        } else {
            result(NSNumber(false))
        }
    }
    
    func openSettings(_ result: @escaping FlutterResult) -> Void {
        
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!) { suc in
                
                result(NSNumber(value: suc))
            }
        } else {
            let success = UIApplication.shared.openURL(URL(string:UIApplication.openSettingsURLString)!)
            result(NSNumber(value: success))
        }
        
        
    }
    
    func onGetAccuracyPermission(_ result: @escaping FlutterResult) {
        let permission = SwiftLocation.authorizationStatus
        if (permission == .denied || permission == .notDetermined) {
            result(AccuracyPermission.denied.rawValue)
        } else {
            let accuracy = SwiftLocation.preciseAccuracy
            print(accuracy)
            switch accuracy {
                case .fullAccuracy:
                    result(AccuracyPermission.precise.rawValue)
                case .reducedAccuracy:
                    result(AccuracyPermission.approximate.rawValue)
            }

        }
        
    }
    
    func onGetLastKnownPosition(_ result: @escaping FlutterResult) {
        if !CLLocationManager.locationServicesEnabled() {
            result(FlutterError(
                code: ErrorCodes.locationServicesDisabled.code,
                message: ErrorCodes.locationServicesDisabled.description,
                details: nil))
            return
        }
        
        let authStatus = SwiftLocation.authorizationStatus
        if (authStatus == .denied || authStatus == .notDetermined) {
            result(FlutterError(
                code: ErrorCodes.permissionDenied.code,
                message: ErrorCodes.permissionDenied.description,
                details: nil
            ))
            return
        }
        
        let lastKnownPosition = SwiftLocation.lastKnownGPSLocation
        result(Position.fromLocation(lastKnownPosition)?.toMap())
    }
    
    func onGetCurrentPosition(_ result: @escaping FlutterResult) {

        if !checkServiceAndAuth(result) {
            return
        }
        SwiftLocation.gpsLocationWith(getDefaultGPSLocationOptions()).then {
            locationResult in
            
            switch locationResult {
                case .success(let location):
                    result(Position.fromLocation(location)?.toMap())
                case .failure(let error):
                    result(FlutterError(
                        code: ErrorCodes.errorWhileAcquiringPosition.code,
                        message: error.localizedDescription,
                        details: error.recoverySuggestion
                    ))
                    
            }
        }

    }
    
    func onRequestPositionUpdate(_ call: FlutterMethodCall, _ result:  @escaping FlutterResult) {
        if (!checkServiceAndAuth(result)) {
            return
        }
        
        if !isReady {
            result(FlutterError(
                code: ErrorCodes.missedCallReadyMethod.code,
                message:ErrorCodes.missedCallReadyMethod.description,
                details: nil
            ))
            return
        }
        if (isTracking) {
            result(NSNumber(true))
            return
        }
        
        requestSettings = RequestSettings.fromMap(call.arguments as! Dictionary<String, Any?>)
        print(requestSettings ?? "nil RequestSettings received")
        let options = requestSettings?.toGSPLocationOptions()
        print("options: \(options!)")
        //SwiftLocation.allowsBackgroundLocationUpdates = true
        locationRequest = SwiftLocation.gpsLocationWith(options!)
        print("options in locationRequest: \(String(describing: locationRequest?.options))")
        locationRequest?.then { [self]locationResult in
            switch locationResult {
                case .success(let location):
                    print("New Location: \(location)")
                    positionStream?.eventSink!(Position.fromLocation(location)?.toMap())
                case .failure(let error):
                    print("An Error on Listen Position: \(error)")
                    positionStream?.eventSink!(FlutterError(
                        code: ErrorCodes.errorWhileAcquiringPosition.code,
                        message: ErrorCodes.errorWhileAcquiringPosition.description,
                        details: error.localizedDescription
                    ))
                    stopPositionUpdate()
                    
            }
        }
        result(NSNumber(true))
    }
    
    func onStopPositionUpdate(_ result: @escaping FlutterResult) {
        stopPositionUpdate()
        result(true)
    }
    
    private func stopPositionUpdate() {
        isTracking = false
        locationRequest?.cancelRequest()
    }
    
    func onIsPowerSaveModeEnable(_ result: @escaping FlutterResult) {
        let isEnabled: Bool = ProcessInfo.processInfo.isLowPowerModeEnabled
        
        result(NSNumber(value: isEnabled))
    }
    
    func getDefaultGPSLocationOptions() -> GPSLocationOptions {
        let defaultOption = GPSLocationOptions()
        defaultOption.precise = .fullAccuracy
        defaultOption.minTimeInterval = 2
        defaultOption.subscription = .single
        
        return defaultOption
    }
    
    private func checkServiceAndAuth(_ result: @escaping FlutterResult) -> Bool {
        if !CLLocationManager.locationServicesEnabled() {
            result(FlutterError(
                code: ErrorCodes.locationServicesDisabled.code,
                message: ErrorCodes.locationServicesDisabled.description,
                details: nil))
            return false
        }
        
        let authStatus = SwiftLocation.authorizationStatus
        if (authStatus == .denied || authStatus == .notDetermined) {
            result(FlutterError(
                code: ErrorCodes.permissionDenied.code,
                message: ErrorCodes.permissionDenied.description,
                details: nil
            ))
            return false
        }
        
        return true
    }
}
