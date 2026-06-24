import UIKit

enum OptionalNonFreeBridge {
    static func registerIfAvailable() {
        call("SDAIAdMobBridgeRegistration", selector: "registerIfAvailable")
        call("SDAIIapBridgeRegistration", selector: "registerIfAvailable")
    }

    static func setRootViewController(_ viewController: UIViewController) {
        call("SDAIAdMobBridgeRegistration", selector: "setRootViewController:", object: viewController)
    }

    private static func call(
        _ className: String,
        selector selectorName: String,
        object: Any? = nil
    ) {
        guard let type = NSClassFromString(className) as? NSObject.Type else { return }
        let selector = NSSelectorFromString(selectorName)
        guard type.responds(to: selector) else { return }
        _ = type.perform(selector, with: object)
    }
}
