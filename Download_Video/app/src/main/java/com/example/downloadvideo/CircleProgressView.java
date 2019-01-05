package com.example.downloadvideo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author White Date 2017/4/23 Time 14:38
 */

public class CircleProgressView extends ProgressBar {

    @IntDef({PROGRESS_STYLE_NORMAL, PROGRESS_STYLE_FILL_IN,
            PROGRESS_STYLE_FILL_IN_ARC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressStyle {

    }

    private static final int PROGRESS_STYLE_NORMAL = 0;
    private static final int PROGRESS_STYLE_FILL_IN = 1;
    private static final int PROGRESS_STYLE_FILL_IN_ARC = 2;

    private int mReachBarSize = dp2px(getContext(), 2);

    private int mNormalBarSize = dp2px(getContext(), 2);

    private int mReachBarColor = Color.parseColor("#108ee9");

    private int mNormalBarColor = Color.parseColor("#FFD3D6DA");

    private int mTextSize = sp2px(getContext(), 14);

    private int mTextColor = Color.parseColor("#108ee9");

    private float mTextSkewX;

    private String mTextSuffix = "%";

    private String mTextPrefix = "";

    private boolean mTextVisible = true;

    private boolean mReachCapRound;

    private int mRadius = dp2px(getContext(), 20);

    private int mStartArc;

    private int mInnerBackgroundColor;

    private int mProgressStyle = PROGRESS_STYLE_NORMAL;

    private int mInnerPadding = dp2px(getContext(), 1);

    private int mOuterColor;

    private boolean needDrawInnerBackground;

    private RectF rectF;

    private RectF rectInner;

    private int mOuterSize = dp2px(getContext(), 1);

    private Paint mTextPaint;

    private Paint mNormalPaint;

    private Paint mReachPaint;

    private Paint mInnerBackgroundPaint;

    private Paint mOutPaint;

    private int mRealWidth;
    private int mRealHeight;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttributes(attrs);
        initPaint();
    }

    /**
     * Ã¥Ë†ï¿½Ã¥Â§â€¹Ã¥Å’â€“Ã§â€�Â»Ã§Â¬â€�
     */
    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextSkewX(mTextSkewX);
        mTextPaint.setAntiAlias(true); // Ã¦Å â€”Ã©â€�Â¯Ã©Â½Â¿

        mNormalPaint = new Paint();
        mNormalPaint.setColor(mNormalBarColor);
        mNormalPaint
                .setStyle(mProgressStyle == PROGRESS_STYLE_FILL_IN_ARC ? Paint.Style.FILL
                        : Paint.Style.STROKE);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStrokeWidth(mNormalBarSize);

        mReachPaint = new Paint();
        mReachPaint.setColor(mReachBarColor);
        mReachPaint
                .setStyle(mProgressStyle == PROGRESS_STYLE_FILL_IN_ARC ? Paint.Style.FILL
                        : Paint.Style.STROKE);
        mReachPaint.setAntiAlias(true);
        mReachPaint.setStrokeCap(mReachCapRound ? Paint.Cap.ROUND
                : Paint.Cap.BUTT);
        mReachPaint.setStrokeWidth(mReachBarSize);

        if (needDrawInnerBackground) {
            mInnerBackgroundPaint = new Paint();
            mInnerBackgroundPaint.setStyle(Paint.Style.FILL);
            mInnerBackgroundPaint.setAntiAlias(true);
            mInnerBackgroundPaint.setColor(mInnerBackgroundColor);
        }
        if (mProgressStyle == PROGRESS_STYLE_FILL_IN_ARC) {
            mOutPaint = new Paint();
            mOutPaint.setStyle(Paint.Style.STROKE);
            mOutPaint.setColor(mOuterColor);
            mOutPaint.setStrokeWidth(mOuterSize);
            mOutPaint.setAntiAlias(true);
        }
    }

