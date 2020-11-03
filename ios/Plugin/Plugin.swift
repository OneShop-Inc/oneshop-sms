import Foundation
import Capacitor
import MessageUI
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(OneShopSms)
public class OneShopSms: CAPPlugin, MFMessageComposeViewControllerDelegate {

    var pluginCall: CAPPluginCall?
    
    @objc func echo(_ call: CAPPluginCall) {
        print("iOs echo")
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    public func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        print("messageComposeViewController")
        print(result.rawValue)
        switch (result.rawValue) {
            case MessageComposeResult.cancelled.rawValue:
                self.pluginCall!.reject("SEND_CANCELLED")
            case MessageComposeResult.failed.rawValue:
                self.pluginCall!.reject("ERR_SEND_FAILED")
            case MessageComposeResult.sent.rawValue:
                self.pluginCall!.success()
            default:
                self.pluginCall!.reject("ERR_SEND_UNKNOWN_STATE")
        }
        
        controller.dismiss(animated: true, completion: nil)
    }
    
    
    @objc func openMessanger(_ call: CAPPluginCall) {
//        print("ios open messanger!")
        NSLog("ios open messanger!")
        self.pluginCall = call
        
        if !MFMessageComposeViewController.canSendText() {
            call.reject("ERR_SERVICE_NOTFOUND")
            return
        }
        
        DispatchQueue.main.async {
            let composeVC = MFMessageComposeViewController()
            composeVC.messageComposeDelegate = self
            composeVC.body = "hey!"
            composeVC.disableUserAttachments()
            self.bridge.viewController.present(composeVC,  animated: true, completion: nil)
        }
        /*
        let composeVC = MFMessageComposeViewController()
        composeVC.messageComposeDelegate = self
        
        composeVC.subject = "cool subject"
        // Configure the fields of the interface.
//        composeVC.recipients = numbers
//        composeVC.body = text
        // Present the view controller modally.
        DispatchQueue.main.async {
            NSLog("present!")
            // Update UI
            self.bridge.viewController.present(composeVC, animated: true, completion: nil);
        }
 */
        
    }

    
}
