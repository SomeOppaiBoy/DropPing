package com.example.dropping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<String[]> contactsList;

    public ContactsAdapter(List<String[]> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] contact = contactsList.get(position);
        holder.nameText.setText(contact[0]);
        holder.phoneText.setText(contact[1]);
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, phoneText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contactName);
            phoneText = itemView.findViewById(R.id.contactPhone);
        }
    }
}