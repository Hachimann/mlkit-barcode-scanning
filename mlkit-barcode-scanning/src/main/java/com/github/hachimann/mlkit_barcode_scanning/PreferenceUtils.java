package com.github.hachimann.mlkit_barcode_scanning;

import android.graphics.RectF;

import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;
import com.google.mlkit.vision.barcode.Barcode;

public class PreferenceUtils {
    private static boolean enableBarcodeSizeCheck = false;
    private static int barcodeReticleWidth = 60;
    private static int barcodeReticleHeight = 30;
    private static int minimumBarcodeWidth = 50;
    private static boolean delayLoadingBarcodeResult = false;

    public static void setEnableBarcodeSizeCheck(boolean enableBarcodeSizeCheck) {
        PreferenceUtils.enableBarcodeSizeCheck = enableBarcodeSizeCheck;
    }

    public static void setBarcodeReticleWidth(int barcodeReticleWidth) {
        PreferenceUtils.barcodeReticleWidth = barcodeReticleWidth;
    }

    public static void setBarcodeReticleHeight(int barcodeReticleHeight) {
        PreferenceUtils.barcodeReticleHeight = barcodeReticleHeight;
    }

    public static void setMinimumBarcodeWidth(int minimumBarcodeWidth) {
        PreferenceUtils.minimumBarcodeWidth = minimumBarcodeWidth;
    }

    public static void setDelayLoadingBarcodeResult(boolean delayLoadingBarcodeResult) {
        PreferenceUtils.delayLoadingBarcodeResult = delayLoadingBarcodeResult;
    }

    public static Float getProgressToMeetBarcodeSizeRequirement(GraphicOverlay overlay, Barcode barcode) {
        if (enableBarcodeSizeCheck) {
            float reticleBoxWidth = getBarcodeReticleBox(overlay).width();
            float barcodeWidth_ = 0f;
            if (barcode.getBoundingBox() != null)
                barcodeWidth_ = (float) (barcode.getBoundingBox().width() * 1.0);
            float barcodeWidth = overlay.translateX(barcodeWidth_);
            float requiredWidth = reticleBoxWidth * minimumBarcodeWidth / 100;
            return Math.min(barcodeWidth / requiredWidth, 1f);
        } else {
            return 1f;
        }
    }

    public static RectF getBarcodeReticleBox(GraphicOverlay overlay) {
        float overlayWidth = (float) overlay.getWidth();
        float overlayHeight = (float) overlay.getHeight();
        float boxWidth = overlayWidth * barcodeReticleWidth / 100;
        float boxHeight = overlayHeight * barcodeReticleHeight / 100;
        float cx = overlayWidth / 2;
        float cy = overlayHeight / 2;
        return new RectF(
                cx - boxWidth / 2,
                cy - boxHeight / 2,
                cx + boxWidth / 2,
                cy + boxHeight / 2
        );
    }

    public static boolean shouldDelayLoadingBarcodeResult() {
        return delayLoadingBarcodeResult;
    }
}
