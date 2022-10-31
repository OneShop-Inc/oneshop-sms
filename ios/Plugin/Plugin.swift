import Foundation
import Capacitor
import MessageUI
import Photos
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

  // https://stackoverflow.com/questions/38303413/ios-sharing-image-to-instagram-without-using-a-menu-display
  func saveToCameraRoll(image: UIImage, completion: @escaping (URL?) -> Void) {
    var placeHolder: PHObjectPlaceholder? = nil
    PHPhotoLibrary.shared().performChanges({
      let creationRequest = PHAssetChangeRequest.creationRequestForAsset(from: image)
      placeHolder = creationRequest.placeholderForCreatedAsset!
    }, completionHandler: { success, error in
      guard success, let placeholder = placeHolder else {
        completion(nil)
        return
      }
      let assets = PHAsset.fetchAssets(withLocalIdentifiers: [placeholder.localIdentifier], options: nil)
      guard let asset = assets.firstObject else {
        completion(nil)
        return
      }
      asset.requestContentEditingInput(with: nil, completionHandler: { (editingInput, _) in
        completion(editingInput?.fullSizeImageURL)
      })
    })
  }

  // https://stackoverflow.com/questions/38303413/ios-sharing-image-to-instagram-without-using-a-menu-display
  func shareToInstagram(_ theImageToShare: UIImage){
    self.saveToCameraRoll(image: theImageToShare) { (theUrl) in
      if let url = theUrl {
        DispatchQueue.main.async {
          if let path = url.absoluteString.addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed){
            let instagram = URL(string: "instagram://library?AssetPath=\(path)")
            UIApplication.shared.open(instagram!)
          }
        }
      }
    }
  }

  @objc func share(_ call: CAPPluginCall) {
    let imageData = SmsHelper().getImageData(call.getString("image"))

    guard let image = UIImage(data: imageData!)?.scaleImageWithAspectToWidth(640) else {
      call.reject("something went wrong sharing the image")
      return
    }

    if (call.getBool("shareToStories", false)) {
      // https://developers.facebook.com/docs/instagram/sharing-to-stories
      var urlComps = URLComponents(string: "instagram-stories://share")!
      if let appId = call.getString("appId") {
        urlComps.queryItems = [URLQueryItem(name: "source_application", value: appId)]
      }

      if let url = urlComps.url {
        if UIApplication.shared.canOpenURL(url as URL) {
          let pasteboardItems: [String: Any] = [
            "com.instagram.sharedSticker.stickerImage": imageData!,
            "com.instagram.sharedSticker.backgroundTopColor": call.getString("topColor", "#444444"),
            "com.instagram.sharedSticker.backgroundBottomColor": call.getString("bottomColor", "#444444")
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
    } else {
      // https://stackoverflow.com/questions/38303413/ios-sharing-image-to-instagram-without-using-a-menu-display
      //check and see if we can save photos
      let status = PHPhotoLibrary.authorizationStatus()
      if (status == PHAuthorizationStatus.authorized) {
        self.shareToInstagram(image)
      } else if (status == PHAuthorizationStatus.denied) {
        print("access denied")
        call.reject("access denied")
      } else if (status == PHAuthorizationStatus.notDetermined) {
        print("access not determined")
          PHPhotoLibrary.requestAuthorization({ (newStatus) in
            if (newStatus == PHAuthorizationStatus.authorized) {
              self.shareToInstagram(image)
            } else {
              print("take 2 denied")
              call.reject("access not determined")
            }
          })
      } else if (status == PHAuthorizationStatus.restricted) {
        print("restricted access")
        call.reject("access restricted")
      }
    }
  }
}


// https://stackoverflow.com/questions/73261845/swiftui-share-instagram-post
extension UIImage {
  func scaleImageWithAspectToWidth(_ toWidth:CGFloat) -> UIImage {
    let oldWidth:CGFloat = size.width
    let scaleFactor:CGFloat = toWidth / oldWidth
    
    let newHeight = self.size.height * scaleFactor
    let newWidth = oldWidth * scaleFactor;
    
    UIGraphicsBeginImageContext(CGSize(width: newWidth, height: newHeight))
    draw(in: CGRect(x: 0, y: 0, width: newWidth, height: newHeight))
    let newImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return newImage!
  }
}
