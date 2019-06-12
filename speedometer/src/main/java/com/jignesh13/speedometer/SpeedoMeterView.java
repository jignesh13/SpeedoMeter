package com.jignesh13.speedometer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

public class SpeedoMeterView extends View {
    private Bitmap backgroundbitmap,backimagebitmap;
    private float centerx,centery,radius;
    private Path path;
    private boolean isSpeedIncrease = false;
    private float speed=0;
    private float currentSpeed=0;
    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private Animator.AnimatorListener animatorListener;
    private float trembleDegree = 4f;
    private boolean canceled = false;
    private Drawable drawable;
    private boolean isborder;
    private int linecolor,needlecolor;
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);

    public boolean isborder() {
        return isborder;
    }

    public SpeedoMeterView(Context context) {
        this(context,null);
    }

    public SpeedoMeterView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeedoMeterView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs == null){
            return;
        }

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyCustomView);
        needlecolor=ta.getColor(R.styleable.MyCustomView_needlecolor,Color.WHITE);
        linecolor=ta.getColor(R.styleable.MyCustomView_linecolor,Color.WHITE);
        drawable = ta.getDrawable(R.styleable.MyCustomView_backimage);
        isborder=ta.getBoolean(R.styleable.MyCustomView_removeborder,true);

        init();

    }
    public Bitmap transform(Bitmap source) {
        source=Bitmap.createScaledBitmap(source,getWidth(),getHeight(),false);
        int size = Math.min(source.getWidth(),source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);


        canvas.drawCircle(centerx, centery, radius-((radius/9.85f)*2.0f), paint);

        squaredBitmap.recycle();
        return bitmap;
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
    public void setbackImageResource(@DrawableRes int resId){
        drawable = ContextCompat.getDrawable(getContext(),resId);
        createback();
        invalidate();

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getWidth()>0&&getHeight()>0){
           createback();
        }
    }
    public void createback(){
        centerx=getWidth()/2.0f;
        centery=getHeight()/2.0f;

        radius=Math.min(getWidth()/2.0f,getHeight()/2.0f);
        if (drawable!=null){
            backimagebitmap=transform(drawableToBitmap(drawable));
        }
        createSpeedometerDisk();
        path=new Path();
        float  bottomY = getWidth()/1.6f ;
        path.moveTo(getWidth()/2.0f,radius/4.0f);
        path.lineTo(centerx-(radius/78.8f),radius/4.0f);
        path.lineTo(centerx-(radius/39.4f),bottomY);
        path.lineTo(centerx+(radius/39.4f),bottomY);
        path.lineTo(centerx+(radius/78.8f),radius/4.0f);
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        if (backimagebitmap!=null)canvas.drawBitmap(backimagebitmap,0,0,paint);
        if (backgroundbitmap!=null){
            paint.setColor(Color.BLACK);
            canvas.drawBitmap(backgroundbitmap,0,0,paint);
        }


        float angle=215.0f+(252*currentSpeed)/140.0f;
        canvas.save();
        canvas.rotate(angle,centerx,centery);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(needlecolor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,paint);
        canvas.drawCircle(centerx,centery,radius/19.7f,paint);
        paint.setColor(getComplimentColor(needlecolor));
        canvas.drawCircle(centerx,centery,radius/197.0f,paint);
        canvas.restore();

    }

    public void setisborder(boolean isborder) {
        this.isborder = isborder;
        createSpeedometerDisk();
        invalidate();
    }



    public static int getComplimentColor(int color) {
        // get existing colors
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        // find compliments
        red = (~red) & 0xff;
        blue = (~blue) & 0xff;
        green = (~green) & 0xff;

        return Color.argb(alpha, red, green, blue);
    }

    private void createSpeedometerDisk() {
        Log.e("radius",radius+"");
        float strokewidth=radius/9.85f;
        backgroundbitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(backgroundbitmap);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokewidth);
        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), Color.rgb(181,181,181), Color.rgb(0,0,0), Shader.TileMode.CLAMP);
        paint.setShader(shader);
       if (isborder)canvas.drawCircle(centerx,centery,radius-(strokewidth/2.0f),paint);
        Shader shader1 = new LinearGradient(0, 0, getWidth(), getHeight(), Color.rgb(0,0,0), Color.rgb(181,181,181), Shader.TileMode.CLAMP);
        paint.setShader(shader1);
       if (isborder)canvas.drawCircle(centerx,centerx,radius-(strokewidth+(strokewidth/2.0f)),paint);
        paint.setShader(null);
        paint.setColor(Color.rgb(35,35 ,35));
        paint.setStyle(Paint.Style.FILL);
        if (backimagebitmap==null)canvas.drawCircle(centerx,centerx,radius-(strokewidth*2.0f),paint);
        paint.setStyle(Paint.Style.STROKE);

//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(6);
//        canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,252,false,paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawArc(new RectF(80,80,getWidth()-80,getHeight()-80),125,180,false,paint);


        paint.setStrokeWidth(radius/26.26f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawArc(new RectF((strokewidth*2.0f)+paint.getStrokeWidth()/2.0f,(strokewidth*2.0f)+paint.getStrokeWidth()/2.0f,getWidth()-(strokewidth*2.0f)-paint.getStrokeWidth()/2.0f,getHeight()-(strokewidth*2.0f)-paint.getStrokeWidth()/2.0f),125,252,false,paint);
        paint.setColor(Color.GREEN);
        canvas.drawArc(new RectF((strokewidth*2.0f)+paint.getStrokeWidth()/2.0f,(strokewidth*2.0f)+paint.getStrokeWidth()/2.0f,getWidth()-(strokewidth*2.0f)-paint.getStrokeWidth()/2.0f,getHeight()-(strokewidth*2.0f)-paint.getStrokeWidth()/2.0f),125,180,false,paint);

        Paint linepaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linepaint.setColor(linecolor);
        int point=-10;
        int lastodd=0;
        for (int i=90+125;i<=90+125+252;i+=9f){

            canvas.save();
            canvas.rotate(i,centerx,centery);
            canvas.translate(0,strokewidth*2.0f);

            if (lastodd==0){
                linepaint.setStrokeWidth(radius/65.66f);
                canvas.drawLine(getWidth()/2.0f,0,getWidth()/2.0f,radius/12.90f,linepaint);
            }
            else {
                linepaint.setStrokeWidth(radius/98.5f);
                canvas.drawLine(getWidth()/2.0f,0,getWidth()/2.0f,radius/24.26f,linepaint);
            }
            if (lastodd==0){
                canvas.translate(0,radius/13.13f);
                point+=10;
                TextPaint textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setTextSize(radius/13.13f);
                textPaint.setColor(linecolor);
                StaticLayout staticLayout=  new StaticLayout(point+"", textPaint, getWidth()
                        , Layout.Alignment.ALIGN_CENTER, 0.0f, 0.0f, false);
                staticLayout.draw(canvas);
            }
            lastodd=lastodd==0?1:0;
            canvas.restore();
        }
    }

    public void setNeedlecolor(@ColorInt int needlecolor) {
        this.needlecolor = needlecolor;
        invalidate();
    }

    public void setLinecolor(@ColorInt int linecolor) {
        this.linecolor = linecolor;
        createSpeedometerDisk();
        invalidate();
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
