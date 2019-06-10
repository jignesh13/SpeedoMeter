package com.jignesh13.speedometer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

public class SpeedoMeterView extends View {
    private Bitmap backgroundbitmap;
    private float centerx,centery,radius;
    private Path path;
    private boolean isSpeedIncrease = false;
    private float speed=0;
    private float currentSpeed=0;
    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private Animator.AnimatorListener animatorListener;
    private float trembleDegree = 4f;
    private boolean canceled = false;
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    public SpeedoMeterView(Context context) {
        this(context,null);
    }

    public SpeedoMeterView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeedoMeterView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
    private void init(){
        speedAnimator = ValueAnimator.ofFloat(0f, 1f);
        trembleAnimator = ValueAnimator.ofFloat(0f, 1f);
        realSpeedAnimator = ValueAnimator.ofFloat(0f, 1f);
        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelTremble() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        canceled = true;
        trembleAnimator.cancel();
        canceled = false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void tremble() {
        cancelTremble();
        Random random = new Random();
        float mad = trembleDegree * random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        mad = (speed + mad > 140) ? 140 - speed
                : (speed + mad < 0) ? 0 - speed : mad;
        trembleAnimator = ValueAnimator.ofFloat(currentSpeed, speed + mad);
        trembleAnimator.setInterpolator(new DecelerateInterpolator());
        trembleAnimator.setDuration(1000);
        trembleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isSpeedIncrease = (float) trembleAnimator.getAnimatedValue() > currentSpeed;
                currentSpeed = (float) trembleAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        trembleAnimator.addListener(animatorListener);
        trembleAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getWidth()>0&&getHeight()>0){
            centerx=getWidth()/2.0f;
            centery=getHeight()/2.0f;
            radius=Math.min(getWidth()/2.0f,getHeight()/2.0f);
            createSpeedometerDisk();
            path=new Path();
            float  bottomY = getWidth()/1.6f ;
            path.moveTo(getWidth()/2.0f,80);
            path.lineTo(centerx-5,80);
            path.lineTo(centerx-10,bottomY);
            path.lineTo(centerx+10,bottomY);
            path.lineTo(centerx+5,80);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundbitmap!=null){
            paint.setColor(Color.BLACK);
            canvas.drawBitmap(backgroundbitmap,0,0,paint);
        }

        float angle=215.0f+(252*currentSpeed)/140.0f;

        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        Log.e("Angle",angle+"");
        if (currentSpeed>100){
            paint.setColor(Color.RED);
            canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,angle-215,false,paint);
            paint.setColor(Color.GREEN);
            canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,180,false,paint);
        }
        else {
            paint.setColor(Color.GREEN);
            canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,angle-215,false,paint);
        }
        canvas.save();
        canvas.rotate(angle,centerx,centery);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(208,50,0));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,paint);
        canvas.drawCircle(centerx,centery,20,paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerx,centery,2,paint);
        canvas.restore();


    }

    private void createSpeedometerDisk() {
        backgroundbitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(backgroundbitmap);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(40);
        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), Color.rgb(181,181,181), Color.rgb(0,0,0), Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(centerx,centery,radius-20,paint);
        Shader shader1 = new LinearGradient(0, 0, getWidth(), getHeight(), Color.rgb(0,0,0), Color.rgb(181,181,181), Shader.TileMode.CLAMP);
        paint.setShader(shader1);
        canvas.drawCircle(centerx,centerx,radius-60,paint);
        paint.setShader(null);
        paint.setColor(Color.rgb(35,35 ,35));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerx,centerx,radius-80,paint);
        paint.setStyle(Paint.Style.STROKE);

//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(6);
//        canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,252,false,paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,180,false,paint);

        Paint linepaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linepaint.setColor(Color.WHITE);
        int point=-10;
        int lastodd=0;
        for (int i=90+125;i<=90+125+252;i+=9f){

            canvas.save();
            canvas.rotate(i,centerx,centery);
            canvas.translate(0,80);

            if (lastodd==0){
                linepaint.setStrokeWidth(6);
                canvas.drawLine(getWidth()/2.0f,0,getWidth()/2.0f,22,linepaint);
            }
            else {
                linepaint.setStrokeWidth(4);
                canvas.drawLine(getWidth()/2.0f,0,getWidth()/2.0f,15,linepaint);
            }
            if (lastodd==0){
                canvas.translate(0,30);
                point+=10;
                TextPaint textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setTextSize(30);
                textPaint.setColor(Color.WHITE);
                StaticLayout staticLayout=  new StaticLayout(point+"", textPaint, getWidth()
                        , Layout.Alignment.ALIGN_CENTER, 0.0f, 0.0f, false);
                staticLayout.draw(canvas);
            }
            lastodd=lastodd==0?1:0;
            canvas.restore();
        }

    }

    public void setSpeed(int speed, boolean wantmaintainspeed){
        speed = (speed > 140) ? 140 : (speed < 0) ? 0 : speed;
        if (speed == this.speed)
            return;
        this.speed = speed;



        isSpeedIncrease = speed > currentSpeed;

        cancelSpeedAnimator();
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, speed);
        speedAnimator.setInterpolator(new DecelerateInterpolator());
        speedAnimator.setDuration(2000);
        speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentSpeed = (float) speedAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        speedAnimator.addListener(animatorListener);
        speedAnimator.start();
    }
    protected void cancelSpeedAnimator() {
        cancelSpeedMove();
        cancelTremble();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelSpeedMove() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        canceled = true;
        speedAnimator.cancel();
        realSpeedAnimator.cancel();
        canceled = false;
    }

}
