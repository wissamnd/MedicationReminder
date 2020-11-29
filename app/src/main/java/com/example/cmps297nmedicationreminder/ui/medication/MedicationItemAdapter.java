package com.example.cmps297nmedicationreminder.ui.medication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmps297nmedicationreminder.MainActivity;
import com.example.cmps297nmedicationreminder.MedicationDetailsActivity;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class MedicationItemAdapter extends RecyclerView.Adapter<MedicationItemAdapter.MedicationsViewHolder> {


    private final ArrayList<MedicationItem> medicationItems;
    private Context context;
    private static ClickListener clickListener;


    public MedicationItemAdapter(Context context, ArrayList<MedicationItem> medicationItems) {
        this.medicationItems = medicationItems;
        this.context = context;
    }


    class MedicationsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {


        public final TextView medicationName, medicationInstructions;
        final MedicationItemAdapter mAdapter;

        public MedicationsViewHolder(View itemView, MedicationItemAdapter adapter) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medication_item_name);
            medicationInstructions = itemView.findViewById(R.id.medication_item_instructions);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) { }
    }


    @NonNull
    @Override
    public MedicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mItemView = mInflater.inflate(
                R.layout.medication_item, parent, false);

        return new MedicationsViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationsViewHolder holder, int position) {
        // Retrieve the data for that position.
        MedicationItem mCurrent = medicationItems.get(position);
        // Add the data to the view holder.
        holder.medicationName.setText(mCurrent.name);

        String instructionStatement = mCurrent.strength +"mg";
        String foodInstruction = mCurrent.getInstruction();
        if(foodInstruction.length() >0){
            instructionStatement+= ", "+foodInstruction;
        }
        holder.medicationInstructions.setText(instructionStatement);
    }

    @Override
    public int getItemCount() {
        return medicationItems.size();
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        MedicationItemAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(View v, int position);
    }


}
