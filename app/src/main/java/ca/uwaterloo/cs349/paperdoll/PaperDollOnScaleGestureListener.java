package ca.uwaterloo.cs349.paperdoll;

import android.util.Log;
import android.view.ScaleGestureDetector;

public class PaperDollOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    PaperDollModel model;
    private Coordinate pos = new Coordinate(0f, 0f);

    PaperDollOnScaleGestureListener(PaperDollModel _model) {
        model = _model;
    }

    public void setPosition(float _x, float _y) {
        pos.x = _x;
        pos.y = _y;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        model.resetInput(detector.getFocusX(), detector.getFocusY());
        model.scaleEnd();
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        model.resetInput(detector.getFocusX(), detector.getFocusY());
        model.scaleBegin(detector.getFocusX(), detector.getFocusY());
        return true;
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        BodyPart bodyPart = model.getSelectedBodyPart();
        if (bodyPart == null) {
            return false;
        }

        // rotate focus
        Coordinate focus = new Coordinate(detector.getFocusX(), detector.getFocusY());
        float x = focus.x;
        float y = focus.y;
        focus.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                * (float) Math.cos(-bodyPart.getTotalRotation() * Math.PI / 180f)
                - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                * (float) Math.sin(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + bodyPart.getGlobalRotatedPivotLocation().x;
        focus.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                * (float) Math.cos(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                * (float) Math.sin(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + bodyPart.getGlobalRotatedPivotLocation().y;

        // rotate position
        Coordinate _pos = new Coordinate(pos.x, pos.y);
        x = _pos.x;
        y = _pos.y;
        _pos.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                * (float) Math.cos(-bodyPart.getTotalRotation() * Math.PI / 180f)
                - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                * (float) Math.sin(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + bodyPart.getGlobalRotatedPivotLocation().x;
        _pos.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                * (float) Math.cos(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                * (float) Math.sin(-bodyPart.getTotalRotation() * Math.PI / 180f)
                + bodyPart.getGlobalRotatedPivotLocation().y;


        Coordinate angle = new Coordinate(Math.abs(focus.x - _pos.x), Math.abs(focus.y - _pos.y));
        angle.normalize();
        float diff = detector.getScaleFactor() - 1f;

        model.setScale(1f + diff * angle.y);
        return false;
    }
}
