package coop.somenergia.representa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import java.io.FileOutputStream
import java.io.IOException
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
    fun saveBase64(filename: String, base64Content: String, mimeType: String="application/pdf") {
        val decoded = Base64.getDecoder().decode(base64Content)
        val targetDir = "${Environment.DIRECTORY_DOCUMENTS}/Som Energia"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, targetDir)
        }
        val target = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        Log.i("What", contentValues.toString())
        val pdfUri = contentResolver.insert(target, contentValues)
        pdfUri?.let { uri -> contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(decoded)
        }}
        toastMe("Guardado en $targetDir/$filename")
    }

    @JavascriptInterface
    fun saveUtf8(filename: String, text: String, mimeType: String="plain/text") {
        val targetDir = "${Environment.DIRECTORY_DOCUMENTS}/Som Energia"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, targetDir)
        }
        Log.i("What", contentValues.toString())
        val target = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileUri:  = contentResolver.insert(target, contentValues)
        try {
            fileUri = contentResolver.insert(target, contentValues)
        } catch(android.database.sqlite.SQLiteConstraintException)
        {
            fileUri = contentResolver.update(target, contentValues)
        }
        fileUri?.let { uri -> contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(text.encodeToByteArray())
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
