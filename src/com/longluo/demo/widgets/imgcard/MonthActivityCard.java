package com.longluo.demo.widgets.imgcard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longluo.demo.R;
import com.longluo.demo.activitycard.bean.ActivityInfo;

/**
 * MonthActivityCard
 * 
 * @author luolong
 * @version
 * @Date 2015-6-26 下午4:32:55
 */
public class MonthActivityCard extends RelativeLayout {
    private static final String TAG = MonthActivityCard.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private TextView mCardTitle;
    private LinearLayout mCardGrid;

    private int mItemLayout = R.layout.image_card_item;
    private Calendar dateDisplay;
    private ArrayList<ImageCellLayout> mCells = new ArrayList<ImageCellLayout>();
    private ArrayList<ActivityInfo> mActivityInfos = new ArrayList<ActivityInfo>();

    public MonthActivityCard(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MonthActivityCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MonthActivityCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public void init() {
        if (isInEditMode()) {
            return;
        }

        mLayoutInflater = LayoutInflater.from(mContext);

        View layout = LayoutInflater.from(mContext).inflate(R.layout.image_card_view, null, false);

        mCardTitle = (TextView) layout.findViewById(R.id.cardTitle);
        mCardGrid = (LinearLayout) layout.findViewById(R.id.cardGrid);

        addView(layout);
        updateCells();
    }
    
    public void initData(ArrayList<ActivityInfo> activityInfos) {
        mActivityInfos = activityInfos;
    }

    public void updateCells() {
        Log.d("luolong", TAG + ",updateCells, " + mActivityInfos.size());
        
        int index = 0;
        
        for (int y = 0; y < mCardGrid.getChildCount(); y++) {
            LinearLayout row = (LinearLayout) mCardGrid.getChildAt(y);

            for (int x = 0; x < row.getChildCount() && index < mActivityInfos.size(); x++) {
                ImageCellLayout cell = (ImageCellLayout) row.getChildAt(x);

                ImageView cellContent = (ImageView) mLayoutInflater.inflate(mItemLayout, cell, false);
                cellContent.setImageLevel(mActivityInfos.get(index).mActivityLevel);
                index++;
                cell.addView(cellContent);
                mCells.add(cell);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Log.d("luolong", TAG + ",changed=" + changed + " l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);

        if (changed && mCells.size() > 0) {
            int size = (r - l) / 16;
            for (ImageCellLayout cell : mCells) {
                cell.getLayoutParams().height = size;
            }

            Log.d("luolong", TAG + "," + mCells.size() + ",size=" + size);
        }
    }

    public int getItemLayout() {
        return mItemLayout;
    }

    public void setItemLayout(int itemLayout) {
        this.mItemLayout = itemLayout;
    }

    public Calendar getDateDisplay() {
        return dateDisplay;
    }

    public void setDateDisplay(Calendar dateDisplay) {
        this.dateDisplay = dateDisplay;
        mCardTitle.setText(new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(dateDisplay.getTime()));
    }

    /**
     * call after change any input data - to refresh view
     */
    public void notifyChanges() {
        updateCells();
    }

}
