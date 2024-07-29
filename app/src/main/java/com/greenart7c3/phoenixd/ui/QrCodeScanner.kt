package com.greenart7c3.phoenixd.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.Intents
import com.greenart7c3.phoenixd.databinding.ScanViewBinding
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun BoxScope.ScannerView(
    onScanViewBinding: (DecoratedBarcodeView) -> Unit,
    onScannedText: (String) -> Unit,
) {
    // scanner view using a legacy binding
    AndroidViewBinding(
        modifier = Modifier.fillMaxWidth(),
        factory = { inflater, viewGroup, attach ->
            val binding = ScanViewBinding.inflate(inflater, viewGroup, attach)
            binding.scanView.let { scanView ->
                scanView.initializeFromIntent(
                    Intent().apply {
                        putExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
                        putExtra(Intents.Scan.FORMATS, BarcodeFormat.QR_CODE.name)
                    },
                )
                scanView.decodeContinuous(object : BarcodeCallback {
                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) = Unit
                    override fun barcodeResult(result: BarcodeResult?) {
                        result?.text?.trim()?.takeIf { it.isNotBlank() }?.let {
                            scanView.pause()
                            onScannedText(it)
                        }
                    }
                })
                onScanViewBinding(scanView)
                scanView.resume()
            }
            binding
        },
    )
}

@Composable
fun SimpleQrCodeScanner(onScan: (String?) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val qrLauncher =
        rememberLauncherForActivityResult(ScanContract()) {
            if (it.contents != null) {
                onScan(it.contents)
            } else {
                onScan(null)
            }
        }

    val scanOptions =
        ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Point to the QR Code")
            setBeepEnabled(false)
            setOrientationLocked(false)
            addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
        }

    DisposableEffect(lifecycleOwner) {
        qrLauncher.launch(scanOptions)
        onDispose { }
    }
}
