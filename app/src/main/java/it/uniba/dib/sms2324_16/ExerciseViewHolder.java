package it.uniba.dib.sms2324_16;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    public TextView childNameTextView;
    public TextView exerciseTypeTextView;
    public TextView completedStatusTextView;

    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        childNameTextView = itemView.findViewById(R.id.childNameTextView);
        exerciseTypeTextView = itemView.findViewById(R.id.exerciseTypeTextView);
        completedStatusTextView = itemView.findViewById(R.id.completedStatusTextView);
    }
}

