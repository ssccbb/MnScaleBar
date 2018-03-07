package com.sung.notes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;


/**
 * Created by sung on 2018/3/7.
 */

public class MnScaleBar extends View {

    private Context mContext;

    private int max = 30; //最大刻度
    private int mCountScale; //滑动的总刻度

    private int screenWidth = 720; //默认屏幕分辨率

    private int mScaleMargin = 135; //刻度间距
    private int mScaleHeight = 20; //刻度线的高度
    private int mScaleMaxHeight = mScaleHeight * 2; //整刻度线高度

    private int mRectWidth = max * mScaleMargin; //总宽度
    private int mRectHeight = 150; //高度

    private Scroller mScroller;
    private int mScrollLastX;

    private int mTempScale = screenWidth / mScaleMargin / 2; //判断滑动方向
    private int mScreenMidCountScale = screenWidth / mScaleMargin / 2; //中间刻度

    private OnScrollListener onScrollListener;

    private String tag = MnScaleBar.class.getSimpleName();

    public MnScaleBar(Context context) {
        this(context, null);
    }

    public MnScaleBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MnScaleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        screenWidth = getPhoneW(mContext);

        mTempScale = screenWidth / mScaleMargin / 2; //判断滑动方向
        mScreenMidCountScale = screenWidth / mScaleMargin / 2; //中间刻度

        mScroller = new Scroller(mContext);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置LayoutParams
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mRectWidth, mRectHeight);
        this.setLayoutParams(lp);

        onDrawScale(canvas); //画刻度
        onDrawPointer(canvas); //画指针
        super.onDraw(canvas);
    }

    /**
     * 画刻度
     */
    private void onDrawScale(Canvas canvas) {
        if (canvas == null) return;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setTextAlign(Paint.Align.CENTER); //文字居中
        mPaint.setTextSize(30);
        for (int i = 0; i <= max; i++) {
            canvas.drawLine(i * mScaleMargin, mRectHeight, i * mScaleMargin, mRectHeight - mScaleMaxHeight, mPaint);
            canvas.drawText(String.valueOf(i + 1) + "日", i * mScaleMargin, mRectHeight - mScaleMaxHeight - 20, mPaint);
        }
    }

    /**
     * 画指针
     */
    private void onDrawPointer(Canvas canvas) {
        if (canvas == null) return;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30);
        //每一屏幕刻度的个数/2
        int countScale = screenWidth / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalX / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = tmpCountScale + countScale;
        if (onScrollListener != null) { //回调方法
            onScrollListener.onScrollScale(mCountScale);
        }
        canvas.drawLine(countScale * mScaleMargin + finalX, mRectHeight,
                countScale * mScaleMargin + finalX, mRectHeight - mScaleMaxHeight, mPaint);
        canvas.drawText(String.valueOf(mCountScale + 1) + "日", countScale * mScaleMargin + finalX, mRectHeight - mScaleMaxHeight - 20, mPaint);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mCountScale - mTempScale < 0) { //向右边滑动
                    if (mCountScale < 0) {
                        if (dataX < 0) //禁止继续向右滑动
                            return super.onTouchEvent(event);
                    }
                } else if (mCountScale - mTempScale > 0) { //向左边滑动
                    if (mCountScale > max) {
                        if (dataX > 0) //禁止继续向左滑动
                            return super.onTouchEvent(event);
                    }
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < 0) mCountScale = 0;
                if (mCountScale > max) mCountScale = max;
                int finalX = (mCountScale - mScreenMidCountScale) * mScaleMargin;
                mScroller.setFinalX(finalX); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    public void smoothScrollTo(int position) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), mScroller.getFinalX() + (position - mScreenMidCountScale) * mScaleMargin, mScroller.getFinalY());
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScrollScale(int scale);
    }

    /**
     * 获取手机分辨率--W
     */
    public static int getPhoneW(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int disW = dm.widthPixels;
        return disW;
    }

}
