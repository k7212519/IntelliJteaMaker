package com.xzw.intellijteamaker;

/**
 * Created by 7212519 on 2018/4/11.
 */

public class Tea {
    private String name;
    private int temperature;
    private int washTime;
    private int makeTime;
    private int makeFrequency;

    public Tea(){
        temperature = 0;
        washTime = 0;
        makeTime = 0;
        makeFrequency = 0;


    }

    public String getName(){
        return name;
    }

    public int getTemperature(){
        return temperature;
    }

    public int getWashTime(){
        return washTime;
    }

    public int getMakeTime(){
        return makeTime;
    }

    public int getMakeFrequency(){
        return makeFrequency;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTemperature(int temperature){
        this.temperature = temperature;
    }

    public void setWashTime(int washTime){
        this.washTime = washTime;
    }

    public void setMakeTime(int makeTime){
        this.makeTime = makeTime;
    }

    public void setMakeFrequency(int makeFrequency){
        this.makeFrequency = makeFrequency;
    }
}
