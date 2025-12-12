import SwiftUI
import WebKit

struct WebWithGlassTextField: View {
  let url: String

  @State private var text: String = ""

  var body: some View {
    ZStack(alignment: .bottom)  {
      WebViewRepresentable(url: URL(string: url)!)

      // Background scrim
      LinearGradient(
        colors: [.clear, Color(uiColor: .systemBackground).opacity(0.5)],
        startPoint: .top,
        endPoint: .bottom
      )
          .frame(height: 150)
          .frame(maxWidth: .infinity)
          .ignoresSafeArea(edges: .bottom)

      if #available(iOS 26.0, *) {
        TextField("Search...", text: $text)
            .padding()
            .glassEffect(.clear)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
            .frame(maxWidth: .infinity)
            .padding(24)
      } else {
        TextField("Search...", text: $text)
            .padding()
            .background(.ultraThinMaterial)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
            .clipShape(Capsule())
            .frame(maxWidth: .infinity)
            .padding(24)
      }
    }
  }
}

struct WebViewRepresentable: UIViewRepresentable {
  let url: URL

  func makeUIView(context: Context) -> WKWebView {
    let webView = WKWebView()
    webView.load(URLRequest(url: url))
    return webView
  }

  func updateUIView(_ uiView: WKWebView, context: Context) {}
}

@objc(SwiftGlassWebViewProvider)
public class SwiftGlassWebViewProvider: NSObject {
  @objc public static func createWebViewGlass(_ url: String) -> UIViewController {
    let view = WebWithGlassTextField(url: url)
    return UIHostingController(rootView: view)
  }
}
