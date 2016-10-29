package org.liyunkun.customclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

/**
 * Created by liyunkun on 2016/10/26 0026.
 */
public class CustomClock extends View {

    /**
     * 模式一：ONLY_HOUR_NOT_NUMBER  没有数字，而且只有大的划分
     * 模式二：ONLY_HOUR_HAVE_NUMBER   有数字，而且只有大的划分
     * 模式三：HOUR_AND_MINUTE_NOT_NUMBER  没有数字，有大的划分和小的划分
     * 模式四：HOUR_AND_MINUTE_HAVE_NUMBER  有数字，有大的划分和小的划分
     */
    public static final int ONLY_HOUR_NOT_NUMBER = 0;
    public static final int ONLY_HOUR_HAVE_NUMBER = 1;
    public static final int HOUR_AND_MINUTE_NOT_NUMBER = 2;
    public static final int HOUR_AND_MINUTE_HAVE_NUMBER = 3;

    private int outsideCircleColor = Color.BLACK;
    private int hourLineColor = Color.RED;
    private int minuteLineColor = Color.GREEN;
    private int secondLineColor = Color.BLUE;
    private int insideSolidCircleColor = Color.BLUE;
    private int insideSolidCircleRadius = 20;
    private int numberColor = Color.BLACK;
    private int numberTextSize = 30;
    private int numberPaintLength = 80;
    private int clockMode = 0;
    private int outsideCirclePaintSize=10;
    private int outsideCirclePaintLength=30;
    private int hourLinePaintSize=8;
    private int minuteLinePaintSize=6;
    private int secondLinePaintSize=4;

    private Paint outsideCirclePaint;
    private Paint hourLinePaint;
    private Paint minuteLinePaint;
    private Paint secondLinePaint;
    private Paint insideSolidCirclePaint;
    private Paint numberPaint;
    private Paint minPaint;

