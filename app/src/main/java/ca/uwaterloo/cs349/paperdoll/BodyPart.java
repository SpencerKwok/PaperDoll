package ca.uwaterloo.cs349.paperdoll;

import java.util.Vector;

public class BodyPart {
    private float localRotation = 0f;
    private float rotationRestriction;
    private Vector<BodyPart> children = new Vector<>();
    private Vector<GlobalRotation> globalRotations = new Vector<>();
    private Coordinate globalPosition = new Coordinate();
    private Coordinate prevGlobalPosition = new Coordinate();
    private Coordinate globalRotatedPosition = new Coordinate();
    private Coordinate prevGlobalRotatedPosition = new Coordinate();
    private Coordinate localPivot = new Coordinate();
    private Coordinate globalPivot = new Coordinate();
    private Coordinate globalPivotLocation = new Coordinate();
    private Coordinate prevGlobalPivotLocation = new Coordinate();
    private Coordinate globalRotatedPivotLocation = new Coordinate();
    private Coordinate displaySize = new Coordinate();
    private Coordinate defaultSize = new Coordinate();
    private Coordinate size = new Coordinate();
    private Coordinate prevSize = new Coordinate();
    private Coordinate defaultScale = new Coordinate();
    private Coordinate prevScale = new Coordinate();
    private Coordinate currScale = new Coordinate();

    BodyPart(float _x, float _y, float _scale_x, float _scale_y, float _rotationRestriction) {
        localPivot.x = _x;
        localPivot.y = _y;
        globalPivot.x = _x;
        globalPivot.y = _y;
        defaultScale.x = _scale_x;
        defaultScale.y = _scale_y;
        prevScale.x = _scale_x;
        prevScale.y = _scale_y;
        currScale.x = _scale_x;
        currScale.y = _scale_y;
        rotationRestriction = _rotationRestriction;
    }

    float getLocalRotation() {
        return (float) (localRotation * 180. / Math.PI);
    }

    Coordinate getGlobalPosition() {
        return globalPosition;
    }

    Coordinate getGlobalRotatedPosition() {
        return globalRotatedPosition;
    }

    Coordinate getSize() {
        return size;
    }

    Coordinate getLocalPivot() {
        return localPivot;
    }

    Coordinate getGlobalPivot() {
        return globalPivot;
    }

    Vector<GlobalRotation> getGlobalRotations() {
        return globalRotations;
    }

    Coordinate getGlobalPivotLocation() {
        return globalPivotLocation;
    }

    Coordinate getGlobalRotatedPivotLocation() {
        return globalRotatedPivotLocation;
    }

    Coordinate getDisplaySize() {
        return displaySize;
    }

    Coordinate getScale() {
        return currScale;
    }

    Coordinate getDefaultSize() {
        return defaultSize;
    }

    float getTotalRotation() {
        float rotation = 0f;
        for (GlobalRotation gr : globalRotations) {
            rotation += gr.bodyPart.getLocalRotation();
        }
        rotation += getLocalRotation();
        return rotation;
    }

    void setDisplaySize(float _x, float _y) {
        displaySize.x = _x;
        displaySize.y = _y;
    }

    void setPosition(float _x, float _y) {
        prevGlobalPosition.x = _x;
        prevGlobalPosition.y = _y;
        prevGlobalRotatedPosition.x = _x;
        prevGlobalRotatedPosition.y = _y;
        globalPosition.x = _x;
        globalPosition.y = _y;
        globalRotatedPosition.x = _x;
        globalRotatedPosition.y = _y;

        Coordinate up = new Coordinate();
        up.x = 0f;
        up.y = -1f;
        Coordinate offset = new Coordinate();
        offset.x = globalRotatedPosition.x - globalPosition.x;
        offset.y = globalRotatedPosition.y - globalPosition.y;
        offset.normalize();
        float angle =  (float) (Math.acos((2f - ((offset.x * offset.x) + (offset.y * offset.y))) / 2f) * 180f / Math.PI);

        double prev_t_x = Math.acos(offset.x);
        double t_x = Math.acos(up.x);
        double t_y = Math.asin(up.y);

        if (t_y < 0.) {
            if (prev_t_x > t_x) {
                localRotation = -angle;
            } else if (prev_t_x < t_x) {
                localRotation = angle;
            } else {
                localRotation = 0f;
            }
        } else {
            if (prev_t_x < t_x) {
                localRotation = -angle;
            } else if (prev_t_x > t_x) {
                localRotation = angle;
            } else {
                localRotation = 0f;
            }
        }
    }

    void setGlobalPivotLocation(float _x, float _y) {
        globalPivotLocation.x = _x;
        globalPivotLocation.y = _y;
        globalRotatedPivotLocation.x = _x;
        globalRotatedPivotLocation.y = _y;
    }

    void setSize(float _x, float _y) {
        defaultSize.x = _x;
        defaultSize.y = _y;
        prevSize.x = _x;
        prevSize.y = _y;
        size.x = _x;
        size.y = _y;
    }

