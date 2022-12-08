package com.lesd.exploratchoo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder
{

    public final TextView provenance;
    public final TextView type;

    public ItemViewHolder(@NonNull View itemView)
    {
        super(itemView);

        provenance = itemView.findViewById(R.id.provenance);
        type = itemView.findViewById(R.id.type);
    }
}
