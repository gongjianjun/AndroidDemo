package com.longluo.demo.adherent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;

import com.longluo.demo.R;

/**
 * Created by luolong on 2015/12/31.
 */
public class AdherentLayout extends RelativeLayout {
    private Circle mHeaderCircle = new Circle();
    private Circle mFooterCircle = new Circle();

    // ����
    private Paint mPaint = new Paint();
    // �����������ߵ�Path����
    private Path mPath = new Path();
    // ճ������ɫ
    private int mColor = Color.rgb(247, 82, 49);
    // �Ƿ�ճ����
    private boolean isAdherent = true;
    // ��View��ʼ��ȡ��߶�
    private int mOriginalWidth;
    private int mOriginalHeight;
    // �Ƿ��һ��onSizeChanged
    private boolean isFirst = true;
    // �û���ӵ���ͼ�����Բ���ӣ�
    private View mView;
    // �Ƿ����ڽ��ж�����
    private boolean isAnim = false;
    // ��¼���µ�x��y
    float mDownX;
    float mDownY;
    // ��View�����Ͻ�x��y
    private float mX;
    private float mY;
    // ���ؼ������ڱ߾�
    private float mParentPaddingLeft;
    private float mParentPaddingTop;
    // Ĭ��ճ������󳤶�
    private float mMaxAdherentLength = 1000;
    // ͷ��Բ��Сʱ����С�������С�뾶
    private float mMinHeaderCircleRadius = 4;
    // �Ƿ�������Գ���
    private boolean isDismissed = true;
    // �Ƿ���
    boolean isDown = false;

