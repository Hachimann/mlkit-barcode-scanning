/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hachimann.mlkit_barcode_scanning.camera;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;

import com.github.hachimann.mlkit_barcode_scanning.ScopedExecutor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;

/**
 * Abstract base class for vision frame processors. Subclasses need to implement {@link
 * #onSuccess(Object, GraphicOverlay)} to define what they want to with the detection results and
 * {@link #detectInImage(InputImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
public abstract class FrameProcessorBase<T> implements FrameProcessor {

    private final ScopedExecutor executor;

    // Whether this processor is already shut down
    private boolean isShutdown;

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private ByteBuffer latestImage;

    @GuardedBy("this")
    private FrameMetadata latestImageMetaData;
    // To keep the images and metadata in process.
    @GuardedBy("this")
    private ByteBuffer processingImage;

    @GuardedBy("this")
    private FrameMetadata processingMetaData;

    protected FrameProcessorBase() {
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    // -----------------Code for processing live preview frame from Camera1 API-----------------------
    @Override
    public synchronized void processByteBuffer(
            ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
        latestImage = data;
        latestImageMetaData = frameMetadata;
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay);
        }
    }

    private synchronized void processLatestImage(final GraphicOverlay graphicOverlay) {
        processingImage = latestImage;
        processingMetaData = latestImageMetaData;
        latestImage = null;
        latestImageMetaData = null;
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage, processingMetaData, graphicOverlay);
        }
    }

    private void processImage(
            ByteBuffer frame, final FrameMetadata frameMetaData, final GraphicOverlay graphicOverlay) {

        InputImage image = InputImage.fromByteBuffer(
                frame,
                frameMetaData.getWidth(),
                frameMetaData.getHeight(),
                frameMetaData.getRotation(),
                InputImage.IMAGE_FORMAT_NV21
        );
        detectInImage(image).addOnSuccessListener(executor, results -> {
            FrameProcessorBase.this.onSuccess(results, graphicOverlay);
            processLatestImage(graphicOverlay);
        }).addOnFailureListener(executor, FrameProcessorBase.this::onFailure);
    }

    @Override
    public void stop() {
        executor.shutdown();
        isShutdown = true;
    }

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract void onSuccess(@NonNull T results, @NonNull GraphicOverlay graphicOverlay);

    protected abstract void onFailure(@NonNull Exception e);
}
