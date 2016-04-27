package com.pockettimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.pockettimer.classes.AdapterWorkouts;
import com.pockettimer.classes.WorkoutClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class LoadWorkoutActivity extends AppCompatActivity {

    ArrayList<WorkoutClass> arrayList = new ArrayList<WorkoutClass>();
    AdapterWorkouts workoutAdapter;
    private ListView lv;
    WorkoutClass workoutItem;

    @Override
    public void onResume(){ // when resuming to the activity, reload all existing workouts (in case anything has changed)
        super.onResume();
        // SAME CODE AS IN onCreate!
        lv = (ListView) findViewById(R.id.listView1);

        // get SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson array_gson = new Gson();
        String array_json = sharedPrefs.getString("arraytag", null);
        Type array_type = new TypeToken<ArrayList<WorkoutClass>>() {}.getType();

        //get  ArrayList from Json
        if(array_json != null && !array_json.isEmpty()){
            arrayList = array_gson.fromJson(array_json, array_type);
        }
        //set up ArrayAdapter
        workoutAdapter = new AdapterWorkouts(this,R.layout.workout_item,arrayList);
        lv.setAdapter(workoutAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_workout);

        lv = (ListView) findViewById(R.id.listView1);

        // get SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor editor = sharedPrefs.edit();
        //editor.clear(); // for debugging
        //editor.commit();// for debugging

        Gson array_gson = new Gson();
        String array_json = sharedPrefs.getString("arraytag", null);
        Type array_type = new TypeToken<ArrayList<WorkoutClass>>() {}.getType();

        //get  ArrayList from Json
        if(array_json != null && !array_json.isEmpty()){
            arrayList = array_gson.fromJson(array_json, array_type);
        }

        //set up ArrayAdapter
        workoutAdapter = new AdapterWorkouts(this,R.layout.workout_item,arrayList);
        lv.setAdapter(workoutAdapter);

        // register for context menu
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long selectedItemId) {
                Intent intent = new Intent(LoadWorkoutActivity.this, WorkoutActivity.class);
                // add workout item to intent
                intent.putExtra("workoutObject", new Gson().toJson(workoutAdapter.getItem(position)));
                startActivity(intent);
            }
        });
    }

    void removeAll(){

        // get SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get  ArrayList from Json
        Gson array_gson = new Gson();
        String array_json = sharedPrefs.getString("arraytag", null);
        Type array_type = new TypeToken<ArrayList<WorkoutClass>>() {}.getType();
        if(array_json != null && !array_json.isEmpty()){
            arrayList = array_gson.fromJson(array_json, array_type);
        }

        arrayList.clear();

        //update SharedPreferences with new ArrayAdapter
        array_json = array_gson.toJson(arrayList);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("arraytag", array_json);

        editor.commit();

        lv.setAdapter(null);
        lv.setAdapter(new AdapterWorkouts(this,R.layout.workout_item,arrayList)); // refilling listview
        lv.invalidateViews();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.loadLayout), "All workouts removed", Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_load_workout, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_removeall:
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Do you really want to remove all workouts?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                removeAll();
                            }})
                        .setNegativeButton("No", null).show();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        // Get the info on which item was selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Retrieve the item that was clicked on
        workoutItem = workoutAdapter.getItem(info.position);

        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, getApplicationContext().getString(R.string.edit));
        menu.add(0, v.getId(), 0, getApplicationContext().getString(R.string.remove));
    }

    @Override // when item from context menu is clicked
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(getApplicationContext().getString(R.string.edit))) {
            Intent intent = new Intent(LoadWorkoutActivity.this, NewWorkoutActivity.class);
            intent.putExtra("workoutObject", new Gson().toJson(workoutItem));
            startActivity(intent);
        }
        else if (item.getTitle().equals(getApplicationContext().getString(R.string.remove))){

            String workoutName = workoutItem.getName();

            // get SharedPreferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            //get  ArrayList from Json
            Gson array_gson = new Gson();
            String array_json = sharedPrefs.getString("arraytag", null);

            arrayList.remove(workoutItem);

            //update SharedPreferences with new ArrayAdapter
            array_json = array_gson.toJson(arrayList);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("arraytag", array_json);
            editor.commit();

            lv.setAdapter(null); // THEN EMPTY YOUR ADAPTER
            lv.setAdapter(new AdapterWorkouts(this, R.layout.workout_item, arrayList)); // AT LAST REFILL YOUR LISTVIEW (Recursively)
            lv.invalidateViews();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.loadLayout), workoutName + " " + getApplicationContext().getString(R.string.has_been_removed), Snackbar.LENGTH_SHORT);
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