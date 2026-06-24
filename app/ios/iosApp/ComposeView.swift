import AiSdPresentation
import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = ComposeContainerViewController(contentController: MainViewControllerKt.MainViewController())
        OptionalNonFreeBridge.setRootViewController(controller)
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

private final class ComposeContainerViewController: UIViewController {
    private let contentController: UIViewController

    init(contentController: UIViewController) {
        self.contentController = contentController
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        addChild(contentController)
        view.backgroundColor = .systemBackground

        let contentView = contentController.view!
        contentView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(contentView)
        NSLayoutConstraint.activate([
            contentView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            contentView.topAnchor.constraint(equalTo: view.topAnchor),
            contentView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        contentController.didMove(toParent: self)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        OptionalNonFreeBridge.setRootViewController(self)
    }
}
