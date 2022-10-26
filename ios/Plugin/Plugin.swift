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
                self.pluginCall!.resolve()
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

            self.bridge?.viewController?.present(composeVC, animated: true, completion: nil);
        }
    }

    @objc func canShare(_ call: CAPPluginCall) {
      if let _ = URL(string: "instagram-stories://share") {
        call.resolve(["value": true])
      } else {
        call.resolve(["value": false])
      }
    }
    
    @objc func share(_ call: CAPPluginCall) {
      var urlComps = URLComponents(string: "instagram-stories://share")!
      if let appId = call.getString("appId") {
        urlComps.queryItems = [URLQueryItem(name: "source_application",value: appId)]
      }

      if let url = urlComps.url {
        if UIApplication.shared.canOpenURL(url as URL) {
          let imageData = SmsHelper().getImageData(call.getString("image"))
          
          let pasteboardItems: [String: Any] = [
            "com.instagram.sharedSticker.stickerImage": imageData!,
            "com.instagram.sharedSticker.backgroundTopColor": call.getString("topColor", "#777777"),
            "com.instagram.sharedSticker.backgroundBottomColor": call.getString("bottomColor", "#bbbbbb")
          ]
          let pasteboardOptions = [
            UIPasteboard.OptionsKey.expirationDate: Date().addingTimeInterval(300)
          ]
          UIPasteboard.general.setItems([pasteboardItems], options:pasteboardOptions)
          DispatchQueue.main.async {
            UIApplication.shared.open(url) {success in if success {
              call.resolve()
            } else {
              call.reject("error opening url")
            }}
          }
        } else {
          call.reject("can not share to instagram")
        }
      } else {
        call.reject("can not create url")
      }
    }
}
