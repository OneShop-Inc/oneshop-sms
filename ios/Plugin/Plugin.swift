import Foundation
import Capacitor
import MessageUI
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(OneshopSms)
public class OneshopSms: CAPPlugin, MFMessageComposeViewControllerDelegate {

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

    @objc func openMessenger(_ call: CAPPluginCall) {
        self.pluginCall = call
        
        let number = call.getString("number") ?? ""
        let body = call.getString("body") ?? ""

        let attachments = call.getArray("attachments", String.self) ?? []
        
        if !MFMessageComposeViewController.canSendText() {
            call.reject("ERR_SERVICE_NOTFOUND")
            return
        }

        // Present the view controller modally.
        DispatchQueue.main.async {
            let smsHelper = SmsHelper()
            let composeVC = MFMessageComposeViewController()

            composeVC.messageComposeDelegate = self
            composeVC.body = body
            if (number != "") {
                composeVC.recipients = [number]
            }
            
            attachments.forEach { path in
                let file = smsHelper.getFile(path)
                do {
                    let data = try Data(contentsOf: file!)
                    composeVC.addAttachmentData(data, typeIdentifier: "image/jpeg", filename: "image.jpeg")
                } catch {
                    print("oop")
                }
            }

            self.bridge.viewController.present(composeVC, animated: true, completion: nil);
        }
    }
}
