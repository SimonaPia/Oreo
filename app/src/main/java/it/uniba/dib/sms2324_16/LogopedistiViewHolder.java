package it.uniba.dib.sms2324_16;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LogopedistiViewHolder extends RecyclerView.ViewHolder {
    public TextView logopedistaNameTextView;
    public TextView idLogopedistaTextView;
    public RadioButton radioButton;

    public LogopedistiViewHolder(@NonNull View itemView) {
        super(itemView);
        idLogopedistaTextView = itemView.findViewById(R.id.idLogopedistaTextView);
        logopedistaNameTextView = itemView.findViewById(R.id.logopedistaNameTextView);
        radioButton = itemView.findViewById(R.id.radioButton);
    }
}
