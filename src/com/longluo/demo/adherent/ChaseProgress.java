package com.longluo.demo.adherent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

public class ChaseProgress extends View{
	//��̬Բ������
	private int mWallCircleCount = 5;
	private Circle[] mWallCircles;
	/**
     * ����
     */
    private Paint mPaint = new Paint();
    
    /**
     * ��̬Բ
     */
    private Circle mCenterCircle = new Circle();
    private float mCenterCircleRadius = 200;
    private float mWallCircleRadius = 50;
    private float mDistance = 2*mWallCircleRadius;

    /**
     * ���
     */
    private int mWidth;
    /**
     * �߶�
     */
    private int mHeight;
    /**
     * ����Ƕ�
     */
    private float mDivideAngle = 25;
    
	public ChaseProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public ChaseProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ChaseProgress(Context context) {
		super(context);
		init();
	}	
	
	private void init(){
		mPaint.setColor(Color.RED);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mWidth = mHeight = (int)(2*(mCenterCircleRadius+mDistance+2*mWallCircleRadius)); 
		
		/* ����Բ */
		mCenterCircle = new Circle();
		mCenterCircle.x = mWidth/2;
		mCenterCircle.y = mHeight/2;
		mCenterCircle.radius = mCenterCircleRadius;
		
		mWallCircles = new Circle[mWallCircleCount];
		for(int i=0;i<mWallCircleCount;i++){
			Circle circle = new Circle();
			circle.angle = -90 - mDivideAngle * i;
			circle.x = (float)(mCenterCircle.x + (mCenterCircleRadius+mDistance+mWallCircleRadius) * Math.cos(Math.toRadians(circle.angle)));
			circle.y = (float)(mCenterCircle.y + (mCenterCircleRadius+mDistance+mWallCircleRadius) * Math.sin(Math.toRadians(circle.angle)));
			circle.radius = mWallCircleRadius;
			mWallCircles[i] = circle;
		}
		
		/* ��ʼ���� */
        startAnim();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawCircle(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSizeAndState(mWidth, widthMeasureSpec, MeasureSpec.UNSPECIFIED), resolveSizeAndState(mHeight, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
	}
	
	private void drawCircle(Canvas canvas){
        canvas.drawCircle(mCenterCircle.x, mCenterCircle.y, mCenterCircle.radius, mPaint);
        		
		for(int i=0;i<mWallCircleCount;i++){
			Circle circle = mWallCircles[i];			
			canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint);							
		}		
	}		
	
	/**
     * ��ʼ����
     */
    private void startAnim() {        
    	/* ׷�϶��� */
        for (int i = 0; i < mWallCircleCount; i++) {            
            final Circle circle = mWallCircles[i];
            
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(circle.angle, circle.angle + 360);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setDuration(1000 + 200 * i);
            valueAnimator.start();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float angle = (Float) animation.getAnimatedValue();
                    Log.i("cky", angle+"");
                    circle.x = (float)(mCenterCircle.x + (mCenterCircleRadius+mDistance+mWallCircleRadius) * Math.cos(Math.toRadians(angle)));
        			circle.y = (float)(mCenterCircle.y + (mCenterCircleRadius+mDistance+mWallCircleRadius) * Math.sin(Math.toRadians(angle)));
                    invalidate();
                }
            });
            
            if (i == mWallCircleCount - 1) {
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startAnim();
                    }
                });
            }
			
        }
    }
	
	private class Circle{
		public float x;
		public float y;
		public float radius;
		public float angle;
	}
}
