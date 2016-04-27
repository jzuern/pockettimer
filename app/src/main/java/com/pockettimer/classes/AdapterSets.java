package com.pockettimer.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pockettimer.R;

import java.util.ArrayList;

public class AdapterSets extends ArrayAdapter<SetClass> {

    Context context;
    int layoutResourceId;
    ArrayList<SetClass> sets;

    public AdapterSets(Context context,int layoutResourceId, ArrayList<SetClass> sets) {
        super(context, layoutResourceId, sets);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.sets = sets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SetClass set = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.set_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvLength = (TextView) convertView.findViewById(R.id.tvDuration);
        TextView tvReps = (TextView) convertView.findViewById(R.id.tvReps);
        TextView tvLengthT = (TextView) convertView.findViewById(R.id.tvLengthTotal);

        // Populate the data into the template view using the data object
        tvName.setText(set.getName());
        tvLength.setText(context.getResources().getString(R.string.length) + " " + set.getmLengthFormatted());

        tvReps.setText(context.getResources().getString(R.string.length) + " " + set.getRounds());
        if (set.getRounds() == 1 ){
            tvReps.setText(set.getRounds()+ " " + context.getResources().getString(R.string.repetition));
        }
        else tvReps.setText(set.getRounds()+ " " + context.getResources().getString(R.string.repetitions));

        tvLengthT.setText(context.getResources().getString(R.string.total_length)+ " " + set.getmTotalLengthFormatted());


        // Return the completed view to render on screen
        return convertView;
    }


    public SetClass getItem(int position){
        return sets.get(position);
    }
}