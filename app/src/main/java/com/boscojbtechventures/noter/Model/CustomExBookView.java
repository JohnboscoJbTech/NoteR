package com.boscojbtechventures.noter.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;
import android.view.MotionEvent;

import com.boscojbtechventures.noter.NoteBook;
import com.boscojbtechventures.noter.R;

import java.util.ArrayList;

/**
 * Created by Johnbosco on 27/09/2017.
 */

public class CustomExBookView extends AppCompatEditText {

    private Paint horizontalLinePaint;
    private Paint marginPaint;
    private int marginColor;
    private int paperColor;//only draw this when you want to change the color of the paper

    //drawing mode inside of this views.
    private Path drawingPath;
    private Paint drawingCanvasPaint;
    private Paint drawingPaint;
    private int drawingColor;
    private Canvas drawingCanvas;
    private Bitmap drawingBitmap;

    //mode
    private boolean drawing;

    //path values
    public static ArrayList<Path> drawingPaths;
    public static ArrayList<Path> savedPaths;

    public CustomExBookView(Context context) {
        super(context);
        setLineSpacing(35,0);
        setFitsSystemWindows(true);
        initialize();
    }

    public CustomExBookView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFitsSystemWindows(true);
        setLineSpacing(35,0);
        initialize();
    }

    public CustomExBookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLineSpacing(35,0);
        setFitsSystemWindows(true);
        initialize();
    }

    private void initialize() {
        /*
         * Initialize editing mode variables
         * */
        horizontalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        horizontalLinePaint.setColor(getResources().getColor(R.color.colorPrimary));
        paperColor = Color.WHITE;
        marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        marginColor = Color.RED;
        marginPaint.setColor(marginColor);
        /*
        * Initialize drawing mode variables
        * */
        drawingPath = new Path();
        drawingCanvasPaint = new Paint(Paint.DITHER_FLAG);
        drawingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawingColor = 0xFF660000;
        drawingPaint.setColor(drawingColor);
        drawingPaint.setStrokeWidth(5);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);

        drawingPaths = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*
        * Draw paper color
        * */
        //canvas.drawColor(paperColor);
        /*
         * Draw lines in the paper
         * */
        int lineHeight;
        int marginWidth = 70;
        int viewHeight = canvas.getHeight();
        for (lineHeight = 0; lineHeight < viewHeight; lineHeight += 35) {
            canvas.drawLine(0, lineHeight, canvas.getWidth(), lineHeight, horizontalLinePaint);
        }
        /*
         * Adds a margin to the paper
         * */
        //canvas.drawLine(marginWidth, 0, marginWidth, canvas.getHeight(), marginPaint);
        //canvas.translate(70, 0);

        if (drawing) {
            canvas.drawBitmap(drawingBitmap, 0, 0, drawingCanvasPaint);
            canvas.drawPath(drawingPath, drawingPaint);
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(drawingBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (NoteBook.inDrawingMode) {
            drawing = true;
            float touchX = event.getX();
            float touchY = event.getY();
            /*xAxisValues.add(touchX);
            yAxisValues.add(touchY);*/
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawingPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawingPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawingCanvas.drawPath(drawingPath, drawingPaint);
                    drawingPaths.add(new Path(drawingPath));
                    drawingPath.reset();
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
