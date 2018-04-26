package com.xzw.intellijteamaker;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private ArrayList<Tea> teaArrayList;
    private Tea tea;
    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinner;
    private Button btn_onekey;
    private TextView textView_content;
    private TextView textView_title;
    private Chronometer chronometer;
    private ArrayList<String> arrayList;
    private String[] teaTypeString = {"绿茶","红茶","铁观音","黄茶","白茶","黑茶"};



    public CardPagerAdapter(Context context) {
        tea = new Tea();
        teaArrayList = new ArrayList<>();
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        arrayList = new ArrayList<String>();
        arrayList.addAll(Arrays.asList(teaTypeString));
        arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        CardView cardView;
        if(position == 0){
            view= LayoutInflater.from(container.getContext())
                    .inflate(R.layout.adapter, container, false);
            container.addView(view);
            bind(mData.get(position), view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            spinner = (Spinner) view.findViewById(R.id.spinner_tea);
            btn_onekey = (Button) view.findViewById(R.id.start_onekey_btn);
            chronometer= (Chronometer) view.findViewById(R.id.chronometer);
            textView_content = (TextView) view.findViewById(R.id.contentTextView);
            textView_title = (TextView) view.findViewById(R.id.titleTextView);
        }else {
            view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.adapter_two, container, false);
            container.addView(view);
            bind(mData.get(position), view);
            cardView = (CardView) view.findViewById(R.id.cardView_two);
        }



        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        addTeaArrayListData(0,"greenTea",80,15,180,3);
        addTeaArrayListData(1,"redTea",95,20,180,3);
        addTeaArrayListData(2,"tieGuanYin",100,10,60,4);
        addTeaArrayListData(3,"yellowTea",85,20,120,5);
        addTeaArrayListData(4,"whiteTea",90,30,120,9);
        addTeaArrayListData(5,"blackTea",100,15,120,20);





        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelected(true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择后做的事
                tea = teaArrayList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }


    private void addTeaArrayListData(int position,String name,int temperature,int washTime,int makeTime,int makeFrequency){
        teaArrayList.add(position,new Tea());
        teaArrayList.get(position).setName(name);
        teaArrayList.get(position).setTemperature(temperature);
        teaArrayList.get(position).setWashTime(washTime);
        teaArrayList.get(position).setMakeTime(makeTime);
        teaArrayList.get(position).setMakeFrequency(makeFrequency);
    }

    public Tea getTea(){
        return tea;
    }

    public void changeViewInvisible(int time){
        spinner.setVisibility(View.INVISIBLE);
        btn_onekey.setVisibility(View.GONE);
        textView_content.setText("预计等待时间："+time);
        textView_title.setText("正在泡茶，请等待");

        chronometer.setVisibility(View.VISIBLE);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
        chronometer.start();
    }

    public void changeViewVisible(){
        spinner.setVisibility(View.VISIBLE);
        btn_onekey.setVisibility(View.VISIBLE);
        textView_content.setText(R.string.text_1);
        textView_title.setText(R.string.title_1);
        chronometer.setVisibility(View.GONE);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零

    }



}
