package smt.alignment_fab_bar;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 *  @author 宋美涛
 *  @email song_meitao@163.com
 */
public class FabBarView extends View {


    private Paint mBluePaint;

    private Paint mPurplePaint;

    private Paint mWPaint;

    private int mWidth;//宽

    private int mHeight;//高

    private int mRadius;//半径

    private int mXCenter;//x圆心

    private int mYCenter;//y圆心

    private int mMenuRectTop = 20;//菜单矩形的高度

    private int mMenuRectLeftFinal = 80;//50

    private int mMenuRectLeft ;

    private int mMenuRound = 40;//菜单矩形圆角  40

    private int mLen = 15;

    private int mWradio = 8;

    private final int STATE_OPEN = 0;

    private final int STATE_CLOSE = 1;

    private int STATE = STATE_OPEN;
    private int x1;
    private int y1;
    private int x3;
    private int y3;
    private int x2;
    private int y2;
    private int x4;
    private int y4;
    private int finalX1;
    private int finalX4;

    private int mMenuColor=Color.parseColor("#AB47BC");//默认紫色
    private int mCircleColor = Color.parseColor("#288AFF");//默认蓝色
    private int mIconColor = Color.parseColor("#ffffff");//默认白色

    private OnMenuOnClickListener mOnClickListener;


    public FabBarView(Context context) {
        this(context, null);

    }

    public FabBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public FabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mBluePaint = new Paint();
        mBluePaint.setAntiAlias(true);
        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setStrokeWidth(10);
        mBluePaint.setColor(mCircleColor);//蓝色

        mPurplePaint = new Paint();
        mPurplePaint.setAntiAlias(true);
        mPurplePaint.setStyle(Paint.Style.FILL);
        mPurplePaint.setStrokeWidth(10);
        mPurplePaint.setColor(mMenuColor);//紫色

        mWPaint = new Paint();
        mWPaint.setAntiAlias(true);
        mWPaint.setStyle(Paint.Style.FILL);
        mWPaint.setStrokeWidth(17);
        mWPaint.setColor(mIconColor);
        mWPaint.setStrokeCap(Paint.Cap.ROUND);


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - 5;
        mXCenter = mWidth / 2;
        mYCenter = mHeight / 2;//圆心按照高来计算
        mRadius = mHeight / 2;

        mMenuRectLeft=mMenuRectLeftFinal;//菜单矩形的框度,可变

        finalX1 = (int) (mXCenter + mLen * Math.sqrt(2));//固定的x1
        x1 = finalX1;//可变的s1
        y1 = (int) (mYCenter - mLen * Math.sqrt(2));
        x3 = (int) (mXCenter - mLen * Math.sqrt(2));
        y3 = (int) (mYCenter + mLen * Math.sqrt(2));
        x2 = (int) (mXCenter + mLen * Math.sqrt(2));
        y2 = (int) (mYCenter + mLen * Math.sqrt(2));
        finalX4 = (int) (mXCenter - mLen * Math.sqrt(2));//固定的x4
        x4 = finalX4;
        y4 = (int) (mYCenter - mLen * Math.sqrt(2));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF menuRect = new RectF(mMenuRectLeft, mMenuRectTop, mWidth - mMenuRectLeft, mHeight - mMenuRectTop);
        canvas.drawRoundRect(menuRect, mMenuRound, mMenuRound, mPurplePaint);

        canvas.drawCircle(mXCenter, mYCenter, mRadius, mBluePaint);

       // canvas.drawCircle(x1, y1, mWradio, mWPaint);
       // canvas.drawCircle(x3, y3, mWradio, mWPaint);
       // canvas.drawCircle(x2, y2, mWradio, mWPaint);
       // canvas.drawCircle(x4, y4, mWradio, mWPaint);
        canvas.drawLine(x1, y1, x3, y3, mWPaint);
        canvas.drawLine(x2, y2, x4, y4, mWPaint);

    }


    private void doAnim() {
        switch (STATE) {
            case STATE_OPEN:
                //当前是打开，关闭操作
                //  (View 的宽 - 圆半径）/2 - left
                performAnimate(mMenuRectLeft, (mWidth - mRadius) / 2 - (0) );
                performAnimateX1(x1, mXCenter);
                performAnimateX4(x4, mXCenter);

                STATE = STATE_CLOSE;
                break;
            case STATE_CLOSE:
                //当前是关闭，打开操作
                performAnimate(mMenuRectLeft, mMenuRectLeftFinal);
                performAnimateX1(mXCenter, finalX1);
                performAnimateX4(mXCenter, finalX4);

                STATE = STATE_OPEN;
                break;
        }
    }


    private void performAnimate(final int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            //持有一个IntEvaluator对象，方便下面估值的时候使用
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //获得当前动画的进度值，整型，1-100之间
                int currentValue = (Integer) animator.getAnimatedValue();

                //计算当前进度占整个动画过程的比例，浮点型，0-1之间
                float fraction = currentValue / 100f;

                mMenuRectLeft = mEvaluator.evaluate(fraction, start, end);
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(1000).start();
    }

    private void performAnimateX1(final int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentValue = (Integer) animator.getAnimatedValue();
                float fraction = currentValue / 100f;
                x1 = mEvaluator.evaluate(fraction, start, end);
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(1000).start();
    }

    private void performAnimateX4(final int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentValue = (Integer) animator.getAnimatedValue();
                float fraction = currentValue / 100f;
                x4 = mEvaluator.evaluate(fraction, start, end);
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(1000).start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                int clickX = (int) event.getRawX();
                int clickY = (int) event.getRawY();

                int[] location = new int[2];
                this.getLocationInWindow(location);
                //XY是控件在屏幕的XY，而不是定义的圆心
                int cenY = location[1] + mYCenter;

                if (Math.pow(mXCenter - clickX, 2) + Math.pow(cenY - clickY, 2) <= Math.pow(mRadius, 2)) {
                    doAnim();
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    /**
     * 设置菜单圆角
     */
    public void setMenuRound(int value) {
        this.mMenuRound = value;
    }

    /**
     * 设置菜单X V 的长度
     *
     * @param value
     */
    public void setLen(int value) {
        this.mLen = value;
    }

    /**
     * 设置菜单左边和右边的距离
     */
    public void setMenuLeftAndRight(int value) {
        this.mMenuRectLeftFinal = value;
    }

    /**
     * 设置菜单颜色
     *
     * @param color
     */
    public void setMenuColor(int color) {
        this.mMenuColor = color;
    }

    /**
     * 设置圆颜色
     *
     * @param color
     */
    public void setCircleColor(int color) {

        this.mCircleColor = color;

    }

    /**
     * 设置圆中心图标颜色
     *
     * @param color
     */
    public void setIconColor(int color) {

        this.mIconColor = color;

    }

    /**
     * 设置菜单打开
     */
    public void setMenuOpen() {
        doAnim();
    }

    /**
     * 设置菜单关闭
     */
    public void setMenuClose() {
        doAnim();
    }

    /**
     * 设置点击事件，用于处理打开或关闭菜单的其他事件
     *
     * @param listener
     */
    public void setOnMenuOnClickListener(OnMenuOnClickListener listener) {
        this.mOnClickListener = listener;
    }

    /**
     * 菜单点击事件
     */
    public static interface OnMenuOnClickListener {
        public void onClick(View v);
    }

}
