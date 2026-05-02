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
        enableEdgeToEdge()
        
        // CRITICAL: Enable debugging so we can see console errors
        WebView.setWebContentsDebuggingEnabled(true)
        
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
                setBackgroundColor(android.graphics.Color.parseColor("#f6f1e9"))
                
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    databaseEnabled = true
                }
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // FORCE VISIBILITY VIA INJECTION
                        view?.evaluateJavascript("""
                            (function() {
                                console.log('ValutX: Page finished loading, forcing visibility...');
                                
                                // 1. Remove loader immediately
                                var loader = document.getElementById('loader');
                                if (loader) {
                                    loader.style.setProperty('display', 'none', 'important');
                                    loader.style.setProperty('opacity', '0', 'important');
                                }
                                
                                // 2. Force Dashboard to show
                                var dashboard = document.getElementById('screen-dashboard');
                                if (dashboard) {
                                    dashboard.style.setProperty('display', 'block', 'important');
                                    dashboard.style.setProperty('opacity', '1', 'important');
                                    dashboard.style.setProperty('visibility', 'visible', 'important');
                                    dashboard.classList.add('active');
                                }
                                
                                // 3. Force main content to show
                                var mains = document.getElementsByClassName('main');
                                for(var i=0; i<mains.length; i++) {
                                    mains[i].style.setProperty('opacity', '1', 'important');
                                    mains[i].style.setProperty('visibility', 'visible', 'important');
                                }
                                
                                // 4. Trigger resize for background
                                window.dispatchEvent(new Event('resize'));
                                console.log('ValutX: Visibility forced.');
                            })();
                        """.trimIndent(), null)
                    }
                }
                webChromeClient = WebChromeClient()
                loadUrl("file:///android_asset/index.html")
            }
        }
    )
}