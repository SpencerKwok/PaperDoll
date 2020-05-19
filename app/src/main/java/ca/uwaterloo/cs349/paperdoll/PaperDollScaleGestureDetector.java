package ca.uwaterloo.cs349.paperdoll;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

class PaperDollScaleGestureDetector extends ScaleGestureDetector {
    private PaperDollOnScaleGestureListener listener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        listener.setPosition(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    PaperDollScaleGestureDetector(Context _context, PaperDollOnScaleGestureListener _listener) {
        super(_context, _listener);
        listener = _listener;
    }
}
