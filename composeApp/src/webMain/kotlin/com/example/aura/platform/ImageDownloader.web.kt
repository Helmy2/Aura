package com.example.aura.platform


import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.fetch.RequestInit
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise

actual class ImageDownloader {
    @OptIn(ExperimentalWasmJsInterop::class)
    actual suspend fun downloadImage(url: String, fileName: String): Boolean {
        return try {
            // 1. Fetch image
            val response = window.fetch(url,object : RequestInit {}).await()

            // 2. Convert to Blob
            val blob = response.blob().await()

            // 3. Create Object URL
            val objectUrl = URL.createObjectURL(blob)

            // 4. Create and Click Anchor
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = objectUrl
            anchor.download = "$fileName.jpg"
            anchor.style.display = "none"

            document.body?.appendChild(anchor)
            anchor.click()

            // 5. Cleanup
            document.body?.removeChild(anchor)
            URL.revokeObjectURL(objectUrl)

            true
        } catch (e: Exception) {
            println("Error downloading image: $e")
            false
        }
    }
}


@OptIn(ExperimentalWasmJsInterop::class)
suspend fun <T : JsAny?> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then(
        onFulfilled = {
            cont.resume(it)
            it
        },
        onRejected = {
            throw Throwable(it.toString())
        }
    )
}