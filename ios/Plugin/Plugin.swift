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
    
    public func messageComposeViewController(
        _ controller: MFMessageComposeViewController,
        didFinishWith result: MessageComposeResult) {
        
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
        self.pluginCall = call
        
        let number = call.getString("number") ?? ""
        let body = call.getString("body") ?? ""
        
        if !MFMessageComposeViewController.canSendText() {
            call.reject("ERR_SERVICE_NOTFOUND")
            return
        }
        
        DispatchQueue.main.async {
            let composeVC = MFMessageComposeViewController()
            composeVC.messageComposeDelegate = self
            composeVC.body = body
            if (number != "") {
                composeVC.recipients = [number]
            }
            composeVC.disableUserAttachments()
            self.bridge.viewController.present(composeVC,  animated: true, completion: nil)
        }
    }
}