    void scaleBegin() {
        prevScale.x = currScale.x;
        prevScale.y = currScale.y;
        prevSize.x = size.x;
        prevSize.y = size.y;
        prevGlobalPosition.x = globalPosition.x;
        prevGlobalPosition.y = globalPosition.y;
        prevGlobalRotatedPosition.x = globalRotatedPosition.x;
        prevGlobalRotatedPosition.y = globalRotatedPosition.y;
        prevGlobalPivotLocation.x = globalPivotLocation.x;
        prevGlobalPivotLocation.y = globalPivotLocation.y;
    }

    Coordinate scale(float _scale, boolean changeGlobalPosition) {
        float diff = (_scale - 1f);

        // Fix scale
        currScale.y = prevScale.y + prevScale.y * diff;

        // Fix size
        size.y = prevSize.y + prevSize.y * diff;

        if (changeGlobalPosition) {
            // Fix global position
            Coordinate defaultDiff = new Coordinate(0f, size.y - prevSize.y);
            globalPosition.y = prevGlobalPosition.y + (defaultDiff.y / 2f);

            // Fix rotated global position
            float x = globalPosition.x;
            float y = globalPosition.y;
            globalRotatedPosition.x = (x - globalPivotLocation.x)
                    * (float) Math.cos(localRotation)
                    - (y - globalPivotLocation.y)
                    * (float) Math.sin(localRotation)
                    + globalPivotLocation.x;
            globalRotatedPosition.y = (y - globalPivotLocation.y)
                    * (float) Math.cos(localRotation)
                    + (x - globalPivotLocation.x)
                    * (float) Math.sin(localRotation)
                    + globalPivotLocation.y;

            for (GlobalRotation globalRotation : globalRotations) {
                BodyPart bodyPart = globalRotation.bodyPart;
                x = globalRotatedPosition.x;
                y = globalRotatedPosition.y;
                globalRotatedPosition.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                        * (float) Math.cos(globalRotation.rotation)
                        - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                        * (float) Math.sin(globalRotation.rotation)
                        + bodyPart.getGlobalRotatedPivotLocation().x;
                globalRotatedPosition.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                        * (float) Math.cos(globalRotation.rotation)
                        + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                        * (float) Math.sin(globalRotation.rotation)
                        + bodyPart.getGlobalRotatedPivotLocation().y;
            }

            // Set global rotation for pivot point
            globalRotatedPivotLocation.x = globalPivotLocation.x;
            globalRotatedPivotLocation.y = globalPivotLocation.y;
            for (GlobalRotation globalRotation : globalRotations) {
                BodyPart bodyPart = globalRotation.bodyPart;
                x = globalRotatedPivotLocation.x;
                y = globalRotatedPivotLocation.y;
                globalRotatedPivotLocation.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                        * (float) Math.cos(globalRotation.rotation)
                        - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                        * (float) Math.sin(globalRotation.rotation)
                        + bodyPart.getGlobalRotatedPivotLocation().x;
                globalRotatedPivotLocation.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                        * (float) Math.cos(globalRotation.rotation)
                        + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                        * (float) Math.sin(globalRotation.rotation)
                        + bodyPart.getGlobalRotatedPivotLocation().y;
            }
        }

        return (new Coordinate(size.x - prevSize.x, size.y - prevSize.y));
    }

    Coordinate scale(float _scale) {
        return scale(_scale, true);
    }

    void rotate(float _angle) {
        float newLocalRotation = (localRotation + _angle);
        if (rotationRestriction >= 0f && Math.abs(newLocalRotation) > rotationRestriction) {
            return;
        }

        localRotation += _angle;

        float x = globalPosition.x;
        float y = globalPosition.y;
        globalRotatedPosition.x = (x - globalPivotLocation.x)
                * (float) Math.cos(localRotation)
                - (y - globalPivotLocation.y)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.x;
        globalRotatedPosition.y = (y - globalPivotLocation.y)
                * (float) Math.cos(localRotation)
                + (x - globalPivotLocation.x)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.y;

        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            x = globalRotatedPosition.x;
            y = globalRotatedPosition.y;
            globalRotatedPosition.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.cos(globalRotation.rotation)
                    - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().x;
            globalRotatedPosition.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.cos(globalRotation.rotation)
                    + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().y;
        }

