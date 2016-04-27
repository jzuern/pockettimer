package com.pockettimer.classes;

import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;

public class WorkoutClass implements Serializable {

    private String mName;
    private ArrayList<SetClass> mSets;

    public WorkoutClass(){
        this.mName = "[empty]";
    }

    public WorkoutClass(String workoutName, ArrayList<SetClass> sets){
        this.mName = workoutName;
        this.mSets = sets;
    }

    public void setName(String name) {
        this.mName = name;
    }
    public String getName(){
        return this.mName;
    }
    public String getSetCount(){
        return  Integer.toString(mSets.size());
    }

    public String getTotalLength(){
        int temp = 0;
        for (int i = 0; i < mSets.size(); i++){
            temp += mSets.get(i).getLength() *  mSets.get(i).getRounds();
            Log.d("tag",Integer.toString(temp));
        }
        return Integer.toString(temp);
    }

    public String getTotalLengthFormatted(){
        int temp = 0;
        for (int i = 0; i < mSets.size(); i++){
            temp += mSets.get(i).getLength() *  mSets.get(i).getRounds();
            Log.d("tag",Integer.toString(temp));
        }

        int secs = temp % 60;
        int mins = (temp-secs)/60;

        return String.valueOf(mins).format("%02d", mins) +":"+ String.valueOf(secs).format("%02d", secs);
    }

    public ArrayList<SetClass> getSets(){
        return mSets;
    }

}