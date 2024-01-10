package it.uniba.dib.sms2324_16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.Button;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private Context context;
    private List<Patient> patientList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAssignClick(Patient patient);
    }

    public PatientListAdapter(Context context, List<Patient> patientList, OnItemClickListener listener) {
        this.context = context;
        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.bind(patient, listener);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView patientName;
        private Button assignButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name_id);
            assignButton = itemView.findViewById(R.id.assignButton);
        }

        public void bind(final Patient patient, final OnItemClickListener listener) {
            patientName.setText(patient.getName());

            assignButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAssignClick(patient);
                }
            });
        }
    }
}


