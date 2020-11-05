#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "Capacitor.h"

#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMessageComposeViewController.h>

//! Project version number for Plugin.
FOUNDATION_EXPORT double PluginVersionNumber;

//! Project version string for Plugin.
FOUNDATION_EXPORT const unsigned char PluginVersionString[];

// In this header, you should import all the public headers of your framework using statements like #import <Plugin/PublicHeader.h>

@interface SmsHelper : NSObject

@property(retain) NSString* tempStoredFile;

- (NSURL *)getFile: (NSString *)fileName;

@end

