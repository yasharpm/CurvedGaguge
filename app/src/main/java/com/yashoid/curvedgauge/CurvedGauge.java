package com.yashoid.curvedgauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CurvedGauge extends View {

    private final Paint mPaint = new Paint();
    private final Paint mDividerPaint = new Paint();

    private float mArcRadius;
    private double mCapAdjust;

    private final RectF mArcRect = new RectF();

    private final RectF mClipRect = new RectF();
    private final Path mClipPath = new Path();
    private final Path mDividerClipPath = new Path();

    public CurvedGauge(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public CurvedGauge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public CurvedGauge(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    public CurvedGauge(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        float dp = getResources().getDisplayMetrics().density;

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(54 * dp);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(mPaint.getStrokeWidth());
        mDividerPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w == 0 || h == 0) {
            return;
        }

        float width = w;
        float height = h;

        float y = height * 2f;

        mArcRadius = 140;

        mArcRect.set(mPaint.getStrokeWidth(), mPaint.getStrokeWidth() / 2, width - mPaint.getStrokeWidth(), y - mPaint.getStrokeWidth() / 2);
        mClipRect.set(
                mArcRect.left - mArcRect.width(),
                mArcRect.top - mArcRect.height(),
                mArcRect.right + mArcRect.width(),
                mArcRect.bottom + mArcRect.height()
        );

        // L = deg * r  ==>  deg = L / r.
        float L = mPaint.getStrokeWidth() / 2f;
        float r = (mArcRect.width() + mArcRect.height()) / 4f;
        mCapAdjust = L / r;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float sweep = mArcRadius;
        float start = 270 - sweep / 2f;

        mPaint.setColor(0xffeeeeee);
        canvas.drawArc(mArcRect, start, sweep, false, mPaint);

        drawSegment(1f, Color.YELLOW, start, sweep, canvas);
        drawSegment(0.85f, Color.MAGENTA, start, sweep, canvas);
        drawSegment(0.75f, Color.GREEN, start, sweep, canvas);
        drawSegment(0.6f, Color.BLUE, start, sweep, canvas);
    }

    private void drawSegment(float progress, int color, float start, float sweep, Canvas canvas) {
        float capAdjustDeg = (float) Math.toDegrees(mCapAdjust);

        float clipStart = start - capAdjustDeg - 0.5f;
        float clipSweep = sweep + 2 * capAdjustDeg + 1f;

        float pivotX = mArcRect.centerX();
        float pivotY = mArcRect.centerY();

        mDividerClipPath.reset();
        mDividerClipPath.moveTo(pivotX, pivotY);
        mDividerClipPath.arcTo(mClipRect, clipStart, progress * clipSweep + 0.4f);
        mDividerClipPath.lineTo(pivotX, pivotY);

        mClipPath.reset();
        mClipPath.moveTo(pivotX, pivotY);
        mClipPath.arcTo(mClipRect, clipStart, progress * clipSweep - 0.4f);
        mClipPath.lineTo(pivotX, pivotY);

        canvas.save();
        canvas.clipPath(mDividerClipPath);
        canvas.drawArc(mArcRect, start, sweep, false, mDividerPaint);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mClipPath);
        mPaint.setColor(color);
        canvas.drawArc(mArcRect, start, sweep, false, mPaint);
        canvas.restore();
    }

}
