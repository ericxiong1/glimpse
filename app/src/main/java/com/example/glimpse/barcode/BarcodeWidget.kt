package com.example.glimpse.barcode

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glimpse.SharedViewModel
import com.example.glimpse.camera.CameraAnalyzer
import com.google.mlkit.vision.barcode.common.Barcode
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/*
FR13 - Barcode.Widget
FR14 - Barcode.Lookup
Classes and composable for barcode scanning widget.
 */

@Composable
fun BarcodeWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var scannedBarcode by remember { mutableStateOf("") }

    val analyzer = remember {
        BarcodeCameraFeedAnalyzer(context, sharedViewModel) { result ->
            if (result.detected) {
                when (result.barcode?.valueType) {
                    Barcode.TYPE_ISBN -> {
                        // FR14 - Barcode.Lookup: Gets associated barcode info from isbn lookup
                        result.barcode.rawValue?.let {
                            lookupIsbn(context, it) { title ->
                                scannedBarcode = title ?: ""
                            }
                        }
                    }
                    Barcode.TYPE_PRODUCT -> {
                        // FR14 - Barcode.Lookup: Gets associated barcode info from UPC lookup
                        result.barcode.rawValue?.let {
                            lookupProductByUPC(context, it) { title ->
                                scannedBarcode = title ?: ""
                            }
                        }
                    }
                    else -> scannedBarcode = result.barcode?.rawValue ?: ""
                }
            } else {
                scannedBarcode = ""
            }
        }
    }

    // Use the generalized CameraAnalyzer
    CameraAnalyzer(analyzer = analyzer)

    // Display the scanned barcode
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(0.dp, 0.dp, 0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = scannedBarcode,
                style = TextStyle(
                    color = sharedViewModel.hudForegroundColor,
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = sharedViewModel.selectedFont.fontResource,
                )
            )
        }
    }
}

// FR14 - Barcode.Lookup: Looks up information for book barcodes
fun lookupIsbn(context: Context, isbn: String, onResult: (String?) -> Unit) {
    // Open Library query https://openlibrary.org/dev/docs/api/books
    val url = "https://openlibrary.org/api/books?bibkeys=ISBN:$isbn&format=json&jscmd=data"
    val request = Request.Builder()
        .url(url)
        .header("Cache-Control", "public, max-age=86400") // Cache for 24 hours
        .build()

    createCachedHttpClient(context).newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            e.printStackTrace()
            onResult(isbn)
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                response.body?.string()?.let { body ->
                    val bookJson = JSONObject(body).optJSONObject("ISBN:$isbn")

                    if (bookJson != null) {
                        val title = bookJson.optString("title") ?: "Unknown"
                        val author =
                            bookJson.optJSONArray("authors")?.getJSONObject(0)?.optString("name")
                                ?: "Unknown"
                        onResult("Title: $title\nAuthor: $author")
                    } else {
                        onResult("Book not found, $isbn")
                    }
                }
            } else {
                Log.e("Barcode", "Error: ${response.code}")
                onResult(isbn)
            }
        }
    })
}

// FR14 - Barcode.Lookup: Looks up information for upc barcodes
fun lookupProductByUPC(context: Context, upc: String, onResult: (String?) -> Unit) {
    val request = Request.Builder()
        .url("https://api.upcitemdb.com/prod/trial/lookup?upc=$upc")
        .header("Cache-Control", "public, max-age=86400") // Cache for 24 hours
        .build()

    createCachedHttpClient(context).newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            onResult(upc)
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                response.body?.string()?.let { body ->
                    val json = JSONObject(body).getJSONArray("items")
                    if (json.length() > 0) {
                        val item = json.getJSONObject(0)
                        val title = item.getString("title")
                        onResult(title)
                    } else {
                        onResult("Product not found, $upc")
                    }
                }
            } else {
                Log.e("Barcode", "Error: ${response.code}")
                onResult(upc)
            }
        }
    })
}

fun createCachedHttpClient(context: Context): OkHttpClient {
    // 10 MB cache, use this to prevent making too many requests
    val cacheSize = 10L * 1024 * 1024
    val cache = Cache(context.cacheDir, cacheSize)

    // Ensure caching even if not supported by server
    val cacheInterceptor = okhttp3.Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val cacheControl = okhttp3.CacheControl.Builder()
            .maxAge(1, TimeUnit.DAYS)
            .build()
        response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
    return OkHttpClient.Builder()
        .cache(cache)
        .addNetworkInterceptor(cacheInterceptor)
        .build()
}