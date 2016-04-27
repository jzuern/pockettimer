package com.pockettimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.pockettimer.classes.AdapterSets;
import com.pockettimer.classes.SetClass;
import com.pockettimer.classes.WorkoutClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class NewWorkoutActivity extends AppCompatActivity {

    public ListView list;
    EditText tvWorkoutName;

    ArrayList<SetClass> arraySets = new ArrayList<SetClass>();
    AdapterSets setAdapter;
    ArrayList<WorkoutClass> arrayWorkouts = new ArrayList<WorkoutClass>();
    //AdapterWorkouts workoutAdapter;

    boolean existedBefore;
    Gson array_gson;
    String array_json;
    SharedPreferences sharedPrefs;

    //Creating a shared preference
    //SharedPreferences mPrefs;

    WorkoutClass workout;
    SetClass setItem;

    String workoutName;
    boolean editedWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        tvWorkoutName = (EditText) findViewById(R.id.setWorkoutName);
        list = (ListView) findViewById(R.id.list);

        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("workoutObject");
            workout = new Gson().fromJson(jsonMyObject, WorkoutClass.class);
            workoutName = workout.getName();
            arraySets = workout.getSets();
            tvWorkoutName.setText(workoutName);
            editedWorkout = true; // workout was loaded and not new created
        }

        //set up ArrayAdapter
        setAdapter = new AdapterSets(this, R.layout.set_item, arraySets);
        list.setAdapter(setAdapter);

        // register for context menu
        registerForContextMenu(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_workout, menu);
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                addSet();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addSet() {

        final Dialog dialog = new Dialog(NewWorkoutActivity.this);

        //setting custom layout to dialog

        dialog.setContentView(R.layout.new_set_dialog);
        dialog.setTitle(R.string.add_set);

        final EditText setName = (EditText) dialog.findViewById(R.id.setname);

        final NumberPicker npMinutes = (NumberPicker) dialog.findViewById(R.id.numberPickerMinutes);
        final NumberPicker npSeconds = (NumberPicker) dialog.findViewById(R.id.numberPickerSeconds);
        final NumberPicker npRounds = (NumberPicker) dialog.findViewById(R.id.numberPickerRepetitions);

        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);
        npMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npMinutes.setWrapSelectorWheel(false);

        npSeconds.setMaxValue(59);
        npSeconds.setMinValue(0);
        npSeconds.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npSeconds.setWrapSelectorWheel(false);

        npRounds.setMaxValue(100);
        npRounds.setMinValue(1);
        npRounds.setWrapSelectorWheel(false);

        //adding button click event
        Button dismissButton = (Button) dialog.findViewById(R.id.buttonDismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button addButton = (Button) dialog.findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = setName.getText().toString();
                String durationFormatted = String.valueOf(npMinutes.getValue()).format("%02d", npMinutes.getValue()) + ":" +
                        String.valueOf(npSeconds.getValue()).format("%02d", npSeconds.getValue());
                int duration = 60*npMinutes.getValue() + npSeconds.getValue();
                int rounds = npRounds.getValue();

                SetClass set = new SetClass(name, durationFormatted, duration, rounds);
                arraySets.add(set);


                list.setAdapter(null);
                list.setAdapter(new AdapterSets(getApplicationContext(), R.layout.set_item, arraySets));

                setAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void saveWorkout(View view) {

        workoutName = tvWorkoutName.getText().toString();

        if (arraySets.size() == 0) {
            new AlertDialog.Builder(this).setTitle("Warning").setMessage(getApplicationContext().getResources().getString(R.string.arrayempty))
                    .setNeutralButton("Ok", null).show();
        }
        else {
            if(workoutName != null && !workoutName.isEmpty()){

                // get SharedPreferences
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //get Workout ArrayList from Json
                array_gson = new Gson();
                array_json = sharedPrefs.getString("arraytag", null);
                Type array_type = new TypeToken<ArrayList<WorkoutClass>>() {}.getType();
                if(array_json != null && !array_json.isEmpty()){
                    arrayWorkouts = array_gson.fromJson(array_json, array_type);
                }
                else{
                }

                final WorkoutClass workout = new WorkoutClass(workoutName,arraySets);
                existedBefore = false;
                for ( int c = 0; c < arrayWorkouts.size(); c++){
                    final int loc_c = c;
                    if (workoutName.equals(arrayWorkouts.get(c).getName())){
                        existedBefore = true;
                        new android.support.v7.app.AlertDialog.Builder(this)
                                .setTitle(getApplicationContext().getResources().getString(R.string.warning))
                                .setMessage(getApplicationContext().getResources().getString(R.string.q1) + workoutName + getApplicationContext().getResources().getString(R.string.q2))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(getApplicationContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        arrayWorkouts.set(loc_c , workout);

                                        //update SharedPreferences with new Workout ArrayList
                                        array_json = array_gson.toJson(arrayWorkouts);
                                        SharedPreferences.Editor editor = sharedPrefs.edit();
                                        editor.putString("arraytag", array_json);
                                        editor.commit();
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.test), workout.getName() + getApplicationContext().getResources().getString(R.string.overwritten),
                                                Snackbar.LENGTH_SHORT);
                                        View snackView = snackbar.getView();
                                        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(Color.WHITE);
                                        snackbar.show();
                                    }})
                                .setNegativeButton(R.string.no, null).show();
                    }
                }

                // add new workout to array of none of such a name existed before
                if(!existedBefore) {
                    //Log.e("tag", "existedBefore is " + existedBefore);
                    arrayWorkouts.add(workout);

                    //update SharedPreferences with new Workout ArrayList
                    array_json = array_gson.toJson(arrayWorkouts);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("arraytag", array_json);
                    editor.commit();

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.test), workout.getName() + " " + getApplicationContext().getResources().getString(R.string.saved),
                            Snackbar.LENGTH_SHORT);
                    View snackView = snackbar.getView();
                    TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
            else {
                new AlertDialog.Builder(this).setTitle("Error").setMessage(getApplicationContext().getResources().getString(R.string.not_empty))
                        .setNeutralButton("Ok", null).show();
            }
        }

    }

    public void startWorkout(View view){
        workoutName = tvWorkoutName.getText().toString();


        if (arraySets.size() == 0) {
            new AlertDialog.Builder(this).setTitle("Warning").setMessage(getApplicationContext().getResources().getString(R.string.arrayempty))
                    .setNeutralButton("Ok", null).show();
        }
        else {
            if(workoutName != null && !workoutName.isEmpty()){
                Intent intent = new Intent(NewWorkoutActivity.this, WorkoutActivity.class);
                WorkoutClass workout = new WorkoutClass(workoutName,arraySets);

                // add workout item to intent
                intent.putExtra("workoutObject", new Gson().toJson(workout));
                //intent.putExtras(workoutSpecifications);
                startActivity(intent);
            }
            else {
                new AlertDialog.Builder(this).setTitle("Error").setMessage(getApplicationContext().getResources().getString(R.string.not_empty))
                        .setNeutralButton("Ok", null).show();
            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        // Get the info on which item was selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Retrieve the item that was clicked on
        setItem = setAdapter.getItem(info.position);

        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, getApplicationContext().getString(R.string.edit));
        menu.add(0, v.getId(), 0, getApplicationContext().getString(R.string.remove));
    }


    @Override // when item from context menu is clicked
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(getApplicationContext().getString(R.string.edit))) {

            final Dialog dialog = new Dialog(NewWorkoutActivity.this);
            //setting custom layout to dialog

            dialog.setContentView(R.layout.new_set_dialog);
            dialog.setTitle("Edit set");

            final EditText setName = (EditText) dialog.findViewById(R.id.setname);
            setName.setText(setItem.getName());
            Log.e("test", "setName: " + setItem.getName());

            final NumberPicker npMinutes = (NumberPicker) dialog.findViewById(R.id.numberPickerMinutes);
            final NumberPicker npSeconds = (NumberPicker) dialog.findViewById(R.id.numberPickerSeconds);
            final NumberPicker npRounds = (NumberPicker) dialog.findViewById(R.id.numberPickerRepetitions);

            npMinutes.setMaxValue(59);
            npMinutes.setMinValue(0);
            npMinutes.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return String.format("%02d", i);
                }
            });
            npMinutes.setWrapSelectorWheel(false);
            npMinutes.setValue((setItem.getLength()/60));

            npSeconds.setMaxValue(59);
            npSeconds.setMinValue(0);
            npSeconds.setValue((setItem.getLength()%60));
            npSeconds.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return String.format("%02d", i);
                }
            });
            npSeconds.setWrapSelectorWheel(false);

            npRounds.setMaxValue(100);
            npRounds.setMinValue(1);
            npRounds.setValue(setItem.getRounds());
            npRounds.setWrapSelectorWheel(false);

            //adding button click event
            Button dismissButton = (Button) dialog.findViewById(R.id.buttonDismiss);
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button addButton = (Button) dialog.findViewById(R.id.buttonAdd);
            addButton.setText("Set");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = setName.getText().toString();
                    if(name != null && !name.isEmpty()){ // test if name is empty
                        String durationFormatted = String.valueOf(npMinutes.getValue()).format("%02d", npMinutes.getValue()) + ":" +
                                String.valueOf(npSeconds.getValue()).format("%02d", npSeconds.getValue());
                        int duration = 60*npMinutes.getValue() + npSeconds.getValue();

                        int rounds = npRounds.getValue();

                        setItem.setName(name);
                        setItem.setRounds(rounds);
                        setItem.setLength(duration);
                        setItem.setLengthFormatted(durationFormatted);

                        list.setAdapter(null);
                        list.setAdapter(new AdapterSets(getApplicationContext(), R.layout.set_item, arraySets));

                        setAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    else{
                        new AlertDialog.Builder(getApplicationContext()).setTitle("Error").setMessage("Set name must not be empty.").
                                setNegativeButton("OK", null).setPositiveButton("OK", null).setNeutralButton("No", null).show();
                    }
                }
            });
            dialog.show();
        }
        else if (item.getTitle().equals(getApplicationContext().getString(R.string.remove))){

            String setName = setItem.getName();
            arraySets.remove(setItem);
            list.setAdapter(null); // THEN EMPTY YOUR ADAPTER
            list.setAdapter(new AdapterSets(this, R.layout.set_item, arraySets));
            list.invalidateViews();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.test), setName + R.string.has_been_removed, Snackbar.LENGTH_SHORT);
            View snackView = snackbar.getView();
            TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        }
        else {
            return false;
        }
        return true;
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}