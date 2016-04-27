package com.pockettimer.classes;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pockettimer.R;
import java.util.ArrayList;

public class AdapterWorkouts extends ArrayAdapter<WorkoutClass> {

    Context context;
    int layoutResourceId;
    ArrayList<WorkoutClass> workouts;

    public AdapterWorkouts(Context context,int layoutResourceId, ArrayList<WorkoutClass> workouts) {
        super(context, layoutResourceId, workouts);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WorkoutClass workout = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workout_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvSets = (TextView) convertView.findViewById(R.id.tvSets);
        TextView tvTotalLength = (TextView) convertView.findViewById(R.id.tvTotalLength);

        // Populate the data into the template view using the data object
        tvName.setText(workout.getName());
        tvTotalLength.setText(context.getResources().getString(R.string.length) + " " + workout.getTotalLengthFormatted());
        if (Integer.parseInt(workout.getSetCount()) == 1){
            tvSets.setText(workout.getSetCount() + " " + context.getResources().getString(R.string.set));
        }
        else tvSets.setText(workout.getSetCount() + " " + context.getResources().getString(R.string.sets));

        // Return the completed view to render on screen
        return convertView;
    }


    public WorkoutClass getItem(int position){
        return workouts.get(position);
    }
}