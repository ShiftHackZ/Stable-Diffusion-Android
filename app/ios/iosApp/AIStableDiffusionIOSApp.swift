import SwiftUI

@main
struct AIStableDiffusionIOSApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(uiColor: .systemBackground).ignoresSafeArea())
                .ignoresSafeArea()
        }
    }
}
