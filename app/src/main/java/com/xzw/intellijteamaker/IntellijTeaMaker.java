package com.xzw.intellijteamaker;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 7212519 on 2018/4/5.
 */

public class IntellijTeaMaker extends Application {

    private List<String> device_address = new ArrayList<String>();
    private List<String> device_name = new ArrayList<String>();


    public List<String> getDevice_address(){

        return this.device_address;
    }

    public List<String> getDevice_name(){

        return this.device_name;
    }
}
