package com.lesd.exploratchoo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder
{

    public final TextView provenance;
    public final TextView type;
    public final TextView Depart;
    public final TextView Arrivee;

    public ItemViewHolder(@NonNull View itemView)
    {
        super(itemView);

        provenance = itemView.findViewById(R.id.provenance);
        type = itemView.findViewById(R.id.type);
        Depart = itemView.findViewById(R.id.Depart);
        Arrivee = itemView.findViewById(R.id.Arrivee);
    }
}
