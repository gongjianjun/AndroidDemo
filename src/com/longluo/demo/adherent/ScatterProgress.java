package com.longluo.demo.adherent;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ScatterProgress extends View{
	/**
     * ���
     */
    private int mWidth;

    /**
     * �߶�
     */
    private int mHeight;
    /**
     * ����
     */
    private Paint mPaint = new Paint();
    /**
     * �м�Բ�ͳ����Բ�仯�뾶��������
     */
    private float mMaxCircleRadiusScaleRate = 0.4f;    
    /**
     * СԲ�뾶
     */
    private float mWallCircleRadius = 30;
    /**
     * ��Բ�뾶
     */
    private float mCenterCircleRadius = 150;    
    /**
     * �м��Բ
     */
    private Circle mCenterCircle;

	private int mWallCircleCount = 8;

	private Circle[] mWallCircles;

	private float mMaxAdherentLength;
	/**
	 * �м��򶯻�
	 */
	private ValueAnimator mCenterCircleAnimator;
    
	public ScatterProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public ScatterProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ScatterProgress(Context context) {
		super(context);
		init();
	}	
	
	private void init(){
		mPaint.setColor(Color.RED);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mWidth = mHeight = (int)(2*(mWallCircleRadius+mCenterCircleRadius*(1+mMaxCircleRadiusScaleRate)*(1+mMaxCircleRadiusScaleRate))); 
		mMaxAdherentLength = mWallCircleRadius+mCenterCircleRadius*(1+mMaxCircleRadiusScaleRate)*(1+mMaxCircleRadiusScaleRate/2);
			
		mCenterCircle = new Circle();		
		mCenterCircle.radius = mCenterCircleRadius*(1+mMaxCircleRadiusScaleRate);
		mCenterCircle.x = mWidth/2;
		mCenterCircle.y = mHeight/2;
		
		mWallCircles = new Circle[mWallCircleCount];
		for(int i=0;i<mWallCircleCount;i++){
			Circle circle = new Circle();
			circle.angle = 360/mWallCircleCount*i;
			circle.x = mWidth/2;
			circle.y = mHeight/2;
			circle.radius = mWallCircleRadius;
			mWallCircles[i] = circle;
		}
		
		/* ��ʼ���� */
        startAnim();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSizeAndState(mWidth, widthMeasureSpec, MeasureSpec.UNSPECIFIED), resolveSizeAndState(mHeight, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//�м�Բ
		canvas.drawCircle(mCenterCircle.x, mCenterCircle.y, mCenterCircle.radius, mPaint);
		//СԲ
		for (int i = 0; i < mWallCircleCount; i++) {            
            Circle circle = mWallCircles[i];    
            if(doAdhere(circle)){
            	Path path = drawAdherentBody(mCenterCircle.x, mCenterCircle.y, mCenterCircle.radius,20f,circle.x, circle.y, circle.radius,45f);
				canvas.drawPath(path, mPaint);
            }
            canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint);
        }
	}
	
	private void startAnim() {		
		startCenterCirclesAnim();
		startWallCirclesAnim();
	}
		
	ArrayList<Animator> animators1 = new ArrayList<Animator>();
	ArrayList<Animator> animators2 = new ArrayList<Animator>();
	public void startWallCirclesAnim(){
		AnimatorSet set = new AnimatorSet();
		AnimatorSet set1 = new AnimatorSet();
		AnimatorSet set2 = new AnimatorSet();
		animators1.clear();
		animators2.clear();
		/* СԲ���궯�� */		
        for (int i = 0; i < mWallCircleCount; i++) {         
        	//�뾶
            final Circle wallCircle = mWallCircles[i];
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mWallCircleRadius,mWidth/2-mWallCircleRadius);
    		valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    		valueAnimator.setDuration(400);		
    		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
    			
    			@Override
    			public void onAnimationUpdate(ValueAnimator animation) {
    				float radius = (Float) animation.getAnimatedValue();
    				wallCircle.bigRadius = radius;
    			}
    		});    	
    		animators1.add(valueAnimator);
    		//λ��
    		ValueAnimator XYAnimator = ValueAnimator.ofFloat(wallCircle.angle, wallCircle.angle + 360);
    		XYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    		XYAnimator.setDuration(3600);            
    		XYAnimator.start();
    		XYAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float angle = (Float) animation.getAnimatedValue();
                    wallCircle.x = (float) (mCenterCircle.x + wallCircle.bigRadius * Math.cos(Math.toRadians(angle)));
                    wallCircle.y = (float) (mCenterCircle.y + wallCircle.bigRadius * Math.sin(Math.toRadians(angle)));
                    invalidate();
                }
            });    		
    		animators2.add(XYAnimator);
        }
        set1.playSequentially(animators1);        
        set2.playTogether(animators2);
        
        set.playTogether(set1,set2);
        set.addListener(new AnimatorListenerAdapter() {
						
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);    
				startWallCirclesAnimRecovery();
				startCenterCirclesAnimRecovery();
			}					
		});	
        set.start();
	}
	
	public void startWallCirclesAnimRecovery(){
		AnimatorSet set = new AnimatorSet();
		AnimatorSet set1 = new AnimatorSet();
		AnimatorSet set2 = new AnimatorSet();
		animators1.clear();
		animators2.clear();
		/* СԲ���궯�� */		
        for (int i = 0; i < mWallCircleCount; i++) {         
        	//�뾶
            final Circle wallCircle = mWallCircles[i];
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mWidth/2-mWallCircleRadius,mWallCircleRadius);
    		valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    		valueAnimator.setDuration(400);		
    		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
    			
    			@Override
    			public void onAnimationUpdate(ValueAnimator animation) {
    				float radius = (Float) animation.getAnimatedValue();
    				wallCircle.bigRadius = radius;
    			}
    		});    	
    		animators1.add(valueAnimator);   
    		//λ��
    		ValueAnimator XYAnimator = ValueAnimator.ofFloat(wallCircle.angle + 360,wallCircle.angle);
    		XYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    		XYAnimator.setDuration(3600);            
    		XYAnimator.start();
    		XYAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float angle = (Float) animation.getAnimatedValue();
                    wallCircle.x = (float) (mCenterCircle.x + wallCircle.bigRadius * Math.cos(Math.toRadians(angle)));
                    wallCircle.y = (float) (mCenterCircle.y + wallCircle.bigRadius * Math.sin(Math.toRadians(angle)));
                    invalidate();
                }
            });    		
    		animators2.add(XYAnimator);
        }
        set1.playSequentially(animators1);       
        set2.playTogether(animators2);
        
        set.playTogether(set1,set2);
        set.addListener(new AnimatorListenerAdapter() {
			
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);    
				startWallCirclesAnim();
				startCenterCirclesAnim();
			}					
		});
        set.start();
	}
		
	public void startCenterCirclesAnimRecovery(){
		mCenterCircleAnimator = ValueAnimator.ofFloat(mCenterCircleRadius,mCenterCircleRadius*(1+mMaxCircleRadiusScaleRate));
		mCenterCircleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCenterCircleAnimator.setDuration(3600);		
		mCenterCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float radius = (Float) animation.getAnimatedValue();
				mCenterCircle.radius = radius;
				invalidate();
			}
		});
		mCenterCircleAnimator.start();
	}
	
	public void startCenterCirclesAnim(){		
		mCenterCircleAnimator = ValueAnimator.ofFloat(mCenterCircleRadius*(1+mMaxCircleRadiusScaleRate),mCenterCircleRadius);
		mCenterCircleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCenterCircleAnimator.setDuration(3600);		
		mCenterCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float radius = (Float) animation.getAnimatedValue();
				mCenterCircle.radius = radius;
				invalidate();
			}
		});
		mCenterCircleAnimator.start();
	}		
	
	
	private class Circle{
		public float x;
		public float y;
		public float radius;
		public float angle;
		public float bigRadius;
	}
	
	/**
     * �ж�ճ����Χ����̬�ı侲̬Բ��С
     * 
     * @param position
     * @return
     */	
    private boolean doAdhere(Circle circle) {        
        float distance = (float) Math.sqrt(Math.pow(mCenterCircle.x - circle.x, 2) + Math.pow(mCenterCircle.y - circle.y, 2));//Math.abs(mDynamicCircle.x - circle.x);    
        if (distance < mMaxAdherentLength) 
            return true;
        else
            return false;
    }	
	/**
     * ��ճ���壨ͨ�÷�����     
     * @param cx1     Բ��x1
     * @param cy1     Բ��y1
     * @param r1      Բ�뾶r1
     * @param offset1 ����������ƫ�ƽǶ�offset1
     * @param cx2     Բ��x2
     * @param cy2     Բ��y2
     * @param r2      Բ�뾶r2
     * @param offset2 ����������ƫ�ƽǶ�offset2
     * @return
     */
    private Path drawAdherentBody(float cx1,float cy1,float r1,float offset1,float cx2,float cy2,float r2,float offset2) {
        
        /* �����Ǻ��� */
        float degrees =(float) Math.toDegrees(Math.atan(Math.abs(cy2 - cy1) / Math.abs(cx2 - cx1)));
        
        /* ����Բ1��Բ2�����λ�����ĸ��� */
        float differenceX = cx1 - cx2;
        float differenceY = cy1 - cy2;

        /* �������������ߵ��ĸ��˵� */
        float x1,y1,x2,y2,x3,y3,x4,y4;
        
        /* Բ1��Բ2���±� */
        if (differenceX == 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));

        }
        /* Բ1��Բ2���ϱ� */
        else if (differenceX == 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));

        }
        /* Բ1��Բ2���ұ� */
        else if (differenceX > 0 && differenceY == 0) {
            x2 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        } 
        /* Բ1��Բ2����� */
        else if (differenceX < 0 && differenceY == 0 ) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        }
        /* Բ1��Բ2�����½� */
        else if (differenceX > 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }
        /* Բ1��Բ2�����Ͻ� */
        else if (differenceX < 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
        /* Բ1��Բ2�����½� */
        else if (differenceX < 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
        /* Բ1��Բ2�����Ͻ� */
        else {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 + r1* (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }
        
        /* ���������ߵĿ��Ƶ� */
        float anchorX1,anchorY1,anchorX2,anchorY2;
        
        /* Բ1����Բ2 */
        if (r1 > r2) {
            anchorX1 = (x2 + x3) / 2;
            anchorY1 = (y2 + y3) / 2;
            anchorX2 = (x1 + x4) / 2;
            anchorY2 = (y1 + y4) / 2;
        }
        /* Բ1С�ڻ����Բ2 */
        else {
            anchorX1 = (x1 + x4) / 2;
            anchorY1 = (y1 + y4) / 2;
            anchorX2 = (x2 + x3) / 2;
            anchorY2 = (y2 + y3) / 2;
        }
        
        /* ��ճ���� */
        Path path = new Path();
        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX1, anchorY1, x2, y2);
        path.lineTo(x4, y4);
        path.quadTo(anchorX2, anchorY2, x3, y3);
        path.lineTo(x1, y1);

        return path;
    }	
    
}