        for (BodyPart b : children) {
            b.rotate(_angle, this);
        }
    }

    void rotate(float _rotation, BodyPart _bodyPart) {
        for (GlobalRotation gr : globalRotations) {
            if (gr.bodyPart == _bodyPart) {
                gr.rotation += _rotation;
                break;
            }
        }

        // Set local rotation for position
        float x = globalPosition.x;
        float y = globalPosition.y;
        globalRotatedPosition.x = (x - globalPivotLocation.x)
                * (float) Math.cos(localRotation)
                - (y - globalPivotLocation.y)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.x;
        globalRotatedPosition.y = (y - globalPivotLocation.y)
                * (float) Math.cos(localRotation)
                + (x - globalPivotLocation.x)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.y;

        // Set global rotation for position
        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            x = globalRotatedPosition.x;
            y = globalRotatedPosition.y;
            globalRotatedPosition.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.cos(globalRotation.rotation)
                    - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().x;
            globalRotatedPosition.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.cos(globalRotation.rotation)
                    + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().y;
        }

        // Set global rotation for pivot point
        globalRotatedPivotLocation.x = globalPivotLocation.x;
        globalRotatedPivotLocation.y = globalPivotLocation.y;
        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            x = globalRotatedPivotLocation.x;
            y = globalRotatedPivotLocation.y;
            globalRotatedPivotLocation.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.cos(globalRotation.rotation)
                    - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().x;
            globalRotatedPivotLocation.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.cos(globalRotation.rotation)
                    + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().y;
        }
    }

    void translate(float _dx, float _dy, boolean updateGlobalPosition) {
        if (updateGlobalPosition) {
            globalPosition.x += _dx;
            globalPosition.y += _dy;
        } else {
            globalPosition.x = globalPivotLocation.x ;
            globalPosition.y = globalPivotLocation.y + size.y / 2f;
        }

        globalPivotLocation.x += _dx;
        globalPivotLocation.y += _dy;

        // Set local rotation for position
        float x = globalPosition.x;
        float y = globalPosition.y;
        globalRotatedPosition.x = (x - globalPivotLocation.x)
                * (float) Math.cos(localRotation)
                - (y - globalPivotLocation.y)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.x;
        globalRotatedPosition.y = (y - globalPivotLocation.y)
                * (float) Math.cos(localRotation)
                + (x - globalPivotLocation.x)
                * (float) Math.sin(localRotation)
                + globalPivotLocation.y;

        // Set global rotation for position
        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            x = globalRotatedPosition.x;
            y = globalRotatedPosition.y;
            globalRotatedPosition.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.cos(globalRotation.rotation)
                    - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().x;
            globalRotatedPosition.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.cos(globalRotation.rotation)
                    + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().y;
        }

        // Set global rotation for pivot point
        globalRotatedPivotLocation.x = globalPivotLocation.x;
        globalRotatedPivotLocation.y = globalPivotLocation.y;
        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            x = globalRotatedPivotLocation.x;
            y = globalRotatedPivotLocation.y;
            globalRotatedPivotLocation.x = (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.cos(globalRotation.rotation)
                    - (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().x;
            globalRotatedPivotLocation.y = (y - bodyPart.getGlobalRotatedPivotLocation().y)
                    * (float) Math.cos(globalRotation.rotation)
                    + (x - bodyPart.getGlobalRotatedPivotLocation().x)
                    * (float) Math.sin(globalRotation.rotation)
                    + bodyPart.getGlobalRotatedPivotLocation().y;
        }
    }

    void translate(float _dx, float _dy) {
        translate(_dx, _dy, true);
    }

    void addChild(BodyPart _bodyPart, float _x, float _y) {
        children.add(_bodyPart);
        _bodyPart.addParent(this, _x, _y);
    }

    void addParent(BodyPart _bodyPart, float _x, float _y) {
        GlobalRotation globalRotation = new GlobalRotation();
        globalRotation.bodyPart = _bodyPart;
        globalRotation.offsetMultiplier.x = _x;
        globalRotation.offsetMultiplier.y = _y;
        globalRotations.add(globalRotation);
    }

    boolean isInside(float _x, float _y) {
        float x = _x;
        float y = _y;
        for (GlobalRotation globalRotation : globalRotations) {
            BodyPart bodyPart = globalRotation.bodyPart;
            float oldX = x;
            float oldY = y;
            x = (oldX - bodyPart.getGlobalPivotLocation().x)
                    * (float) Math.cos(-globalRotation.rotation)
                    - (oldY - bodyPart.getGlobalPivotLocation().y)
                    * (float) Math.sin(-globalRotation.rotation)
                    + bodyPart.getGlobalPivotLocation().x;
            y = (oldY - bodyPart.getGlobalPivotLocation().y)
                    * (float) Math.cos(-globalRotation.rotation)
                    + (oldX - bodyPart.getGlobalPivotLocation().x)
                    * (float) Math.sin(-globalRotation.rotation)
                    + bodyPart.getGlobalPivotLocation().y;
        }

        float oldX = x;
        float oldY = y;
        x = (oldX - globalPivotLocation.x)
                * (float) Math.cos(-localRotation)
                - (oldY - globalPivotLocation.y)
                * (float) Math.sin(-localRotation)
                + globalPivotLocation.x;
        y = (oldY - globalPivotLocation.y)
                * (float) Math.cos(-localRotation)
                + (oldX - globalPivotLocation.x)
                * (float) Math.sin(-localRotation)
                + globalPivotLocation.y;

        float diffX = (x - globalPosition.x);
        float diffY = (y - globalPosition.y);
        float rx = (size.x / 2f) + 20f;
        float ry = (size.y / 2f) + 10f;
        return ((((diffX * diffX) / (rx * rx)) +
                ((diffY * diffY) / (ry * ry))) <= 1f);
    }

    void reset() {
        localRotation = 0f;
        for (GlobalRotation globalRotation : globalRotations) {
            globalRotation.rotation = 0f;
        }

        currScale.x = defaultScale.x;
        currScale.y = defaultScale.y;
    }
}