package com.add.appxm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class TempView extends View implements Runnable {
    private int offTop = 20, offRight = 20, offBottom = 20, offLeft = 20;
    private int textSize;
    private int tempSize, labelSize;

    public TempView(Context context) {
        super(context);
        initPaint();
    }

    public TempView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initData(context, attrs);
        initPaint();
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData(context, attrs);
        initPaint();
    }

    /*     <attr name="maxR" format="integer"></attr>
            <attr name="minR" format="integer"></attr>
            <attr name="textSize" format="integer"></attr>
            <attr name="textColor" format="color"></attr>
            <attr name="besideColor" format="color"></attr>
            <attr name="insideColor" format="color"></attr>
            <attr name="centerColor" format="color"></attr>
            <attr name="max" format="integer"></attr>
            <attr name="min" format="integer"></attr>*/
    private Color c;
    private int textColor, besideColor, insideColor, centerColor, max, min, stoke,besideStoke,centerWidth,scaleTextSize;
    private Paint centerPaint, besidePaint, insidePaint, textPaint, scaleRed, scaleWhite, scaleBlack, scaleText;
    private int maxR, minR;
    private final String url = "http://schemas.android.com/apk/res-auto";
    private final int MAX = 16777215;

    private void initData(Context context, AttributeSet attrs) {
        Log.i("TAG", "initData");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TempView);

        offTop = typedArray.getInt(R.styleable.TempView_offtop, 0);
        Log.i("TAG", "offTop:" + offTop);
        offRight = typedArray.getInt(R.styleable.TempView_offright, 0);
        offBottom = typedArray.getInt(R.styleable.TempView_offbottom, 0);
        offLeft = typedArray.getInt(R.styleable.TempView_offleft, 0);
        Log.i("TAG", "textSize:" + textSize);
        textColor = typedArray.getColor(R.styleable.TempView_textColor, 0xffffff);
        Log.i("TAG", "textColor:" + textColor);
        //  if(textColor<0)textColor = MAX+textColor;
        textSize = typedArray.getInt(R.styleable.TempView_textSize, 12);
        besideColor = typedArray.getColor(R.styleable.TempView_besideColor, 0xffffff);
        //   if(besideColor<0)besideColor = MAX+besideColor;
        Log.i("TAG", "besideColor:" + besideColor);
        insideColor = typedArray.getColor(R.styleable.TempView_insideColor, 0xff0000);
        //if(insideColor<0)insideColor = MAX+insideColor;
        Log.i("TAG", "insideColor:" + insideColor);
        centerColor = typedArray.getColor(R.styleable.TempView_centerColor, 0xff0000);
        // if(centerColor<0)centerColor = MAX+centerColor;
        stoke = typedArray.getInteger(R.styleable.TempView_stoke,5);
        besideStoke = typedArray.getInteger(R.styleable.TempView_besideStoke,3);
        centerWidth = typedArray.getInteger(R.styleable.TempView_centerWidth,20);
        scaleTextSize = typedArray.getInteger(R.styleable.TempView_scaleTextSize,15);
        typedArray.recycle();
    }

    private void initPaint() {
        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        centerPaint.setAntiAlias(true);

        insidePaint = new Paint();
        insidePaint.setColor(insideColor);
        insidePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        insidePaint.setAntiAlias(true);

        besidePaint = new Paint();
        besidePaint.setColor(besideColor);
        besidePaint.setStyle(Paint.Style.STROKE);
        besidePaint.setStrokeWidth(stoke);
        besidePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        scaleBlack = new Paint();
        scaleBlack.setColor(Color.BLACK);
        scaleBlack.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleBlack.setStrokeWidth(besideStoke);

        scaleRed = new Paint();
        scaleRed.setColor(Color.RED);
        scaleRed.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleRed.setStrokeWidth(besideStoke);

        scaleWhite = new Paint();
        scaleWhite.setColor(Color.WHITE);
        scaleWhite.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleWhite.setStrokeWidth(besideStoke);

        scaleText = new Paint();
      //  scaleText.setStrokeWidth(2);
        scaleText.setTextSize(scaleTextSize);
        scaleText.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxR = (MeasureSpec.getSize(widthMeasureSpec) - offLeft - offRight) / 2;
        minR = maxR * 4 / 5;
    }

    float minc = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(offLeft,
                getMeasuredHeight() - offBottom - maxR * 2,
                offLeft + maxR * 2,
                getMeasuredHeight() - offBottom,
                -60,
                300,
                false,
                besidePaint);
        canvas.drawArc(offLeft + maxR / 2,
                offTop,
                offLeft + maxR * 3 / 2,
                minR + offTop,
                0,
                -180,
                false,
                besidePaint);
        float height1 = (float) Math.sqrt(maxR * maxR * 3 / 4);
        float[] lines = {offLeft + maxR / 2, offTop + maxR / 4, offLeft + maxR / 2, getMeasuredHeight() - offBottom - maxR - height1, offLeft + maxR * 3 / 2, offTop + maxR / 4, offLeft + maxR * 3 / 2, getMeasuredHeight() - offBottom - maxR - height1};
        canvas.drawLines(lines, besidePaint);
        canvas.drawCircle(offLeft + maxR, getMeasuredHeight() - offBottom - maxR, minR, centerPaint);
        int minleft = offLeft + maxR - minR / 2;
        int mintRight = offLeft + maxR + minR / 2;
        int minheight = (int) (getMeasuredHeight() - offBottom - maxR - Math.sqrt(minR * minR * 3 / 4));
       float scaleLength = getMeasuredHeight() - offBottom - offTop - maxR - height1 - minR;
        //每个像素点代表的温度值
        float scaleSize = 60 / scaleLength;
        float sz = scaleLength / 30;
        Log.i("TAG", "scaleSize" + scaleSize);
        float scalex = offLeft + maxR * 3 / 2;
        float scaley = getMeasuredHeight() - offBottom - maxR - height1;
        float[] diwenxian = {scalex, scaley, scalex + 20, scaley,
                scalex, scaley - sz, scalex + 20, scaley - sz,
                scalex, scaley - sz * 2, scalex + 20, scaley - sz * 2,
                scalex, scaley - sz * 3, scalex + 20, scaley - sz * 3,
                scalex, scaley - sz * 4, scalex + 20, scaley - sz * 4,
                scalex, scaley - sz * 5, scalex + 20, scaley - sz * 5,
                scalex, scaley - sz * 6, scalex + 20, scaley - sz * 6,
                scalex, scaley - sz * 7, scalex + 20, scaley - sz * 7,};
        int count = 0;
        while (count < 31) {
            if (count < 6) {
                if (count % 5 != 0) {
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 12, scaley - sz * count, scaleBlack);
                } else
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 20, scaley - sz * count, scaleBlack);
            } else if (count < 23) {
                if (count % 5 != 0) {
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 12, scaley - sz * count, scaleWhite);
                } else
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 20, scaley - sz * count, scaleWhite);
            } else {
                if (count % 5 != 0) {
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 12, scaley - sz * count, scaleRed);
                } else
                    canvas.drawLine(scalex, scaley - sz * count, scalex + 20, scaley - sz * count, scaleRed);
            }
            Rect rect2 = new Rect();
            scaleText.getTextBounds("1",0,1,rect2);
            switch (count) {
                case 0:
                    scaleText.setColor(Color.BLACK);
                    canvas.drawText("-10",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 5:
                    scaleText.setColor(Color.BLACK);
                    canvas.drawText("0",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 10:
                    scaleText.setColor(Color.WHITE);
                    canvas.drawText("10",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 15:
                    scaleText.setColor(Color.WHITE);
                    canvas.drawText("20",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 20:
                    scaleText.setColor(Color.WHITE);
                    canvas.drawText("30",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 25:
                    scaleText.setColor(Color.RED);
                    canvas.drawText("40",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
                case 30:
                    scaleText.setColor(Color.RED);
                    canvas.drawText("50",scalex + 20, scaley - sz * count+rect2.height()/2,scaleText);
                    break;
            }
           float t = getMeasuredHeight() - offBottom - maxR - height1 - (minc+10)/scaleSize;
            if (t < offTop + minR) {
                t = offTop + minR;
            } else if (t > getMeasuredHeight() - offBottom - maxR - height1) {
                t =  getMeasuredHeight() - offBottom - maxR - height1;
            }
            Rect rect = new Rect(offLeft+maxR-centerWidth/2, (int)t, offLeft+maxR+centerWidth/2, minheight);

            canvas.drawRect(rect, centerPaint);
           Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bb);
           // Bitmap bitmap =(Bitmap) getResources().getDrawable(R.drawable.bb);

            float bw = bitmap.getWidth();
            float bh = bitmap.getHeight();
            float bb = maxR*2/bw;
            Log.i("TAG", "bw:" + bw);
            Log.i("TAG", "bh:" + bh);
            Log.i("TAG", "bb:" + bb);
            Rect rectt = new Rect(0,0,(int)bw,83);
            Rect recttt = new Rect(offLeft-5,(int)(t-bh*bb/2)-1,offLeft+maxR*2,(int)(t+bh*bb/2)-1);
           // if(bitmap!=null)
            canvas.drawBitmap(bitmap,rectt,recttt,null);
            String text = minc + "℃";
            Rect rect1 = new Rect();
            besidePaint.getTextBounds(text, 0, text.length(), rect1);
            Log.i("TAG", "圆心:" +(offLeft + maxR));
            Log.i("TAG", "字宽:" + rect1.width());
            Log.i("TAG", "字宽:" + rect1.width()/2);
            canvas.drawText(text, offLeft + maxR - rect1.width()*2/3, getMeasuredHeight() - offBottom - maxR + rect1.height() / 2, textPaint);


            //getRight()
            count++;
        }

    }

    public int order = 600;

    @Override
    public void run() {

        while (minc == order) {
            Log.i("TAG", "run");
            if (minc > order) {
                minc--;
                postInvalidate();
            } else if (minc < order) {
                minc++;
                postInvalidate();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMinc(float id) {
        minc = id;
        postInvalidate();
    }
}
