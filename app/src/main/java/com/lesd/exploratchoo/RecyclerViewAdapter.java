package com.lesd.exploratchoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesd.exploratchoo.Api.models.ArrDep;

import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>
{
    private ArrayList<ArrDep> listElements;

    public RecyclerViewAdapter(ArrDep[] baseElements)
    {
        this.listElements = new ArrayList<>();

        this.listElements.addAll(Arrays.asList(baseElements));
    }

    public void changeList(ArrDep[] newElements)
    {
        this.listElements.clear();
        this.listElements.addAll(Arrays.asList(newElements));

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        ArrDep item = this.listElements.get(position);

        holder.provenance.setText(item.display_informations.label);
        holder.type.setText(item.display_informations.physical_mode);
        holder.Depart.setText(item.stop_date_time.getDepartureDateTime()
                .format(new DateTimeFormatterBuilder()
                        .appendValue(ChronoField.YEAR)
                        .appendLiteral("/")
                        .appendValue(ChronoField.MONTH_OF_YEAR)
                        .appendLiteral("/")
                        .appendValue(ChronoField.DAY_OF_MONTH)
                        .appendLiteral(" ")
                        .appendValue(ChronoField.HOUR_OF_DAY)
                        .appendLiteral(":")
                        .appendValue(ChronoField.MINUTE_OF_HOUR)
                        .toFormatter()));
        holder.Arrivee.setText(item.stop_date_time.getArrivalDateTime()
                .format(new DateTimeFormatterBuilder()
                        .appendValue(ChronoField.YEAR)
                        .appendLiteral("/")
                        .appendValue(ChronoField.MONTH_OF_YEAR)
                        .appendLiteral("/")
                        .appendValue(ChronoField.DAY_OF_MONTH)
                        .appendLiteral(" ")
                        .appendValue(ChronoField.HOUR_OF_DAY)
                        .appendLiteral(":")
                        .appendValue(ChronoField.MINUTE_OF_HOUR)
                        .toFormatter()));
    }

    @Override
    public int getItemCount()
    {
        return this.listElements.size();
    }
}
