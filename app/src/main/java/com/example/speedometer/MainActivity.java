package com.example.speedometer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.jignesh13.speedometer.SpeedoMeterView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int[] color={R.color.colorAccent,R.color.black,R.color.white,R.color.yellow,R.color.blue,R.color.fuchsia};
    private SpeedoMeterView speedoMeterView;
    private SeekBar seekBar;
    private Random random=new Random();
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

    public void removeborder(View view) {
        speedoMeterView.setisborder(!speedoMeterView.isborder());
    }

    public void linecolorchange(View view) {
        int color= Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        speedoMeterView.setLinecolor(color);
    }

    public void needlecolorchange(View view) {
        int color= Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        speedoMeterView.setNeedlecolor(color);
    }

    public void backColorChange(View view) {

        speedoMeterView.setbackImageResource(color[random.nextInt(color.length)]);
    }
}
