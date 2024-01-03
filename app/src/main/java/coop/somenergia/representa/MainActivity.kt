package coop.somenergia.representa
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Base64

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val homeUrl: String = "https://ov-representa.test.somenergia.coop"
    private val jsInsertionName = "SomenergiaPlatform"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.webViewClient = WebViewClient()

        webView.loadUrl(homeUrl)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportZoom(true)
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.allowFileAccess = true

        webView.addJavascriptInterface(this, jsInsertionName)
    }

    private fun toastMe(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    @JavascriptInterface
    fun save(filename: String, base64Content: String) {
        val decoded = Base64.getDecoder().decode(base64Content)
        val targetDir = "${Environment.DIRECTORY_DOCUMENTS}/Som Energia"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, targetDir)
        }
        Log.i("What", contentValues.toString())
        val pdfUri = contentResolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            contentValues
        )
        pdfUri?.let { uri -> contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(decoded)
        }}
        toastMe("Guardado en $targetDir/$filename")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
