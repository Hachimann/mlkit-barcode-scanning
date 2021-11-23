package com.github.hachimann.mlkit_barcode_scanning;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.github.hachimann.mlkit_barcode_scanning.barcodedetection.BarcodeScannerProcessor;
import com.github.hachimann.mlkit_barcode_scanning.camera.CameraSource;
import com.github.hachimann.mlkit_barcode_scanning.camera.CameraSourcePreview;
import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;
import com.github.hachimann.mlkit_barcode_scanning.camera.WorkflowModel;
import com.google.android.material.chip.Chip;

import java.io.IOException;
import java.util.Objects;

public class BarcodeScan {
    private final Context context;

    private CameraSource cameraSource;
    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;

    private final CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private Chip promptChip;
    private AnimatorSet promptChipAnimator;
    private BarcodeResultListener barcodeResultListener;
    private BarcodeStringResultListener barcodeStringResultListener;

    private final int resIdPoint;
    private final int resIdCloser;
    private final int resIdSearching;

    private BarcodeScan(Builder builder) {
        this.context = builder.context;
        this.preview = builder.preview;
        this.graphicOverlay = builder.graphicOverlay;
        this.promptChip = builder.promptChip;
        this.promptChipAnimator = builder.promptChipAnimator;

        this.resIdPoint = builder.resIdPoint;
        this.resIdCloser = builder.resIdCloser;
        this.resIdSearching = builder.resIdSearching;

        apply();
    }

    private void setCameraSource() {
        cameraSource = new CameraSource((Activity) context, graphicOverlay);
    }

    private void apply() {
        if (graphicOverlay == null) {
            graphicOverlay = ((Activity) context).findViewById(R.id.camera_preview_graphic_overlay);
        }
        setCameraSource();
        if (promptChip == null) {
            promptChip = ((Activity) context).findViewById(R.id.bottom_prompt_chip);
        }
        if (promptChipAnimator == null) {
            promptChipAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                    R.animator.bottom_prompt_chip_enter);
        }
        promptChipAnimator.setTarget(promptChip);

        if (context instanceof BarcodeResultListener)
            barcodeResultListener = (BarcodeResultListener) context;
        if (context instanceof BarcodeStringResultListener)
            barcodeStringResultListener = (BarcodeStringResultListener) context;

