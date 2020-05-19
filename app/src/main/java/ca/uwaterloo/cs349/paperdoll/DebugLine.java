package ca.uwaterloo.cs349.paperdoll;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

class DebugLine {
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float x, y, x2, y2;

    DebugLine(float _x, float _y, float _x2,
              float _y2, int _color, float _width) {
        x = _x;
        y = _y;
        x2 = _x2;
        y2 = _y2;
        paint.setColor(_color);
        paint.setStrokeWidth(_width);
    }

    void draw(Canvas _canvas) {
        Matrix oldMatrix = _canvas.getMatrix();
        _canvas.setMatrix(matrix);
        _canvas.drawLine(x, y, x2, y2, paint);
        _canvas.setMatrix(oldMatrix);
    }
}
