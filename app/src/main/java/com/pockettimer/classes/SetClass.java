package com.pockettimer.classes;

import java.io.Serializable;

public class SetClass implements Serializable {

    private String mName; // Name
    private String mLengthFormatted; // Length in mm:ss format
    private int mLength; // Length of Set
    private int mRounds; // number of repetitions


    public SetClass(){
        this.mName = "";
        this.mLengthFormatted = "00:00";
        this.mRounds = 1;
        this.mLength =  0;
    }

    public SetClass(String setName, String setLengthFormatted, int setLength, int setRounds){
        this.mName = setName;
        this.mLengthFormatted = setLengthFormatted;
        this.mLength = setLength;
        this.mRounds = setRounds;
    }

    public void setName(String name) {
        this.mName = name;}
    public void setRounds(int reps) {
        this.mRounds = reps;}
    public void setLength(int length) {
        this.mLength = length;}
    public void setLengthFormatted(String str) {
        this.mLengthFormatted = str;}
    public int getLength () {
        return this.mLength;}
    public String getName(){
        return this.mName;}
    public int getRounds(){
        return this.mRounds;}
    public String getmLengthFormatted(){
        return this.mLengthFormatted;}
    public String getmTotalLengthFormatted(){
        int totalLength = mLength*mRounds;
        int secs = totalLength % 60;
        int mins = (totalLength-secs)/60;
        return String.valueOf(mins).format("%02d", mins) +":"+ String.valueOf(secs).format("%02d", secs);
    }
}
