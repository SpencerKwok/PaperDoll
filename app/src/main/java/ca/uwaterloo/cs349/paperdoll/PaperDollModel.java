package ca.uwaterloo.cs349.paperdoll;

import android.util.Log;

import java.util.Observable;

class PaperDollModel extends Observable {
    private Coordinate input = new Coordinate();
    private float rotation = 0f;
    private boolean isScaling = false;

    private BodyPart selectedBodyPart;
    private BodyPart head;
    private BodyPart left_arm;
    private BodyPart left_forearm;
    private BodyPart left_hand;
    private BodyPart right_arm;
    private BodyPart right_forearm;
    private BodyPart right_hand;
    private BodyPart left_thigh;
    private BodyPart left_leg;
    private BodyPart left_foot;
    private BodyPart right_thigh;
    private BodyPart right_leg;
    private BodyPart right_foot;
    private Torso torso;

    private Coordinate prev_size = new Coordinate();
    private Coordinate prev_size2 = new Coordinate();

    PaperDollModel() {
        head = new BodyPart(0.5f,1f,
                0.36085218f,0.36085218f,(float) (50f * Math.PI /180f));
        left_arm = new BodyPart(0.9f,0.1f,
                0.22663091f,0.22663394f,-1f);
        left_forearm = new BodyPart(0.5f,0.5f,
                0.22663091f,0.22663394f,(float) (135f * Math.PI / 180f));
        left_hand = new BodyPart(0.5f,0.25f,
                0.22663091f,0.22663394f,(float) (35f * Math.PI / 180f));
        right_arm = new BodyPart(0.065f,0.065f, 0.22891548f,0.22891548f, -1f);
        right_forearm = new BodyPart(0.5f,0.5f,
                0.22663091f,0.22663394f,(float) (135f * Math.PI / 180f));
        right_hand = new BodyPart(0.5f,0.25f,
                0.22663091f,0.22663394f,(float) (35f * Math.PI / 180f));
        left_thigh = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (90f * Math.PI / 180f));
        left_leg = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (90f * Math.PI / 180f));
        left_foot = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (35f * Math.PI / 180f));
        right_thigh = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (90f * Math.PI / 180f));
        right_leg = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (90f * Math.PI / 180f));
        right_foot = new BodyPart(0.5f,0f,
                0.24663091f,0.23663394f,(float) (35f * Math.PI / 180f));
        torso = new Torso(0.5f,0.5f, 0.26285218f,0.27085218f);

        left_arm.addChild(left_forearm, 2.2f, 4.15f);
        left_arm.addChild(left_hand, 5.1f, 4.45f);
        left_forearm.addChild(left_hand, 0f,0f);

        right_arm.addChild(right_forearm, -3.2f, 4.15f);
        right_arm.addChild(right_hand, -2.1f, 4.45f);
        right_forearm.addChild(right_hand, 0f, 0f);

        left_thigh.addChild(left_leg, 1f, 4.2f);
        left_thigh.addChild(left_foot, 1f, 4.2f);
        left_leg.addChild(left_foot, 0f, 0f);

        right_thigh.addChild(right_leg, 1f, 4.6f);
        right_thigh.addChild(right_foot, 1f, 4.4f);
        right_leg.addChild(right_foot, 0f, 0f);
    }

    BodyPart getHead() {
        return head;
    }

    BodyPart getLeftArm() {
        return left_arm;
    }

    BodyPart getLeftForearm() {
        return left_forearm;
    }

    BodyPart getLeftHand() {
        return left_hand;
    }

    BodyPart getRightArm() {
        return right_arm;
    }

    BodyPart getRightForearm() {
        return right_forearm;
    }

    BodyPart getRightHand() {
        return right_hand;
    }

    BodyPart getLeftThigh() {
        return left_thigh;
    }

    BodyPart getLeftLeg() {
        return left_leg;
    }

    BodyPart getLeftFoot() {
        return left_foot;
    }

    BodyPart getRightThigh() {
        return right_thigh;
    }

    BodyPart getRightLeg() {
        return right_leg;
    }

    BodyPart getRightFoot() {
        return right_foot;
    }

    BodyPart getTorso() {
        return torso;
    }

    BodyPart getSelectedBodyPart() {
        return selectedBodyPart;
    }

    void setScale(float _scale) {
        if (selectedBodyPart == left_leg) {
            Coordinate shift = left_leg.scale(_scale);
            left_foot.translate((shift.x - prev_size.x) * 1.1f, (shift.y - prev_size.y) * 1.1f);
            prev_size = shift;

            setChanged();
            notifyObservers();
        } else if (selectedBodyPart == right_leg) {
            Coordinate shift = right_leg.scale(_scale);
            right_foot.translate((shift.x - prev_size.x) * 1.1f, (shift.y - prev_size.y) * 1.1f);
            prev_size = shift;

            setChanged();
            notifyObservers();
        } else if (selectedBodyPart == left_thigh) {
            Coordinate shift = left_thigh.scale(_scale);
            Coordinate shift2 = left_leg.scale(_scale);

            left_leg.translate((shift.x - prev_size.x) * 1.05f,
                    (shift.y - prev_size.y) * 1.05f, false);
            left_foot.translate((shift2.x - prev_size2.x + shift.x - prev_size.x) * 1.05f,
                    (shift2.y - prev_size2.y + shift.y - prev_size.y) * 1.05f);
            prev_size = shift;
            prev_size2 = shift2;

            setChanged();
            notifyObservers();
        } else if (selectedBodyPart == right_thigh) {
            Coordinate shift = right_thigh.scale(_scale);
            Coordinate shift2 = right_leg.scale(_scale);

            right_leg.translate((shift.x - prev_size2.x) * 1.05f,
                    (shift.y - prev_size.y) * 1.05f, false);
            right_foot.translate((shift2.x - prev_size2.x + shift.x - prev_size.x) * 1.05f,
                    (shift2.y - prev_size2.y + shift.y - prev_size.y) * 1.05f);
            prev_size = shift;
            prev_size2 = shift2;

            setChanged();
            notifyObservers();
        }
    }

    void setInput(float _x, float _y) {
        if (isScaling) {
            return;
        }

        updateRotation(_x, _y);

        if (selectedBodyPart != null) {
            selectedBodyPart.rotate(rotation);

            if (selectedBodyPart == torso) {
                head.translate(_x - input.x, _y - input.y);
                left_arm.translate(_x - input.x,_y - input.y);
                left_forearm.translate(_x - input.x, _y - input.y);
                left_hand.translate(_x - input.x, _y - input.y);
                right_arm.translate(_x - input.x, _y - input.y);
                right_forearm.translate(_x - input.x, _y - input.y);
                right_hand.translate(_x - input.x, _y - input.y);
                left_thigh.translate(_x - input.x, _y - input.y);
                left_leg.translate(_x - input.x, _y - input.y);
                left_foot.translate(_x - input.x, _y - input.y);
                right_thigh.translate(_x - input.x, _y - input.y);
                right_leg.translate(_x - input.x, _y - input.y);
                right_foot.translate(_x - input.x, _y - input.y);
                torso.translate(_x - input.x, _y - input.y);
            }

            setChanged();
            notifyObservers();
        }

        input.x = _x;
        input.y = _y;
    }

    void scaleBegin(float _x, float _y) {
        if (selectedBodyPart != null) {
            selectedBodyPart.scaleBegin();
            left_leg.scaleBegin();
            right_leg.scaleBegin();
            prev_size.x = 0f;
            prev_size.y = 0f;
            prev_size2.x = 0f;
            prev_size2.y = 0f;
            isScaling = true;
        }
    }

    void scaleEnd() {
        selectedBodyPart = null;
        isScaling = false;
    }

    void resetInput(float _x, float _y) {
        input.x = _x;
        input.y = _y;

        if (left_hand.isInside(_x,_y)) {
            selectedBodyPart = left_hand;
        } else if (left_forearm.isInside(_x,_y)) {
            selectedBodyPart = left_forearm;
        } else if (left_arm.isInside(_x,_y)) {
            selectedBodyPart = left_arm;
        } else if (right_hand.isInside(_x,_y)) {
            selectedBodyPart = right_hand;
        } else if (right_forearm.isInside(_x,_y)) {
            selectedBodyPart = right_forearm;
        } else if (right_arm.isInside(_x,_y)) {
            selectedBodyPart = right_arm;
        } else if (left_foot.isInside(_x,_y)) {
            selectedBodyPart = left_foot;
        } else if (left_leg.isInside(_x,_y)) {
            selectedBodyPart = left_leg;
        } else if (left_thigh.isInside(_x,_y)) {
            selectedBodyPart = left_thigh;
        } else if (right_foot.isInside(_x,_y)) {
            selectedBodyPart = right_foot;
        } else if (right_leg.isInside(_x,_y)) {
            selectedBodyPart = right_leg;
        } else if (right_thigh.isInside(_x,_y)) {
            selectedBodyPart = right_thigh;
        } else if (torso.isInside(_x, _y)) {
            selectedBodyPart = torso;
        } else if (head.isInside(_x,_y)) {
            selectedBodyPart = head;
        } else {
            selectedBodyPart = null;
        }
    }

    private void updateRotation(float _x, float _y) {
        if (selectedBodyPart == null) {
            return;
        }

        Coordinate center = selectedBodyPart.getGlobalRotatedPivotLocation();
        Coordinate prev_input_vec = new Coordinate(input.x - center.x, input.y - center.y);
        Coordinate input_vec = new Coordinate(_x - center.x, _y - center.y);

        rotation = (float) (Math.atan2(input_vec.y, input_vec.x) -
                Math.atan2(prev_input_vec.y, prev_input_vec.x));

        //Log.d("DEBUG", "ANGLE: " + Float.toString(rotation));
    }
}
