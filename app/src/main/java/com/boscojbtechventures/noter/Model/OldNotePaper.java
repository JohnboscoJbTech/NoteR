package com.boscojbtechventures.noter.Model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.boscojbtechventures.noter.NoteBook;
import com.boscojbtechventures.noter.R;

import java.util.ArrayList;

/**
 * Created by Johnbosco on 13/02/2017.
 */

public class OldNotePaper extends AppCompatEditText {
    private Paint linePaint;
    private int paperColor;
    private Rect mRect;
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private boolean drawing;

    public static ArrayList<Float> xAxisValues;
    public static ArrayList<Float> yAxisValues;

    public OldNotePaper(final Context context) {
        super(context);
        init();
    }

    public OldNotePaper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OldNotePaper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //public NotePad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //super(context, attrs, defStyleAttr, defStyleRes);
    //}
    @Override
    protected void onDraw(Canvas canvas) {
        int height, currentHeight;
        height = canvas.getHeight();//getMaximumBitmapHeight()or use 1234567890;
        Rect r = mRect;
        int baseline = getLineBounds(0, r);

        canvas.drawColor(paperColor);
        //canvas.drawLine(0, 0, getMeasuredWidth(), 0, linePaint);
        for (currentHeight = baseline; currentHeight < height; currentHeight += getLineHeight()) {
            canvas.drawLine(r.left, currentHeight, r.right, currentHeight, linePaint);
        }
        if (drawing) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        }
        //canvas.translate(margin, 0);
        super.onDraw(canvas);
        //canvas.restore();
    }

    public void init() {
        mRect = new Rect();
        Resources myResources = getResources();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(myResources.getColor(R.color.notepad_lines));
        paperColor = myResources.getColor(R.color.notepad_paper);

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
    }

    @Override
    public void onMeasure(int widthMeasuredSpec, int heihtMeasuresSpec) {
        super.onMeasure(widthMeasuredSpec, heihtMeasuresSpec);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (NoteBook.inDrawingMode) {
            drawing = true;
            float touchX = event.getX();
            float touchY = event.getY();
            xAxisValues.add(touchX);
            yAxisValues.add(touchY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        else return super.onTouchEvent(event);
    }
}
