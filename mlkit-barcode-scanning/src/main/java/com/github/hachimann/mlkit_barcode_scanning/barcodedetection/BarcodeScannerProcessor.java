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

package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import static com.github.hachimann.mlkit_barcode_scanning.PreferenceUtils.getProgressToMeetBarcodeSizeRequirement;
import static com.github.hachimann.mlkit_barcode_scanning.PreferenceUtils.shouldDelayLoadingBarcodeResult;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.hachimann.mlkit_barcode_scanning.camera.CameraReticleAnimator;
import com.github.hachimann.mlkit_barcode_scanning.camera.FrameProcessorBase;
import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;
import com.github.hachimann.mlkit_barcode_scanning.camera.WorkflowModel;
import com.github.hachimann.mlkit_barcode_scanning.camera.WorkflowModel.WorkflowState;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

/**
 * Barcode Detector.
 */
public class BarcodeScannerProcessor extends FrameProcessorBase<List<Barcode>> {

    private static final String TAG = "BarcodeProcessor";

    private final BarcodeScanner barcodeScanner;
    private final CameraReticleAnimator cameraReticleAnimator;
    private final WorkflowModel workflowModel;

    public BarcodeScannerProcessor(GraphicOverlay graphicOverlay, WorkflowModel workflowModel) {
        this.cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
        this.workflowModel = workflowModel;
        barcodeScanner = BarcodeScanning.getClient();
    }

    @Override
    protected Task<List<Barcode>> detectInImage(InputImage image) {
        return barcodeScanner.process(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<Barcode> barcodes, @NonNull GraphicOverlay graphicOverlay) {

        if (!workflowModel.isCameraLive()) return;

        Barcode barcodeInCenter = barcodes.stream().filter(barcode -> {
            Rect boundingBox = barcode.getBoundingBox();
            if (boundingBox == null) return false;
            RectF box = graphicOverlay.translateRect(boundingBox);
            return box.contains(
                    graphicOverlay.getWidth() / 2f,
                    graphicOverlay.getHeight() / 2f
            );
        }).findFirst().orElse(null);

        graphicOverlay.clear();
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start();
            graphicOverlay.add(new BarcodeReticleGraphic(graphicOverlay,
                    cameraReticleAnimator));
            workflowModel.setWorkflowState(WorkflowState.DETECTING);
        } else {
            cameraReticleAnimator.cancel();
            // Barcode size in the camera view is sufficient.
            Float sizeProgress = getProgressToMeetBarcodeSizeRequirement(graphicOverlay,
                    barcodeInCenter);
            if (sizeProgress < 1) {
                // Barcode in the camera view is too small, so prompt user to move camera closer.
                graphicOverlay.add(new BarcodeConfirmingGraphic(graphicOverlay,
                        barcodeInCenter));
                workflowModel.setWorkflowState(WorkflowState.CONFIRMING);
            } else {
                // Barcode size in the camera view is sufficient.
                if (shouldDelayLoadingBarcodeResult()) {
                    ValueAnimator loadingAnimator = createLoadingAnimator(graphicOverlay,
                            barcodeInCenter);
                    loadingAnimator.start();
                    graphicOverlay.add(new BarcodeLoadingGraphic(graphicOverlay,
                            loadingAnimator));
                    workflowModel.setWorkflowState(WorkflowState.SEARCHING);
                } else {
                    workflowModel.setWorkflowState(WorkflowState.DETECTED);
                    workflowModel.getDetectedBarcode().setValue(barcodeInCenter);
                }
            }
        }
        graphicOverlay.invalidate();
    }

    private ValueAnimator createLoadingAnimator(
            GraphicOverlay graphicOverlay,
            Barcode barcode
    ) {
        float endProgress = 1.1f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, endProgress);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            if ((float) valueAnimator1.getAnimatedValue() >= endProgress) {
                graphicOverlay.clear();
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHED);
                workflowModel.getDetectedBarcode().setValue(barcode);
            } else {
                graphicOverlay.invalidate();
            }
        });
        return valueAnimator;
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }

    @Override
    public void stop() {
        super.stop();
        barcodeScanner.close();
    }
}
