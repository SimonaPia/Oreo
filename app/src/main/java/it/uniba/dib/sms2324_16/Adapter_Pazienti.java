package it.uniba.dib.sms2324_16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.content.Intent;

import android.util.Log;

public class Adapter_Pazienti extends RecyclerView.Adapter<Adapter_Pazienti.ViewHolder> {

    private ArrayList<Paziente> PazientiList;
    private OnItemClickListener listener;

    public Adapter_Pazienti(ArrayList<Paziente> PazientiList, OnItemClickListener listener) {
        this.PazientiList = PazientiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_lista_pazienti, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paziente paziente = PazientiList.get(position);
        holder.bind(paziente);

        holder.buttonVediProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(paziente);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return PazientiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeTextView;
        private TextView cognomeTextView;
        private Button buttonVediProfilo;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.nomeTextView);
            cognomeTextView = itemView.findViewById(R.id.cognomeTextView);
            buttonVediProfilo = itemView.findViewById(R.id.buttonVediProfilo);  // Inizializza il pulsante
        }

        public void bind(Paziente paziente) {
            // Aggiorna dinamicamente le viste con i dati del paziente
            nomeTextView.setText("Nome: " + paziente.getNome());
            cognomeTextView.setText("Cognome: " + paziente.getCognome());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Paziente paziente);
    }

    public void setPazientiList(ArrayList<Paziente> PazientiList) {
        this.PazientiList = PazientiList;
        notifyDataSetChanged();  // Notifica alla RecyclerView che i dati sono stati aggiornati
    }

}