package com.github.hachimann.mlkit_barcode_scanning;

import com.google.mlkit.vision.barcode.common.Barcode;

public interface BarcodeResultListener {
    void onBarcodeResult(Barcode barcode);
}
