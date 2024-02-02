package it.uniba.dib.sms2324_16;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class BambiniViewHolder extends RecyclerView.ViewHolder {
    public TextView childNameTextView;
    public TextView childSurnameTextView;
    public CheckBox checkBox;

    public BambiniViewHolder(@NonNull View itemView) {
        super(itemView);
        childNameTextView = itemView.findViewById(R.id.childNameTextView);
        childSurnameTextView = itemView.findViewById(R.id.childSurnameTextView);
        checkBox = itemView.findViewById(R.id.checkbox);
    }
}
