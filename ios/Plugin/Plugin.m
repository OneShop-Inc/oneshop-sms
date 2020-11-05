#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>
#import "Plugin.h"
#import "MobileCoreServices/MobileCoreServices.h"
#import "NSData+Base64.h"

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(OneShopSms, "OneShopSms",
           CAP_PLUGIN_METHOD(openMessanger, CAPPluginReturnPromise);
)

@implementation SmsHelper

// shamelessly copied from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin/blob/master/src/ios/SocialSharing.m#L557
-(NSURL *)getFile: (NSString *)fileName {
    NSURL *file = nil;
    if (fileName != (id)[NSNull null]) {
        if ([fileName hasPrefix:@"http"]) {
            NSURL *url = [NSURL URLWithString:fileName];
            NSData *fileData = [NSData dataWithContentsOfURL:url];
            file = [NSURL fileURLWithPath:[self storeInFile:(NSString*)[[fileName componentsSeparatedByString: @"/"] lastObject] fileData:fileData]];
        } else if ([fileName hasPrefix:@"www/"]) {
            NSString *bundlePath = [[NSBundle mainBundle] bundlePath];
            NSString *fullPath = [NSString stringWithFormat:@"%@/%@", bundlePath, fileName];
            file = [NSURL fileURLWithPath:fullPath];
        } else if ([fileName hasPrefix:@"file://"]) {
            // stripping the first 6 chars, because the path should start with / instead of file://
            file = [NSURL fileURLWithPath:[fileName substringFromIndex:6]];
        } else if ([fileName hasPrefix:@"data:"]) {
            // using a base64 encoded string
            // extract some info from the 'fileName', which is for example: data:text/calendar;base64,<encoded stuff here>
            NSString *fileType = (NSString*)[[[fileName substringFromIndex:5] componentsSeparatedByString: @";"] objectAtIndex:0];
            fileType = (NSString*)[[fileType componentsSeparatedByString: @"/"] lastObject];
            NSString *base64content = (NSString*)[[fileName componentsSeparatedByString: @","] lastObject];
            
            NSData *fileData = [NSData dataFromBase64String:base64content];
            file = [NSURL fileURLWithPath:[self storeInFile:[NSString stringWithFormat:@"%@.%@", @"file", fileType] fileData:fileData]];
        } else {
            // assume anywhere else, on the local filesystem
            file = [NSURL fileURLWithPath:fileName];
        }
    }
    return file;
}


// shamelessly copied from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin/blob/master/src/ios/SocialSharing.m#L587
-(NSString *)storeInFile: (NSString*) fileName fileData: (NSData*) fileData {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *filePath = [documentsDirectory stringByAppendingPathComponent:fileName];
    [fileData writeToFile:filePath atomically:YES];
    _tempStoredFile = filePath;
    return filePath;
}

@end
