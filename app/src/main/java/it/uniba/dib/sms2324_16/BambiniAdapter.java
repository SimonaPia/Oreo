package it.uniba.dib.sms2324_16;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BambiniAdapter extends RecyclerView.Adapter<BambiniViewHolder> {
    private List<BambiniItem> bambiniItemList;
    private List<Boolean> checkedList;

    public BambiniAdapter(List<BambiniItem> bambiniItemList) {
        this.bambiniItemList = bambiniItemList;
        this.checkedList = new ArrayList<>(Collections.nCopies(bambiniItemList.size(), false));
    }
    @NonNull
    @Override
    public BambiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_bambini, parent, false);
        return new BambiniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BambiniViewHolder holder, int position) {
        BambiniItem item = bambiniItemList.get(position);

        holder.childNameTextView.setText("Nome: " + item.getChildName());
        holder.childSurnameTextView.setText("Cognome: " + item.getChildSurname());

        holder.checkBox.setChecked(item.isChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return bambiniItemList.size();
    }

    public List<BambiniItem> getSelectedItems() {
        List<BambiniItem> selectedItems = new ArrayList<>();

        for (BambiniItem bambino : bambiniItemList) {
            if (bambino.isChecked()) {
                selectedItems.add(bambino);
            }
        }

        return selectedItems;
    }

}
