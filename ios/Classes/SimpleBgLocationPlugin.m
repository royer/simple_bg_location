#import "SimpleBgLocationPlugin.h"
#if __has_include(<simple_bg_location/simple_bg_location-Swift.h>)
#import <simple_bg_location/simple_bg_location-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "simple_bg_location-Swift.h"
#endif

@implementation SimpleBgLocationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSimpleBgLocationPlugin registerWithRegistrar:registrar];
}
@end
