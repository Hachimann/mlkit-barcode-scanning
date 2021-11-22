package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.hachimann.mlkit_barcode_scanning.R;
import com.github.hachimann.mlkit_barcode_scanning.camera.WorkflowModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Displays the bottom sheet to present barcode fields contained in the detected barcode.
 */
public final class BarcodeResultFragment extends BottomSheetDialogFragment {
    private static final String TAG = "BarcodeResultFragment";
    private static final String ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list";

    @NotNull
    @Override
    public View onCreateView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup,
                             @Nullable Bundle bundle) {

        View view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup);

        Bundle arguments = getArguments();
        ArrayList<BarcodeField> barcodeFieldList;
        if (arguments != null && arguments.containsKey(ARG_BARCODE_FIELD_LIST)) {
            barcodeFieldList = arguments.getParcelableArrayList(ARG_BARCODE_FIELD_LIST);
            if (barcodeFieldList == null)
                barcodeFieldList = new ArrayList<>();
        } else {
            Log.e(TAG, "No barcode field list passed in!");
            barcodeFieldList = new ArrayList<>();
        }

        RecyclerView recyclerView = view.findViewById(R.id.barcode_field_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BarcodeFieldAdapter(barcodeFieldList));

        return view;
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialogInterface) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            // Back to working state after the bottom sheet is dismissed.
            ViewModelProviders.of(fragmentActivity).get(WorkflowModel.class)
                    .setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
        }
        super.onDismiss(dialogInterface);
    }

    public static void show(@NotNull FragmentManager fragmentManager,
                            @NotNull ArrayList<BarcodeField> barcodeFieldArrayList) {
        BarcodeResultFragment barcodeResultFragment = new BarcodeResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_BARCODE_FIELD_LIST, barcodeFieldArrayList);
        barcodeResultFragment.setArguments(bundle);
        barcodeResultFragment.show(fragmentManager, TAG);
    }

    public static void dismiss(@NotNull FragmentManager fragmentManager) {
        BarcodeResultFragment barcodeResultFragment =
                (BarcodeResultFragment) fragmentManager.findFragmentByTag(TAG);
        if (barcodeResultFragment != null) {
            barcodeResultFragment.dismiss();
        }
    }
}