    private void obtainAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.CircleProgressView);
        mProgressStyle = ta.getInt(
                R.styleable.CircleProgressView_progressStyle,
                PROGRESS_STYLE_NORMAL);
        // Ã¨Å½Â·Ã¥ï¿½â€“Ã¤Â¸â€°Ã§Â§ï¿½Ã©Â£Å½Ã¦Â Â¼Ã©â‚¬Å¡Ã§â€�Â¨Ã§Å¡â€žÃ¥Â±Å¾Ã¦â‚¬Â§
        mNormalBarSize = (int) ta.getDimension(
                R.styleable.CircleProgressView_progressNormalSize,
                mNormalBarSize);
        mNormalBarColor = ta.getColor(
                R.styleable.CircleProgressView_progressNormalColor,
                mNormalBarColor);

        mReachBarSize = (int) ta
                .getDimension(R.styleable.CircleProgressView_progressReachSize,
                        mReachBarSize);
        mReachBarColor = ta.getColor(
                R.styleable.CircleProgressView_progressReachColor,
                mReachBarColor);

        mTextSize = (int) ta.getDimension(
                R.styleable.CircleProgressView_progressTextSize, mTextSize);
        mTextColor = ta.getColor(
                R.styleable.CircleProgressView_progressTextColor, mTextColor);
        mTextSkewX = ta.getDimension(
                R.styleable.CircleProgressView_progressTextSkewX, 0);
        if (ta.hasValue(R.styleable.CircleProgressView_progressTextSuffix)) {
            mTextSuffix = ta
                    .getString(R.styleable.CircleProgressView_progressTextSuffix);
        }
        if (ta.hasValue(R.styleable.CircleProgressView_progressTextPrefix)) {
            mTextPrefix = ta
                    .getString(R.styleable.CircleProgressView_progressTextPrefix);
        }
        mTextVisible = ta.getBoolean(
                R.styleable.CircleProgressView_progressTextVisible,
                mTextVisible);

        mRadius = (int) ta.getDimension(R.styleable.CircleProgressView_radius,
                mRadius);
        rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);

        switch (mProgressStyle) {
            case PROGRESS_STYLE_FILL_IN:
                mReachBarSize = 0;
                mNormalBarSize = 0;
                mOuterSize = 0;
                break;
            case PROGRESS_STYLE_FILL_IN_ARC:
                mStartArc = ta.getInt(
                        R.styleable.CircleProgressView_progressStartArc, 0) + 270;
                mInnerPadding = (int) ta.getDimension(
                        R.styleable.CircleProgressView_innerPadding, mInnerPadding);
                mOuterColor = ta.getColor(
                        R.styleable.CircleProgressView_outerColor, mReachBarColor);
                mOuterSize = (int) ta.getDimension(
                        R.styleable.CircleProgressView_outerSize, mOuterSize);
                mReachBarSize = 0;// Ã¥Â°â€ Ã§â€�Â»Ã§Â¬â€�Ã¥Â¤Â§Ã¥Â°ï¿½Ã©â€¡ï¿½Ã§Â½Â®Ã¤Â¸Âº0
                mNormalBarSize = 0;
                if (!ta.hasValue(R.styleable.CircleProgressView_progressNormalColor)) {
                    mNormalBarColor = Color.TRANSPARENT;
                }
                int mInnerRadius = mRadius - mOuterSize / 2 - mInnerPadding;
                rectInner = new RectF(-mInnerRadius, -mInnerRadius, mInnerRadius,
                        mInnerRadius);

                break;
            case PROGRESS_STYLE_NORMAL:
                mReachCapRound = ta.getBoolean(
                        R.styleable.CircleProgressView_reachCapRound, true);
                mStartArc = ta.getInt(
                        R.styleable.CircleProgressView_progressStartArc, 0) + 270;
                if (ta.hasValue(R.styleable.CircleProgressView_innerBackgroundColor)) {
                    mInnerBackgroundColor = ta.getColor(
                            R.styleable.CircleProgressView_innerBackgroundColor,
                            Color.argb(0, 0, 0, 0));
                    needDrawInnerBackground = true;
                }
                break;
        }

        ta.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        int maxBarPaintWidth = Math.max(mReachBarSize, mNormalBarSize);
        int maxPaintWidth = Math.max(maxBarPaintWidth, mOuterSize);
        int height = 0;
        int width = 0;
        switch (mProgressStyle) {
            case PROGRESS_STYLE_FILL_IN:
                height = getPaddingTop() + getPaddingBottom() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2); // Ã§â€ºÂ´Ã¥Â¾â€ž
                width = getPaddingLeft() + getPaddingRight() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2); // Ã§â€ºÂ´Ã¥Â¾â€ž
                break;
            case PROGRESS_STYLE_FILL_IN_ARC:
                height = getPaddingTop() + getPaddingBottom() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2) // Ã§â€ºÂ´Ã¥Â¾â€ž
                        + maxPaintWidth;// Ã¨Â¾Â¹Ã¦Â¡â€ 
                width = getPaddingLeft() + getPaddingRight() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2) // Ã§â€ºÂ´Ã¥Â¾â€ž
                        + maxPaintWidth;// Ã¨Â¾Â¹Ã¦Â¡â€ 
                break;
            case PROGRESS_STYLE_NORMAL:
                height = getPaddingTop() + getPaddingBottom() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2) // Ã§â€ºÂ´Ã¥Â¾â€ž
                        + maxBarPaintWidth;// Ã¨Â¾Â¹Ã¦Â¡â€ 
                width = getPaddingLeft() + getPaddingRight() // Ã¨Â¾Â¹Ã¨Â·ï¿½
                        + Math.abs(mRadius * 2) // Ã§â€ºÂ´Ã¥Â¾â€ž
                        + maxBarPaintWidth;// Ã¨Â¾Â¹Ã¦Â¡â€ 
                break;
        }

        mRealWidth = resolveSize(width, widthMeasureSpec);
        mRealHeight = resolveSize(height, heightMeasureSpec);

        setMeasuredDimension(mRealWidth, mRealHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        switch (mProgressStyle) {
            case PROGRESS_STYLE_NORMAL:
                drawNormalCircle(canvas);
                break;
            case PROGRESS_STYLE_FILL_IN:
                drawFillInCircle(canvas);
                break;
            case PROGRESS_STYLE_FILL_IN_ARC:
                drawFillInArcCircle(canvas);
                break;
        }
    }

    /**
     * Ã§Â»ËœÃ¥Ë†Â¶PROGRESS_STYLE_FILL_IN_ARCÃ¥Å“â€ Ã¥Â½Â¢
     */
    private void drawFillInArcCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(mRealWidth / 2, mRealHeight / 2);
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¥Â¤â€“Ã¥Â±â€šÃ¥Å“â€ Ã§Å½Â¯
        canvas.drawArc(rectF, 0, 360, false, mOutPaint);
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¥â€ â€¦Ã¥Â±â€šÃ¨Â¿â€ºÃ¥ÂºÂ¦Ã¥Â®Å¾Ã¥Â¿Æ’Ã¥Å“â€ Ã¥Â¼Â§
        // Ã¥â€ â€¦Ã¥Â±â€šÃ¥Å“â€ Ã¥Â¼Â§Ã¥ï¿½Å Ã¥Â¾â€ž
        float reachArc = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(rectInner, mStartArc, reachArc, true, mReachPaint);

        // Ã§Â»ËœÃ¥Ë†Â¶Ã¦Å“ÂªÃ¥Ë†Â°Ã¨Â¾Â¾Ã¨Â¿â€ºÃ¥ÂºÂ¦
        if (reachArc != 360) {
            canvas.drawArc(rectInner, reachArc + mStartArc, 360 - reachArc,
                    true, mNormalPaint);
        }

        canvas.restore();
    }

    /**
     * Ã§Â»ËœÃ¥Ë†Â¶PROGRESS_STYLE_FILL_INÃ¥Å“â€ Ã¥Â½Â¢
     */
    private void drawFillInCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(mRealWidth / 2, mRealHeight / 2);
        float progressY = getProgress() * 1.0f / getMax() * (mRadius * 2);
        float angle = (float) (Math.acos((mRadius - progressY) / mRadius) * 180 / Math.PI);
        float startAngle = 90 + angle;
        float sweepAngle = 360 - angle * 2;
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¦Å“ÂªÃ¥Ë†Â°Ã¨Â¾Â¾Ã¥Å’ÂºÃ¥Å¸Å¸
        rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        mNormalPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mNormalPaint);
        // Ã§Â¿Â»Ã¨Â½Â¬180Ã¥ÂºÂ¦Ã§Â»ËœÃ¥Ë†Â¶Ã¥Â·Â²Ã¥Ë†Â°Ã¨Â¾Â¾Ã¥Å’ÂºÃ¥Å¸Å¸
        canvas.rotate(180);
        mReachPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(rectF, 270 - angle, angle * 2, false, mReachPaint);
        // Ã¦â€“â€¡Ã¥Â­â€”Ã¦ËœÂ¾Ã§Â¤ÂºÃ¥Å“Â¨Ã¦Å“â‚¬Ã¤Â¸Å Ã¥Â±â€šÃ¦Å“â‚¬Ã¥ï¿½Å½Ã§Â»ËœÃ¥Ë†Â¶
        canvas.rotate(180);
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¦â€“â€¡Ã¥Â­â€”
        if (mTextVisible) {
            String text = mTextPrefix + getProgress() + mTextSuffix;
            float textWidth = mTextPaint.measureText(text);
            float textHeight = (mTextPaint.descent() + mTextPaint.ascent());
            canvas.drawText(text, -textWidth / 2, -textHeight / 2, mTextPaint);
        }
    }

    /**
     * Ã§Â»ËœÃ¥Ë†Â¶PROGRESS_STYLE_NORMALÃ¥Å“â€ Ã¥Â½Â¢
     */
    private void drawNormalCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(mRealWidth / 2, mRealHeight / 2);
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¥â€ â€¦Ã©Æ’Â¨Ã¥Å“â€ Ã¥Â½Â¢Ã¨Æ’Å’Ã¦â„¢Â¯Ã¨â€°Â²
        if (needDrawInnerBackground) {
            canvas.drawCircle(0, 0,
                    mRadius - Math.min(mReachBarSize, mNormalBarSize) / 2,
                    mInnerBackgroundPaint);
        }
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¦â€“â€¡Ã¥Â­â€”
        if (mTextVisible) {
            String text = mTextPrefix + getProgress() + mTextSuffix;
            float textWidth = mTextPaint.measureText(text);
            float textHeight = (mTextPaint.descent() + mTextPaint.ascent());
            canvas.drawText(text, -textWidth / 2, -textHeight / 2, mTextPaint);
        }
        // Ã¨Â®Â¡Ã§Â®â€”Ã¨Â¿â€ºÃ¥ÂºÂ¦Ã¥â‚¬Â¼
        float reachArc = getProgress() * 1.0f / getMax() * 360;
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¦Å“ÂªÃ¥Ë†Â°Ã¨Â¾Â¾Ã¨Â¿â€ºÃ¥ÂºÂ¦
        if (reachArc != 360) {
            canvas.drawArc(rectF, reachArc + mStartArc, 360 - reachArc, false,
                    mNormalPaint);
        }
        // Ã§Â»ËœÃ¥Ë†Â¶Ã¥Â·Â²Ã¥Ë†Â°Ã¨Â¾Â¾Ã¨Â¿â€ºÃ¥ÂºÂ¦
        canvas.drawArc(rectF, mStartArc, reachArc, false, mReachPaint);
        canvas.restore();
    }

    /**
     * Ã¥Å Â¨Ã§â€�Â»Ã¨Â¿â€ºÃ¥ÂºÂ¦(0-Ã¥Â½â€œÃ¥â€°ï¿½Ã¨Â¿â€ºÃ¥ÂºÂ¦)
     *
     * @param duration Ã¥Å Â¨Ã§â€�Â»Ã¦â€”Â¶Ã©â€¢Â¿
     */
    public void runProgressAnim(long duration) {
        setProgressInTime(0, duration);
    }

    /**
     * @param progress Ã¨Â¿â€ºÃ¥ÂºÂ¦Ã¥â‚¬Â¼
     * @param duration Ã¥Å Â¨Ã§â€�Â»Ã¦â€™Â­Ã¦â€�Â¾Ã¦â€”Â¶Ã©â€”Â´
     */
    public void setProgressInTime(final int progress, final long duration) {
        setProgressInTime(progress, getProgress(), duration);
    }

    /**
     * @param startProgress Ã¨ÂµÂ·Ã¥Â§â€¹Ã¨Â¿â€ºÃ¥ÂºÂ¦
     * @param progress      Ã¨Â¿â€ºÃ¥ÂºÂ¦Ã¥â‚¬Â¼
     * @param duration      Ã¥Å Â¨Ã§â€�Â»Ã¦â€™Â­Ã¦â€�Â¾Ã¦â€”Â¶Ã©â€”Â´
     */
    public void setProgressInTime(int startProgress, final int progress,
                                  final long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startProgress,
                progress);
        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        // Ã¨Å½Â·Ã¥Â¾â€”Ã¥Â½â€œÃ¥â€°ï¿½Ã¥Å Â¨Ã§â€�Â»Ã§Å¡â€žÃ¨Â¿â€ºÃ¥ÂºÂ¦Ã¥â‚¬Â¼Ã¯Â¼Å’Ã¦â€¢Â´Ã¥Å¾â€¹Ã¯Â¼Å’1-100Ã¤Â¹â€¹Ã©â€”Â´
                        int currentValue = (Integer) animator
                                .getAnimatedValue();
                        setProgress(currentValue);
                    }
                });
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public int getReachBarSize() {
        return mReachBarSize;
    }

    public void setReachBarSize(int reachBarSize) {
        mReachBarSize = dp2px(getContext(), reachBarSize);
        invalidate();
    }

    public int getNormalBarSize() {
        return mNormalBarSize;
    }

    public void setNormalBarSize(int normalBarSize) {
        mNormalBarSize = dp2px(getContext(), normalBarSize);
        invalidate();
    }

    public int getReachBarColor() {
        return mReachBarColor;
    }

    public void setReachBarColor(int reachBarColor) {
        mReachBarColor = reachBarColor;
        invalidate();
    }

    public int getNormalBarColor() {
        return mNormalBarColor;
    }

    public void setNormalBarColor(int normalBarColor) {
        mNormalBarColor = normalBarColor;
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = sp2px(getContext(), textSize);
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    public float getTextSkewX() {
        return mTextSkewX;
    }

    public void setTextSkewX(float textSkewX) {
        mTextSkewX = textSkewX;
        invalidate();
    }

    public String getTextSuffix() {
        return mTextSuffix;
    }

    public void setTextSuffix(String textSuffix) {
        mTextSuffix = textSuffix;
        invalidate();
    }

    public String getTextPrefix() {
        return mTextPrefix;
    }

    public void setTextPrefix(String textPrefix) {
        mTextPrefix = textPrefix;
        invalidate();
    }

    public boolean isTextVisible() {
        return mTextVisible;
    }

    public void setTextVisible(boolean textVisible) {
        mTextVisible = textVisible;
        invalidate();
    }

    public boolean isReachCapRound() {
        return mReachCapRound;
    }

    public void setReachCapRound(boolean reachCapRound) {
        mReachCapRound = reachCapRound;
        invalidate();
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = dp2px(getContext(), radius);
        invalidate();
    }

    public int getStartArc() {
        return mStartArc;
    }

    public void setStartArc(int startArc) {
        mStartArc = startArc;
        invalidate();
    }

    public int getInnerBackgroundColor() {
        return mInnerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        mInnerBackgroundColor = innerBackgroundColor;
        invalidate();
    }

    public int getProgressStyle() {
        return mProgressStyle;
    }

    public void setProgressStyle(int progressStyle) {
        mProgressStyle = progressStyle;
        invalidate();
    }

    public int getInnerPadding() {
        return mInnerPadding;
    }

    public void setInnerPadding(int innerPadding) {
        mInnerPadding = dp2px(getContext(), innerPadding);
        int mInnerRadius = mRadius - mOuterSize / 2 - mInnerPadding;
        rectInner = new RectF(-mInnerRadius, -mInnerRadius, mInnerRadius,
                mInnerRadius);
        invalidate();
    }

    public int getOuterColor() {
        return mOuterColor;
    }

    public void setOuterColor(int outerColor) {
        mOuterColor = outerColor;
        invalidate();
    }

    public int getOuterSize() {
        return mOuterSize;
    }

    public void setOuterSize(int outerSize) {
        mOuterSize = dp2px(getContext(), outerSize);
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(STATE, super.onSaveInstanceState());
        // Ã¤Â¿ï¿½Ã¥Â­ËœÃ¥Â½â€œÃ¥â€°ï¿½Ã¦Â Â·Ã¥Â¼ï¿½
        bundle.putInt(PROGRESS_STYLE, getProgressStyle());
        bundle.putInt(RADIUS, getRadius());
        bundle.putBoolean(IS_REACH_CAP_ROUND, isReachCapRound());
        bundle.putInt(START_ARC, getStartArc());
        bundle.putInt(INNER_BG_COLOR, getInnerBackgroundColor());
        bundle.putInt(INNER_PADDING, getInnerPadding());
        bundle.putInt(OUTER_COLOR, getOuterColor());
        bundle.putInt(OUTER_SIZE, getOuterSize());
        // Ã¤Â¿ï¿½Ã¥Â­ËœtextÃ¤Â¿Â¡Ã¦ï¿½Â¯
        bundle.putInt(TEXT_COLOR, getTextColor());
        bundle.putInt(TEXT_SIZE, getTextSize());
        bundle.putFloat(TEXT_SKEW_X, getTextSkewX());
        bundle.putBoolean(TEXT_VISIBLE, isTextVisible());
        bundle.putString(TEXT_SUFFIX, getTextSuffix());
        bundle.putString(TEXT_PREFIX, getTextPrefix());
        // Ã¤Â¿ï¿½Ã¥Â­ËœÃ¥Â·Â²Ã¥Ë†Â°Ã¨Â¾Â¾Ã¨Â¿â€ºÃ¥ÂºÂ¦Ã¤Â¿Â¡Ã¦ï¿½Â¯
        bundle.putInt(REACH_BAR_COLOR, getReachBarColor());
        bundle.putInt(REACH_BAR_SIZE, getReachBarSize());

        // Ã¤Â¿ï¿½Ã¥Â­ËœÃ¦Å“ÂªÃ¥Ë†Â°Ã¨Â¾Â¾Ã¨Â¿â€ºÃ¥ÂºÂ¦Ã¤Â¿Â¡Ã¦ï¿½Â¯
        bundle.putInt(NORMAL_BAR_COLOR, getNormalBarColor());
        bundle.putInt(NORMAL_BAR_SIZE, getNormalBarSize());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;

            mProgressStyle = bundle.getInt(PROGRESS_STYLE);
            mRadius = bundle.getInt(RADIUS);
            mReachCapRound = bundle.getBoolean(IS_REACH_CAP_ROUND);
            mStartArc = bundle.getInt(START_ARC);
            mInnerBackgroundColor = bundle.getInt(INNER_BG_COLOR);
            mInnerPadding = bundle.getInt(INNER_PADDING);
            mOuterColor = bundle.getInt(OUTER_COLOR);
            mOuterSize = bundle.getInt(OUTER_SIZE);

            mTextColor = bundle.getInt(TEXT_COLOR);
            mTextSize = bundle.getInt(TEXT_SIZE);
            mTextSkewX = bundle.getFloat(TEXT_SKEW_X);
            mTextVisible = bundle.getBoolean(TEXT_VISIBLE);
            mTextSuffix = bundle.getString(TEXT_SUFFIX);
            mTextPrefix = bundle.getString(TEXT_PREFIX);

            mReachBarColor = bundle.getInt(REACH_BAR_COLOR);
            mReachBarSize = bundle.getInt(REACH_BAR_SIZE);
            mNormalBarColor = bundle.getInt(NORMAL_BAR_COLOR);
            mNormalBarSize = bundle.getInt(NORMAL_BAR_SIZE);

            initPaint();
            super.onRestoreInstanceState(bundle.getParcelable(STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void invalidate() {
        initPaint();
        super.invalidate();
    }

    private static final String STATE = "state";
    private static final String PROGRESS_STYLE = "progressStyle";
    private static final String TEXT_COLOR = "textColor";
    private static final String TEXT_SIZE = "textSize";
    private static final String TEXT_SKEW_X = "textSkewX";
    private static final String TEXT_VISIBLE = "textVisible";
    private static final String TEXT_SUFFIX = "textSuffix";
    private static final String TEXT_PREFIX = "textPrefix";
    private static final String REACH_BAR_COLOR = "reachBarColor";
    private static final String REACH_BAR_SIZE = "reachBarSize";
    private static final String NORMAL_BAR_COLOR = "normalBarColor";
    private static final String NORMAL_BAR_SIZE = "normalBarSize";
    private static final String IS_REACH_CAP_ROUND = "isReachCapRound";
    private static final String RADIUS = "radius";
    private static final String START_ARC = "startArc";
    private static final String INNER_BG_COLOR = "innerBgColor";
    private static final String INNER_PADDING = "innerPadding";
    private static final String OUTER_COLOR = "outerColor";
    private static final String OUTER_SIZE = "outerSize";


    public int dp2px(Context context, int dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    public int sp2px(Context context, int spVal) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spVal * fontScale + 0.5f);
    }

}
