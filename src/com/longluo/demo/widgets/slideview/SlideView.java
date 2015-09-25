package com.longluo.demo.widgets.slideview;

import com.longluo.demo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class SlideView extends LinearLayout {
	private static final String TAG = "SlideView";

	private Context mContext;
	private LinearLayout mViewContent;
	private RelativeLayout mHolder;
	private Scroller mScroller;

	private int mHolderWidth = 120;

	private int mLastX = 0;
	private int mLastY = 0;
	private static final int TAN = 2;

	public SlideView(Context context) {
		super(context);
		initView();
	}

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mContext = getContext();
		mScroller = new Scroller(mContext);

		setOrientation(LinearLayout.HORIZONTAL);
		View.inflate(mContext, R.layout.slideview_merge, this);
		mViewContent = (LinearLayout) findViewById(R.id.view_content);

		mHolderWidth = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources()
						.getDisplayMetrics()));

		// TypedValue.COMPLEX_UNIT_PX,ʱ�ڲ�ͬ�ֻ��ϵ���ֵ����һ���ģ�����ԭ������120.
		//
		/*
		 * mHolderWidth = Math.round(TypedValue.applyDimension(
		 * TypedValue.COMPLEX_UNIT_PX, mHolderWidth, getResources()
		 * .getDisplayMetrics()));
		 */
		// TypedValue.COMPLEX_UNIT_DIP�ڲ�ͬ�ֻ�����ʾ���ǲ�ͬ�ģ��Ƕ������豸�ġ�
		// �ڹ�˾e6��������ʾ����180����lg����ʾ240
		System.out.println("ת�����mHolderWidth is " + mHolderWidth);
	}

	public void setButtonText(CharSequence text) {
		((TextView) findViewById(R.id.delete)).setText(text);
	}

	public void setContentView(View view) {
		mViewContent.addView(view);
	}

	public void onRequireTouchEvent(MotionEvent event) {
		// ע��ִ��˳��ÿ�β�ͬ��action��Ҫִ��int x=event.getX();��mLastX=x;
		// ע��˴���event��getX.Խ����ĵ�x����ԽС��Խ���ң�x������Խ��
		int x = (int) event.getX();
		int y = (int) event.getY();
		System.out.println("event.getX is " + x + "event.getY is --" + y);
		// �ô������ǻ�û����˶��پ��룬���ݴ�ӡ��log����������180ʱ�Ͳ��ٻ�����
		int scrollX = getScrollX();
		System.out.println("�����˶��پ���" + scrollX);
		Log.d(TAG, "x=" + x + "  y=" + y);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("----------down down down down ");

			break;

		case MotionEvent.ACTION_MOVE:
			System.out.println("-----------move move move ");
			int deltaX = x - mLastX;
			int deltaY = y - mLastY;
			System.out.println("----move deltaX is" + deltaX);
			if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
				break;
			}
			// ע����һ���Ĵ���ʹ�õļ���
			int newScrollX = scrollX - deltaX;
			System.out.println("--------newScroll is" + newScrollX);
			if (deltaX != 0) {
				// �˴�Ϊ�����ԭ���һ��򻬶�����ʼ״̬�����򻬶�������
				if (newScrollX < 0) {
					newScrollX = 0;
				} else if (newScrollX > mHolderWidth) {
					newScrollX = mHolderWidth;
				}
				// scrollTo�Ĳ���x����Ϊ���������ҹ�������Ϊ�������������
				this.scrollTo(newScrollX, 0);
			}
			break;

		case MotionEvent.ACTION_UP:
			System.out.println("------------up");
			newScrollX = 0;
			// ����Ѿ�����le0.75���ľ��룬��ֱ�ӻ�����Ŀ�ĵأ����򻬶���ԭ����λ��
			if (scrollX - mHolderWidth * 0.75 > 0) {
				newScrollX = mHolderWidth;
			}

			this.smoothScrollTo(newScrollX, 0);

			break;

		default:
			break;
		}

		mLastX = x;
		mLastY = y;
		System.out.println("---------last x -----" + mLastX);
	}

	private void smoothScrollTo(int destX, int destY) {
		// ����������ָ��λ��
		// ���ع�����view����߽�
		int scrollX = getScrollX();
		// �ڸ������У������󻬶�ʱ�˴���scrollX������
		System.out.println("scrollX is" + scrollX);
		// getScrollX�ǵõ��ܹ������ľ��룬�ض�Ϊ����
		int delta = destX - scrollX;
		// startScroll�еĲ���xofferset���Ϊ���������󻬶���yofferset���Ϊ���������ϻ���
		mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

}
