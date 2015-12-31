package com.longluo.demo.adherent;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

public class RoundProgress extends View{
	//��̬Բ������
	private int mWallCircleCount = 8;
	private Circle[] mWallCircles;
	/**
     * ����
     */
    private Paint mPaint = new Paint();
    /**
     * ����Բ
     */
    private Circle mCenterCircle = new Circle();
    /**
     * ��̬Բ
     */
    private Circle mActiveCircle = new Circle();
    private float mWallCircleRadius = 50;
    private float mActiveCircleRadius = mWallCircleRadius*0.75f;
    /**
     * ����Բ�뾶
     */
	private float mCenterCircleRadius = 200; 
    /**
     * ��̬Բ�仯�뾶��������
     */
    private float mMaxStaticCircleRadiusScaleRate = 0.4f;
    /**
     * ���
     */
    private int mWidth;
    /**
     * �߶�
     */
    private int mHeight;
	private float mCurrentStaticCircleRadius;
	private float mMaxAdherentLength = 2.5f * mWallCircleRadius;
    
	public RoundProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public RoundProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public RoundProgress(Context context) {
		super(context);
		init();
	}	
	
	private void init(){
		mPaint.setColor(Color.RED);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mWidth = mHeight = (int)(2*(mCenterCircleRadius+2*mWallCircleRadius*(1+mMaxStaticCircleRadiusScaleRate))); 
		mCenterCircle = new Circle();
		mCenterCircle.x = mWidth/2;
		mCenterCircle.y = mHeight/2;
		
		mActiveCircle = new Circle();
		mActiveCircle.angle = -90;
		mActiveCircle.radius = mActiveCircleRadius;
		mActiveCircle.x = mWidth/2;
		mActiveCircle.y = mWallCircleRadius*(1+mMaxStaticCircleRadiusScaleRate);
		
		mWallCircles = new Circle[mWallCircleCount];
		for(int i=0;i<mWallCircleCount;i++){
			Circle circle = new Circle();
			circle.angle = 360/mWallCircleCount*i;
			circle.x = (float)(mCenterCircle.x + (mCenterCircleRadius+mWallCircleRadius) * Math.cos(Math.toRadians(circle.angle)));
			circle.y = (float)(mCenterCircle.y + (mCenterCircleRadius+mWallCircleRadius) * Math.sin(Math.toRadians(circle.angle)));
			circle.radius = mWallCircleRadius;
			mWallCircles[i] = circle;
		}
		
		/* ��ʼ���� */
        startAnim();
	}
	
	/**
     * �ж�ճ����Χ����̬�ı侲̬Բ��С
     * 
     * @param position
     * @return
     */
    private boolean doAdhere(Circle circle) {        
        /* �뾶�仯 */
        float distance = (float) Math.sqrt(Math.pow(mActiveCircle.x - circle.x, 2) + Math.pow(mActiveCircle.y - circle.y, 2));//Math.abs(mDynamicCircle.x - circle.x);
        float scale = mMaxStaticCircleRadiusScaleRate -  mMaxStaticCircleRadiusScaleRate * (distance / mMaxAdherentLength);
        mCurrentStaticCircleRadius = circle.radius * (1 + scale);        
        /* �ж��Ƿ���������������� */
        if (distance < mMaxAdherentLength ) 
            return true;
        else
            return false;
    }
	
	private void startAnim() {
		ValueAnimator valueAnimator = ValueAnimator.ofFloat(mActiveCircle.angle, mActiveCircle.angle+360);
		valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(2500);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (Float) animation.getAnimatedValue();
                mActiveCircle.x = (float)(mCenterCircle.x + (mCenterCircleRadius+mWallCircleRadius) * Math.cos(Math.toRadians(angle)));
                mActiveCircle.y = (float)(mCenterCircle.y + (mCenterCircleRadius+mWallCircleRadius) * Math.sin(Math.toRadians(angle)));
                invalidate();
            }
        });
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
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawCircle(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSizeAndState(mWidth, widthMeasureSpec, MeasureSpec.UNSPECIFIED), resolveSizeAndState(mHeight, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
	}
	
	private void drawCircle(Canvas canvas){
        canvas.drawCircle(mActiveCircle.x, mActiveCircle.y, mActiveCircle.radius, mPaint);
        		
		for(int i=0;i<mWallCircleCount;i++){
			Circle circle = mWallCircles[i];	
			if(doAdhere(circle)){
				canvas.drawCircle(circle.x, circle.y, mCurrentStaticCircleRadius, mPaint);					
				Path path = drawAdherentBody(mActiveCircle.x, mActiveCircle.y, mActiveCircle.radius,45f,circle.x, circle.y, circle.radius,45f);
				canvas.drawPath(path, mPaint);
			}
			canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint);							
		}			
	}				
	
	private class Circle{
		public float x;
		public float y;
		public float radius;
		public float angle;
	}
}