        setUpWorkflowModel();
    }

    private void startCameraPreview() {
        if (workflowModel == null || cameraSource == null || preview == null) return;
        if (!workflowModel.isCameraLive()) {
            try {
                workflowModel.markCameraLive();
                preview.start(cameraSource);
            } catch (IOException e) {
                cameraSource.release();
                this.cameraSource = null;
            }
        }
    }

    public void stopCameraPreview() {
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        if (workflowModel == null) return;
        if (workflowModel.isCameraLive()) {
            workflowModel.markCameraFrozen();
            preview.stop();
        }
    }

    public void startCameraSource() throws Exception {
        if (cameraSource == null)
            throw new Exception("CameraSourcePreview is NULL");
        workflowModel.markCameraFrozen();
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        cameraSource.setFrameProcessor(new BarcodeScannerProcessor(graphicOverlay,
                workflowModel));
        workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
    }

    public void stopCameraSource() {
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    @SuppressWarnings("deprecation")
    public void enableFlash(boolean isEnabled) {
        if (cameraSource == null)
            return;
        if (isEnabled)
            cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        else
            cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    }

    private void setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of((FragmentActivity) context).get(WorkflowModel.class);

        workflowModel.getWorkflowState().observe((LifecycleOwner) context, workflowState -> {
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return;
            }
            currentWorkflowState = workflowState;
            boolean wasPromptChipGone = promptChip.getVisibility() == View.GONE;

            switch (workflowState) {
                case DETECTING: {
                    promptChip.setVisibility(View.VISIBLE);
                    promptChip.setText(resIdPoint == 0 ? R.string.prompt_point_at_a_barcode : resIdPoint);
                    startCameraPreview();
                    break;
                }
                case CONFIRMING: {
                    promptChip.setVisibility(View.VISIBLE);
                    promptChip.setText(resIdCloser == 0 ? R.string.prompt_move_camera_closer : resIdCloser);
                    startCameraPreview();
                    break;
                }
                case SEARCHING: {
                    promptChip.setVisibility(View.VISIBLE);
                    promptChip.setText(resIdSearching == 0 ? R.string.prompt_searching : resIdSearching);
                    stopCameraPreview();
                    break;
                }
                case DETECTED:
                case SEARCHED: {
                    promptChip.setVisibility(View.GONE);
                    stopCameraPreview();
                    break;
                }
                default: {
                    promptChip.setVisibility(View.GONE);
                    break;
                }
            }

            boolean shouldPlayPromptChipEnteringAnimation = wasPromptChipGone
                    && promptChip.getVisibility() == View.VISIBLE;
            if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning())
                promptChipAnimator.start();
        });

        workflowModel.getDetectedBarcode().observe((LifecycleOwner) context, barcode -> {
            if (barcode != null) {
                if (context instanceof BarcodeResultListener)
                    barcodeResultListener.onBarcodeResult(barcode);
                if (context instanceof BarcodeStringResultListener)
                    barcodeStringResultListener.onBarcodeStringResult(barcode.getRawValue());
            }
        });
    }

    public static class Builder {
        private final Context context;
        private final CameraSourcePreview preview;
        private GraphicOverlay graphicOverlay;
        private Chip promptChip;
        private AnimatorSet promptChipAnimator;
        private int resIdPoint;
        private int resIdCloser;
        private int resIdSearching;

        public Builder(Context context, CameraSourcePreview preview) {
            this.context = context;
            this.preview = preview;
        }

        public Builder setGraphicOverlay(GraphicOverlay graphicOverlay) {
            this.graphicOverlay = graphicOverlay;
            return this;
        }

        public Builder setPromptChip(Chip promptChip) {
            this.promptChip = promptChip;
            return this;
        }

        public Builder setPromptChipAnimator(AnimatorSet promptChipAnimator) {
            this.promptChipAnimator = promptChipAnimator;
            return this;
        }

        public Builder setEnableBarcodeSizeCheck(boolean enableBarcodeSizeCheck) {
            PreferenceUtils.setEnableBarcodeSizeCheck(enableBarcodeSizeCheck);
            return this;
        }

        public Builder setBarcodeReticleWidth(int barcodeReticleWidth) {
            PreferenceUtils.setBarcodeReticleWidth(barcodeReticleWidth);
            return this;
        }

        public Builder setBarcodeReticleHeight(int barcodeReticleHeight) {
            PreferenceUtils.setBarcodeReticleHeight(barcodeReticleHeight);
            return this;
        }

        public Builder setMinimumBarcodeWidth(int minimumBarcodeWidth) {
            PreferenceUtils.setMinimumBarcodeWidth(minimumBarcodeWidth);
            return this;
        }

        public Builder setDelayLoadingBarcodeResult(boolean delayLoadingBarcodeResult) {
            PreferenceUtils.setDelayLoadingBarcodeResult(delayLoadingBarcodeResult);
            return this;
        }

        public BarcodeScan build() throws Exception {
            BarcodeScan barcodeScan = new BarcodeScan(this);
            validateUserObject(barcodeScan);
            return barcodeScan;
        }

        public Builder setTextPointCamera(int resIdPoint) {
            this.resIdPoint = resIdPoint;
            return this;
        }

        public Builder setTextMoveCloser(int resIdCloser) {
            this.resIdCloser = resIdCloser;
            return this;
        }

        public Builder setTextSearching(int resIdSearching) {
            this.resIdSearching = resIdSearching;
            return this;
        }

        private void validateUserObject(BarcodeScan barcodeScan) throws Exception {
            if (barcodeScan.context == null || barcodeScan.preview == null)
                throw new Exception("Context or CameraSourcePreview is NULL");
        }
    }
}