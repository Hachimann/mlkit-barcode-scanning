package com.github.hachimann.mlkit_barcode_scanning.camera;

import com.google.mlkit.common.MlKitException;

import java.nio.ByteBuffer;

public interface FrameProcessor {

    /**
     * Processes the input frame with the underlying detector.
     */
    void processByteBuffer(
            ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay)
            throws MlKitException;

    /**
     * Stops the underlying detector and release resources.
     */
    void stop();
}
