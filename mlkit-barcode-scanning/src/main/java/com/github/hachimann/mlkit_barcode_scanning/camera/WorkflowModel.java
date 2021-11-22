package com.github.hachimann.mlkit_barcode_scanning.camera;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.mlkit.vision.barcode.Barcode;

import org.jetbrains.annotations.NotNull;

public class WorkflowModel extends AndroidViewModel {

    private final MutableLiveData<WorkflowState> workflowState;
    private final MutableLiveData<Barcode> detectedBarcode;
    private boolean isCameraLive;

    public WorkflowModel(@NotNull Application application) {
        super(application);
        this.workflowState = new MutableLiveData<>();
        this.detectedBarcode = new MutableLiveData<>();
    }

    public final void markCameraLive() {
        this.isCameraLive = true;
    }

    public final void markCameraFrozen() {
        this.isCameraLive = false;
    }

    @MainThread
    public final void setWorkflowState(@NotNull WorkflowModel.WorkflowState workflowState) {
        this.workflowState.setValue(workflowState);
    }

    public final boolean isCameraLive() {
        return this.isCameraLive;
    }

    @NotNull
    public final MutableLiveData<WorkflowState> getWorkflowState() {
        return this.workflowState;
    }

    @NotNull
    public final MutableLiveData<Barcode> getDetectedBarcode() {
        return this.detectedBarcode;
    }

    public enum WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }
}
