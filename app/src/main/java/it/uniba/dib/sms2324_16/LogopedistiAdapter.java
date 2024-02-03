package it.uniba.dib.sms2324_16;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogopedistiAdapter extends RecyclerView.Adapter<LogopedistiViewHolder> {
    private List<LogopedistiItem> logopedistiItemList;
    private List<Boolean> checkedList;

    public LogopedistiAdapter(List<LogopedistiItem> logopedistiItemList) {
        this.logopedistiItemList = logopedistiItemList;
        this.checkedList = new ArrayList<>(Collections.nCopies(logopedistiItemList.size(), false));
    }
    @NonNull
    @Override
    public LogopedistiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_logopedisti, parent, false);
        return new LogopedistiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogopedistiViewHolder holder, int position) {
        LogopedistiItem item = logopedistiItemList.get(position);

        holder.idLogopedistaTextView.setText("ID logopedista: " + item.getIdLogopedista());
        holder.logopedistaNameTextView.setText("Nome: " + item.getLogopedistaName());

        holder.radioButton.setChecked(item.isSelected());

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resetta lo stato di selezione per tutti gli elementi
                for (LogopedistiItem logopedista : logopedistiItemList) {
                    logopedista.setSelected(false);
                }

                // Imposta lo stato di selezione solo per l'elemento corrente
                item.setSelected(true);

                notifyDataSetChanged(); // Aggiorna la visualizzazione
            }
        });
    }

    @Override
    public int getItemCount() {
        return logopedistiItemList.size();
    }

    public List<LogopedistiItem> getSelectedItems() {
        List<LogopedistiItem> selectedItems = new ArrayList<>();

        for (LogopedistiItem logopedista : logopedistiItemList) {
            if (logopedista.isSelected()) {
                selectedItems.add(logopedista);
            }
        }

        return selectedItems;
    }

}
