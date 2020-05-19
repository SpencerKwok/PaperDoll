package ca.uwaterloo.cs349.paperdoll;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class DebugCircle {
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float x, y, radius;

    DebugCircle(float _x, float _y, float _radius, int color) {
        x = _x;
        y = _y;
        radius = _radius;
        paint.setColor(color);
    }

    void draw(Canvas _canvas) {
        Matrix oldMatrix = _canvas.getMatrix();
        _canvas.setMatrix(matrix);
        _canvas.drawCircle(x, y, radius, paint);
        _canvas.setMatrix(oldMatrix);
    }
}