package com.xzw.intellijteamaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class info extends AppCompatActivity {

    private static int count = 0;
    private TextView textView_mengfang;
    private ImageView imageViewDmf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_info);

        textView_mengfang = (TextView) findViewById(R.id.textView_mengfang);
        imageViewDmf = (ImageView) findViewById(R.id.image_dmf);

        textView_mengfang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count>20){
                    imageViewDmf.setVisibility(View.VISIBLE);
                    count = 0;
                }
            }
        });

    }
}
