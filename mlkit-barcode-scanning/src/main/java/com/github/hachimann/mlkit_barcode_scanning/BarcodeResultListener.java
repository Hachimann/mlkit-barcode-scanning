package com.github.hachimann.mlkit_barcode_scanning;

import com.google.mlkit.vision.barcode.Barcode;

public interface BarcodeResultListener {
    void onBarcodeResult(Barcode barcode);
}
