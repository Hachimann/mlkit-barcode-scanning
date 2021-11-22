package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.hachimann.mlkit_barcode_scanning.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Presents a list of field info in the detected barcode.
 */
public final class BarcodeFieldAdapter extends RecyclerView.Adapter<BarcodeFieldAdapter.BarcodeFieldViewHolder> {
    private final List<BarcodeField> barcodeFieldList;

    public BarcodeFieldAdapter(@NotNull List<BarcodeField> barcodeFieldList) {
        this.barcodeFieldList = barcodeFieldList;
    }

    @NonNull
    @Override
    public BarcodeFieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BarcodeFieldViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeFieldViewHolder holder, int position) {
        holder.bindBarcodeField(barcodeFieldList.get(position));
    }

    @Override
    public int getItemCount() {
        return barcodeFieldList.size();
    }

    public static final class BarcodeFieldViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelView;
        private final TextView valueView;

        public final void bindBarcodeField(@NotNull BarcodeField barcodeField) {
            labelView.setText(barcodeField.getLabel());
            valueView.setText(barcodeField.getValue());
        }

        private BarcodeFieldViewHolder(View view) {
            super(view);
            labelView = view.findViewById(R.id.barcode_field_label);
            valueView = view.findViewById(R.id.barcode_field_value);
        }

        @NotNull
        public static BarcodeFieldViewHolder create(@NotNull ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.barcode_field, parent, false);
            return new BarcodeFieldViewHolder(view);
        }
    }
}
