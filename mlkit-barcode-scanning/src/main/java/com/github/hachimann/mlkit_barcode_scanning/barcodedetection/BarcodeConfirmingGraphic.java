package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import static com.github.hachimann.mlkit_barcode_scanning.PreferenceUtils.getProgressToMeetBarcodeSizeRequirement;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;
import com.google.mlkit.vision.barcode.Barcode;

import org.jetbrains.annotations.NotNull;

public final class BarcodeConfirmingGraphic extends BarcodeGraphicBase {
    private final Barcode barcode;

    public BarcodeConfirmingGraphic(@NotNull GraphicOverlay overlay, @NotNull Barcode barcode) {
        super(overlay);
        this.barcode = barcode;
    }

    public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);

        // Draws a highlighted path to indicate the current progress to meet size requirement.
        float sizeProgress = getProgressToMeetBarcodeSizeRequirement(overlay, barcode);
        Path path = new Path();
        if (sizeProgress > 0.95F) {
            // To have a completed path with all corners rounded.
            path.moveTo(getBoxRect().left, getBoxRect().top);
            path.lineTo(getBoxRect().right, getBoxRect().top);
            path.lineTo(getBoxRect().right, getBoxRect().bottom);
            path.lineTo(getBoxRect().left, getBoxRect().bottom);
            path.close();
        } else {
            path.moveTo(getBoxRect().left, getBoxRect().top + getBoxRect().height() * sizeProgress);
            path.lineTo(getBoxRect().left, getBoxRect().top);
            path.lineTo(getBoxRect().left + getBoxRect().width() * sizeProgress, getBoxRect().top);

            path.moveTo(getBoxRect().right, getBoxRect().bottom - getBoxRect().height() * sizeProgress);
            path.lineTo(getBoxRect().right, getBoxRect().bottom);
            path.lineTo(getBoxRect().right - getBoxRect().width() * sizeProgress, getBoxRect().bottom);
        }
        canvas.drawPath(path, getPathPaint());
    }
}