    /**
     * ����ճ���Ƿ�ϵ��ļ�����
     */
    private OnAdherentListener mOnAdherentListener = new OnAdherentListener() {

        @Override
        public void onDismiss() {
            if (mView == null)
                return;
            final Drawable old = mView.getBackground();
            mView.setBackgroundResource(R.drawable.tip_anim);
            // ������
            AnimationDrawable animationDrawable = ((AnimationDrawable) mView
                    .getBackground());
            animationDrawable.stop();
            animationDrawable.start();

            int duration = 0;
            for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                duration += animationDrawable.getDuration(i);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    reset();
//                    mView.setBackground(old);
                    mView.setBackgroundDrawable(old);
                }
            }, duration);
        }
    };

    public AdherentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AdherentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdherentLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        // ͸������
        setBackgroundColor(Color.TRANSPARENT);
        // ���û���
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isFirst && w > 0 && h > 0) {
            mView = getChildAt(0);
            // ��¼��ʼ��ߣ����ڸ�ԭ
            mOriginalWidth = w;
            mOriginalHeight = h;
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
            mX = getX() - lp.leftMargin;
            mY = getY() - lp.topMargin;
            ViewGroup mViewGroup = (ViewGroup) getParent();
            if (mViewGroup != null) {
                mParentPaddingLeft = mViewGroup.getPaddingLeft();
                mParentPaddingTop = mViewGroup.getPaddingTop();
            }
            reset();
            isFirst = false;
        }
    }

    /**
     * �������в���
     */
    public void reset() {
        setWidthAndHeight(mOriginalWidth, mOriginalHeight);
        mHeaderCircle.curRadius = mFooterCircle.curRadius = mHeaderCircle.originalRadius = mFooterCircle.originalRadius = getRadius();
        mFooterCircle.ox = mFooterCircle.curx = mHeaderCircle.ox = mHeaderCircle.curx = mOriginalWidth / 2;
        mFooterCircle.oy = mFooterCircle.cury = mHeaderCircle.oy = mHeaderCircle.cury = mOriginalHeight / 2;
        if (mView != null) {
            if (isFirst) {
                mView.setX(0);
                mView.setY(0);
            } else {
                mView.setX(getPaddingLeft());
                mView.setY(getPaddingTop());
            }
        }
        isAnim = false;
    }

    /**
     * �����ڱ߾෵��Բ�İ뾶
     *
     * @return
     */
    private float getRadius() {
        return (float) (Math.min(
                Math.min(mOriginalWidth / 2 - getPaddingLeft(), mOriginalWidth
                        / 2 - getPaddingRight()),
                Math.min(mOriginalHeight / 2 - getPaddingTop(), mOriginalHeight
                        / 2 - getPaddingBottom())) - 2);
    }

    /**
     * ���ÿ�͸�
     *
     * @param width
     * @param height
     */
    private void setWidthAndHeight(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(mHeaderCircle.curx, mHeaderCircle.cury,
                mHeaderCircle.curRadius, mPaint);
        canvas.drawCircle(mFooterCircle.curx, mFooterCircle.cury,
                mFooterCircle.curRadius, mPaint);
        if (isAdherent)
            drawBezier(canvas);
    }

    /**
     * ������������
     *
     * @param canvas
     */
    private void drawBezier(Canvas canvas) {

		/* �����Ǻ��� */
        float atan = (float) Math
                .atan((mFooterCircle.cury - mHeaderCircle.cury)
                        / (mFooterCircle.curx - mHeaderCircle.curx));
        float sin = (float) Math.sin(atan);
        float cos = (float) Math.cos(atan);

		/* �ĸ��� */
        float headerX1 = mHeaderCircle.curx - mHeaderCircle.curRadius * sin;
        float headerY1 = mHeaderCircle.cury + mHeaderCircle.curRadius * cos;

        float headerX2 = mHeaderCircle.curx + mHeaderCircle.curRadius * sin;
        float headerY2 = mHeaderCircle.cury - mHeaderCircle.curRadius * cos;

        float footerX1 = mFooterCircle.curx - mFooterCircle.curRadius * sin;
        float footerY1 = mFooterCircle.cury + mFooterCircle.curRadius * cos;

        float footerX2 = mFooterCircle.curx + mFooterCircle.curRadius * sin;
        float footerY2 = mFooterCircle.cury - mFooterCircle.curRadius * cos;

        float anchorX = (mHeaderCircle.curx + mFooterCircle.curx) / 2;
        float anchorY = (mHeaderCircle.cury + mFooterCircle.cury) / 2;

		/* ������������ */
        mPath.reset();
        mPath.moveTo(headerX1, headerY1);
        mPath.quadTo(anchorX, anchorY, footerX1, footerY1);
        mPath.lineTo(footerX2, footerY2);
        mPath.quadTo(anchorX, anchorY, headerX2, headerY2);
        mPath.lineTo(headerX1, headerY1);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (isAnim)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setWidthAndHeight(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                // ���ó�MATCH_PARENT�󣬻��ظ�����һ�θ��ؼ�padding������������Ҫ��ȥ
                mFooterCircle.ox = mFooterCircle.curx = mHeaderCircle.ox = mHeaderCircle.curx = mX
                        + mOriginalWidth / 2 - mParentPaddingLeft;
                mFooterCircle.oy = mFooterCircle.cury = mHeaderCircle.oy = mHeaderCircle.cury = mY
                        + mOriginalHeight / 2 - mParentPaddingTop;
                if (mView != null) {
                    mView.setX(mX + getPaddingLeft() - mParentPaddingLeft);
                    mView.setY(mY + getPaddingTop() - mParentPaddingTop);
                }
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                // ��ǰ���
                isDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDown)
                    break;
                // ƫ��
                float detalX = event.getRawX() - mDownX;
                float detalY = event.getRawY() - mDownY;

                mFooterCircle.curx = mFooterCircle.ox + detalX;
                mFooterCircle.cury = mFooterCircle.oy + detalY;
                if (mView != null) {
                    mView.setX(mX + getPaddingLeft() + detalX - mParentPaddingLeft);
                    mView.setY(mY + getPaddingTop() + detalY - mParentPaddingTop);
                }
                doAdhere();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // ���û�а��£�ֱ�ӷ��أ�����Ϊ�˷�ֹ��ָ����
                if (!isDown)
                    break;
                isDown = false;
                isAnim = true;
                if (isAdherent)
                    startAnim();
                else if (mOnAdherentListener != null) {
                    mFooterCircle.curRadius = 0;
                    mOnAdherentListener.onDismiss();
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * ����ճ��Ч���߼�
     */
    private void doAdhere() {
        // ��Բ�ĵľ���
        float distance = (float) Math.sqrt(Math.pow(mFooterCircle.curx
                - mHeaderCircle.ox, 2)
                + Math.pow(mFooterCircle.cury - mHeaderCircle.oy, 2));
        // ���ű���
        float scale = 1 - distance / mMaxAdherentLength;
        mHeaderCircle.curRadius = Math.max(
                mHeaderCircle.originalRadius * scale, mMinHeaderCircleRadius);
        if (distance > mMaxAdherentLength && isDismissed) {
            isAdherent = false;
            mHeaderCircle.curRadius = 0;
        } else
            isAdherent = true;
    }

    /**
     * ��ʼճ������
     */
    private void startAnim() {

		/* x���� */
        ValueAnimator xValueAnimator = ValueAnimator.ofFloat(
                mFooterCircle.curx, mFooterCircle.ox);
        xValueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mFooterCircle.curx = (float) (Float) animation
                                .getAnimatedValue();
                        invalidate();
                    }
                });

		/* y���� */
        ValueAnimator yValueAnimator = ValueAnimator.ofFloat(
                mFooterCircle.cury, mFooterCircle.oy);
        yValueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mFooterCircle.cury = (float) (Float) animation
                                .getAnimatedValue();
                        invalidate();
                    }
                });

		/* �û���ӵ���ͼx��y���� */
        ObjectAnimator objectAnimator = null;
        if (mView != null) {
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X",
                    mFooterCircle.curx - mFooterCircle.curRadius
                            - getPaddingLeft(), mX + getPaddingLeft()
                            - mParentPaddingLeft);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y",
                    mFooterCircle.cury - mFooterCircle.curRadius
                            - getPaddingTop(), mY + getPaddingTop()
                            - mParentPaddingTop);
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, pvhX,
                    pvhY);
        }

		/* �������� */
        AnimatorSet animSet = new AnimatorSet();
        if (mView != null)
            animSet.playTogether(xValueAnimator, yValueAnimator, objectAnimator);
        else
            animSet.playTogether(xValueAnimator, yValueAnimator);
        animSet.setInterpolator(new BounceInterpolator());
        animSet.setDuration(400);
        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reset();
            }
        });
    }

    /**
     * Բ����
     *
     * @author Administrator
     */
    private class Circle {
        /**
         * ��ʼ����x,y
         */
        float ox;
        float oy;
        /**
         * ��ǰ����x,y
         */
        float curx;
        float cury;
        // ��ʼ�뾶
        float originalRadius;
        // ��ǰ�뾶
        float curRadius;
    }

    /**
     * ����ӿڣ����ü�����
     *
     * @param onAdherentListener
     */
    public void setOnAdherentListener(OnAdherentListener onAdherentListener) {
        mOnAdherentListener = onAdherentListener;
    }

    /**
     * ������
     */
    public interface OnAdherentListener {
        public void onDismiss();
    }

    /**
     * ����ӿڣ�������ɫ
     *
     * @param color
     */
    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
    }

    /**
     * ����ӿڣ������Ƿ���Գ���
     */
    public void setDismissedEnable(boolean isDismissed) {
        this.isDismissed = isDismissed;
    }

    /**
     * ����ӿڣ�����ճ������󳤶�
     *
     * @param maxAdherentLength
     */
    public void setMaxAdherentLength(int maxAdherentLength) {
        mMaxAdherentLength = maxAdherentLength;
    }

    /**
     * ����ӿڣ�����ͷ������С�뾶
     *
     * @param minHeaderCircleRadius
     */
    public void setMinHeaderCircleRadius(int minHeaderCircleRadius) {
        mMinHeaderCircleRadius = minHeaderCircleRadius;
    }

}
