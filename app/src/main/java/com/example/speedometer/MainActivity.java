package com.example.speedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SpeedoMeterView speedoMeterView;
    private SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedoMeterView=findViewById(R.id.speedometerview);
        seekBar=findViewById(R.id.seekBar);


    }

    public void okBtnClick(View view) {
        speedoMeterView.setSpeed(seekBar.getProgress(),true);
    }
}