    private final int DEFAULT=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300,getResources().getDisplayMetrics());
    private Calendar calendar;

    public CustomClock(Context context) {
        this(context, null);
    }

    public CustomClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取用户在xml中设置的属性的值
        initFiled(context, attrs);
        //初始化画笔
        initPaint();
    }

    //对View的剪裁
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 测量模式分为3种：
         * 1.EXACTLY 精确测量模式，如果设置控件的宽高为match_parent或者exactly，则测量模式即此
         * 2.AT_MOST 最大测量模式 ，如果控件的宽高设置为wrap_content，则测量模式即此
         * 3.UNSPECIFIED 表示子控件可以无限大
         */
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
            widthSize = DEFAULT;
            break;
            case MeasureSpec.EXACTLY:
                break;
        }
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = DEFAULT;
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        widthSize = heightSize = Math.min(widthSize, heightSize);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int outsideCircleRadius = height / 2;
        //绘画最外面的大圆
        canvas.drawCircle(height / 2, width / 2, outsideCircleRadius - outsideCirclePaintSize/2, outsideCirclePaint);
        //绘画里面的实心圆
        canvas.drawCircle(height / 2, width / 2, insideSolidCircleRadius - 1, insideSolidCirclePaint);
        //绘画12个小时的划分
        for (int i = 1; i < 13; i++) {
            canvas.save();
            canvas.rotate(i * 30, width / 2, height / 2);
            canvas.drawLine(width / 2, 0, width / 2,30, outsideCirclePaint);
            canvas.restore();
        }
        //获取当前的时间和小时，分钟，秒
        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        //绘画时针
        canvas.save();
        canvas.rotate(hour * 30 + minute / 60F * 30, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 + 15, width / 2, (width/2)*4/7, hourLinePaint);
        canvas.restore();
        //绘画分针
        canvas.save();
        canvas.rotate(minute * 6, width / 2, width / 2);
        canvas.drawLine(width / 2, height / 2 + 20, width / 2, (width/2)*3/7, minuteLinePaint);
        canvas.restore();
        //绘画秒针
        canvas.save();
        canvas.rotate(second * 6, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 + 25, width / 2, (width/2)*2/7, secondLinePaint);
        canvas.restore();
        //模式
        switch (clockMode) {
            case ONLY_HOUR_HAVE_NUMBER: {
                drawNumber(canvas, height, width);
            }
            break;
            case ONLY_HOUR_NOT_NUMBER:
                break;
            case HOUR_AND_MINUTE_HAVE_NUMBER:
                drawNumber(canvas, height, width);
                drawMinPaint(canvas, height, width);
                break;
            case HOUR_AND_MINUTE_NOT_NUMBER: {
                drawMinPaint(canvas, height, width);
            }
            break;
        }
        //每隔一秒重绘一次
        postInvalidateDelayed(1000);
    }

    private void drawNumber(Canvas canvas, int height, int width) {
        for (int i = 1; i < 13; i++) {
            canvas.save();
            canvas.rotate(i*30,width/2,height/2);
            canvas.drawText(i+"",width/2,numberPaintLength,numberPaint);
            canvas.restore();
//            canvas.drawText(i+"",(float)(width/2+width/2*Math.sin(i*30)),(float)(height/2-height/2*Math.cos(i*30)),numberPaint);
        }
    }

    private void drawMinPaint(Canvas canvas, int height, int width) {
        for (int i = 1; i < 61; i++) {
            canvas.save();
            canvas.rotate(i * 6, width / 2, height / 2);
            canvas.drawLine(width / 2, 0, width / 2, 20, minPaint);
            canvas.restore();
        }
    }

    private void initPaint() {
        outsideCirclePaint = new Paint();
        outsideCirclePaint.setAntiAlias(true);
        outsideCirclePaint.setColor(outsideCircleColor);
        outsideCirclePaint.setStyle(Paint.Style.STROKE);
        outsideCirclePaint.setStrokeWidth(outsideCirclePaintSize);

        hourLinePaint = new Paint();
        hourLinePaint.setAntiAlias(true);
        hourLinePaint.setColor(hourLineColor);
        hourLinePaint.setStrokeWidth(hourLinePaintSize);

        minuteLinePaint = new Paint();
        minuteLinePaint.setAntiAlias(true);
        minuteLinePaint.setColor(minuteLineColor);
        minuteLinePaint.setStrokeWidth(minuteLinePaintSize);

        secondLinePaint = new Paint();
        secondLinePaint.setAntiAlias(true);
        secondLinePaint.setColor(secondLineColor);
        secondLinePaint.setStrokeWidth(secondLinePaintSize);

        insideSolidCirclePaint = new Paint();
        insideSolidCirclePaint.setAntiAlias(true);
        insideSolidCirclePaint.setColor(insideSolidCircleColor);
        insideSolidCirclePaint.setStrokeWidth(2);
        insideSolidCirclePaint.setStyle(Paint.Style.FILL);

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(numberColor);
        numberPaint.setTextSize(numberTextSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);

        minPaint = new Paint();
        minPaint.setAntiAlias(true);
        minPaint.setColor(outsideCircleColor);
        minPaint.setStrokeWidth(outsideCirclePaintSize/2);
        minPaint.setStyle(Paint.Style.STROKE);
    }

    private void initFiled(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomClock);
        outsideCircleColor = ta.getColor(R.styleable.CustomClock_outsideCircleColor, outsideCircleColor);
        hourLineColor = ta.getColor(R.styleable.CustomClock_hourLineColor, hourLineColor);
        minuteLineColor = ta.getColor(R.styleable.CustomClock_minuteLineColor, minuteLineColor);
        secondLineColor = ta.getColor(R.styleable.CustomClock_secondLineColor, secondLineColor);
        insideSolidCircleColor = ta.getColor(R.styleable.CustomClock_insideSolidCircleColor, insideSolidCircleColor);
        insideSolidCircleRadius = ta.getDimensionPixelSize(R.styleable.CustomClock_insideSolidCircleRadius, insideSolidCircleRadius);
        numberColor = ta.getColor(R.styleable.CustomClock_numberColor, numberColor);
        numberTextSize = ta.getDimensionPixelSize(R.styleable.CustomClock_numberTextSize, numberTextSize);
        numberPaintLength = ta.getDimensionPixelSize(R.styleable.CustomClock_numberPaintLength, numberPaintLength);
        clockMode = ta.getInt(R.styleable.CustomClock_clockMode, clockMode);
        outsideCirclePaintSize=ta.getDimensionPixelSize(R.styleable.CustomClock_outsideCirclePaintSize,outsideCirclePaintSize);
        outsideCirclePaintLength=ta.getDimensionPixelSize(R.styleable.CustomClock_outsideCirclePaintLength,outsideCirclePaintLength);
        hourLinePaintSize=ta.getDimensionPixelSize(R.styleable.CustomClock_hourLinePaintSize,hourLinePaintSize);
        minuteLinePaintSize=ta.getDimensionPixelSize(R.styleable.CustomClock_minuteLinePaintSize,minuteLinePaintSize);
        secondLinePaintSize=ta.getDimensionPixelSize(R.styleable.CustomClock_secondLinePaintSize,secondLinePaintSize);
        ta.recycle();
    }
}
