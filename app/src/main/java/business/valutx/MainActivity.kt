package business.valutx

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import business.valutx.ui.theme.ValutXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // This ensures the design flows under the status bar/navigation bar
        enableEdgeToEdge()
        
        setContent {
            ValutXTheme {
                WebViewScreen()
            }
        }
    }
}

@Composable
fun WebViewScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Exact background color from the HTML design to prevent flashes
                setBackgroundColor(android.graphics.Color.parseColor("#f6f1e9"))
                
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    
                    // Essential for local HTML functionality
                    allowFileAccess = true
                    allowContentAccess = true
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true
                    
                    // Mobile rendering optimizations
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                    
                    // Compatibility for external assets (Fonts/Charts)
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    databaseEnabled = true
                }
                
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                
                // Load the exact original file
                loadUrl("file:///android_asset/index.html")
            }
        }
    )
}