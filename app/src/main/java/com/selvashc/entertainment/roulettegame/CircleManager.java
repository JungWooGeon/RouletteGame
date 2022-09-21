package com.selvashc.entertainment.roulettegame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * 룰렛을 그리도록 관리하는 Manager (View)
 * 생성자에서 받은 strings 를 반영하여 룰렛을 생성 (string 배열과 final 로 정의한 COLORS 를 바탕으로 원판 그리기)
 * COLORS 배열에서 색 변경 가능
 */
@SuppressLint("ViewConstructor")
class CircleManager extends View {

    private static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.GRAY};
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int num;
    private RelativeLayout layoutRoulette;
    private List<String> rouletteList;

    public CircleManager(Context context, int num, RelativeLayout layout, List<String> list) {
        super(context);
        this.num = num;
        layoutRoulette = layout;
        rouletteList = list;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int CIRCLE_ANGLE = 360;
        final int TEXT_SIZE = 20;

        int width = layoutRoulette.getWidth();
        int height = layoutRoulette.getHeight();
        int sweepAngle = CIRCLE_ANGLE / num;

        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(0, 0, width, height);
        @SuppressLint("DrawAllocation")
        Rect rect = new Rect(0, 0, width, height);

        int centerX = (rect.left + rect.right) / 2;
        int centerY = (rect.top + rect.bottom) / 2;
        int radius = (rect.right - rect.left) / 2;

        int temp = 0;

        for (int i = 0; i < num; i++) {
            paint.setColor(COLORS[i]);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawArc(rectF, temp, sweepAngle, true, paint);

            float medianAngle = (temp + (sweepAngle / 2f)) * (float) Math.PI / 180f;

            paint.setColor(Color.BLACK);
            paint.setTextSize(TEXT_SIZE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            float arcCenterX = (float) (centerX + (radius * Math.cos(medianAngle))); // Arc's center X
            float arcCenterY = (float) (centerY + (radius * Math.sin(medianAngle))); // Arc's center Y

            // put text at middle of Arc's center point and Circle's center point
            float textX = (centerX + arcCenterX) / 2;
            float textY = (centerY + arcCenterY) / 2;

            canvas.drawText(rouletteList.get(i), textX, textY, paint);
            temp += sweepAngle;
        }
    }
}
