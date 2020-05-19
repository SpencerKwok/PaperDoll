package ca.uwaterloo.cs349.paperdoll;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.opengl.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class PaperDollView extends View implements Observer{
    PaperDollModel model;

    ImageView head;
    ImageView left_arm;
    ImageView left_forearm;
    ImageView left_hand;
    ImageView right_arm;
    ImageView right_forearm;
    ImageView right_hand;
    ImageView left_thigh;
    ImageView left_leg;
    ImageView left_foot;
    ImageView right_thigh;
    ImageView right_leg;
    ImageView right_foot;
    ImageView torso;

    ScaleGestureDetector scale_detector;
    Button about;
    Button reset;
    TextView about_popup;
    PaperDollView me;

    DebugCircle circle = new DebugCircle(0f, 0f, 15f, Color.BLUE);
    DebugCircle circle2 = new DebugCircle(0f, 0f, 15f, Color.BLUE);
    DebugCircle circle3 = new DebugCircle(0f, 0f, 15f, Color.BLUE);

    public PaperDollView(final Activity _activity, PaperDollModel _model) {
        super(_activity);
        me = this;

        model = _model;
        model.addObserver(this);

        head = _activity.findViewById(R.id.head);
        head.setScaleType(ImageView.ScaleType.MATRIX);
        torso = _activity.findViewById(R.id.torso);
        torso.setScaleType(ImageView.ScaleType.MATRIX);
        left_arm = _activity.findViewById(R.id.left_arm);
        left_arm.setScaleType(ImageView.ScaleType.MATRIX);
        left_forearm = _activity.findViewById(R.id.left_forearm);
        left_forearm.setScaleType(ImageView.ScaleType.MATRIX);
        left_hand = _activity.findViewById(R.id.left_hand);
        left_hand.setScaleType(ImageView.ScaleType.MATRIX);
        right_arm = _activity.findViewById(R.id.right_arm);
        right_arm.setScaleType(ImageView.ScaleType.MATRIX);
        right_forearm = _activity.findViewById(R.id.right_forearm);
        right_forearm.setScaleType(ImageView.ScaleType.MATRIX);
        right_hand = _activity.findViewById(R.id.right_hand);
        right_hand.setScaleType(ImageView.ScaleType.MATRIX);
        left_thigh = _activity.findViewById(R.id.left_thigh);
        left_thigh.setScaleType(ImageView.ScaleType.MATRIX);
        left_leg = _activity.findViewById(R.id.left_leg);
        left_leg.setScaleType(ImageView.ScaleType.MATRIX);
        left_foot = _activity.findViewById(R.id.left_foot);
        left_foot.setScaleType(ImageView.ScaleType.MATRIX);
        right_thigh = _activity.findViewById(R.id.right_thigh);
        right_thigh.setScaleType(ImageView.ScaleType.MATRIX);
        right_leg = _activity.findViewById(R.id.right_leg);
        right_leg.setScaleType(ImageView.ScaleType.MATRIX);
        right_foot = _activity.findViewById(R.id.right_foot);
        right_foot.setScaleType(ImageView.ScaleType.MATRIX);

        scale_detector = new PaperDollScaleGestureDetector(_activity, new PaperDollOnScaleGestureListener(model));

        about = _activity.findViewById(R.id.about);
        about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the about_popup view from the XML
                LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(LAYOUT_INFLATER_SERVICE);
                View aboutPopup = inflater.inflate(R.layout.about_popup, null);

                // create the popup window using the aboutPopup
                final PopupWindow popupWindow =
                        new PopupWindow(aboutPopup,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                true);

                // center it
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                aboutPopup.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        reset = _activity.findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                me.resetDoll(me.getWidth(), me.getHeight());
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Detect scaling
        scale_detector.onTouchEvent(event);

        if (!scale_detector.isInProgress()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                {
                    // Reset user input
                    model.resetInput(event.getX(), event.getY());
                }break;
                case MotionEvent.ACTION_MOVE:
                {
                    // Update user input
                    model.setInput(event.getX(), event.getY());
                }break;
                case MotionEvent.ACTION_UP:
                {
                    // Reset user input
                    model.resetInput(event.getX(), event.getY());
                }break;
                default:
                {
                    // Empty
                }break;
            }
        }

        // Want to continue tracking touch as
        // user moves finger around
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        /*
        circle.draw(canvas);
        circle2.draw(canvas);
        circle3.draw(canvas);
        */
        super.onDraw(canvas);
    }

    @Override
    public void update(Observable var1, Object var2) {
        float values[] = new float[9];
        {
            BodyPart headData = model.getHead();
            head.getImageMatrix().getValues(values);
            float scaleX = (float) Math.sqrt(values[0] * values[0] + values[3] * values[3]);
            float scaleY = (float) Math.sqrt(values[1] * values[1] + values[4] * values[4]);
            float headPivotX = headData.getLocalPivot().x * head.getDrawable().getBounds().width();
            float headPivotY = headData.getLocalPivot().y * head.getDrawable().getBounds().height();
            float headRotation = model.getHead().getLocalRotation();
            Matrix headMatrix = new Matrix();
            headMatrix.postRotate(headRotation, headPivotX, headPivotY);
            headMatrix.postScale(scaleX, scaleY);
            head.setImageMatrix(headMatrix);
            head.setPadding((int) (headData.getGlobalPosition().x - headData.getSize().x / 2f),
                    (int) (headData.getGlobalPosition().y - headData.getSize().y / 2f), 0, 0);
        }

        BodyPart leftArmData = model.getLeftArm();
        {
            left_arm.getImageMatrix().getValues(values);
            float scaleX = (float) Math.sqrt(values[0] * values[0] + values[3] * values[3]);
            float scaleY = (float) Math.sqrt(values[1] * values[1] + values[4] * values[4]);
            float leftArmPivotX = leftArmData.getLocalPivot().x * left_arm.getDrawable().getBounds().width();
            float leftArmPivotY = leftArmData.getLocalPivot().y * left_arm.getDrawable().getBounds().height();
            float leftArmRotation = model.getLeftArm().getLocalRotation();
            Matrix leftArmMatrix = new Matrix();
            leftArmMatrix.postRotate(leftArmRotation, leftArmPivotX, leftArmPivotY);
            leftArmMatrix.postScale(scaleX, scaleY);
            left_arm.setImageMatrix(leftArmMatrix);
            left_arm.setPadding((int) (leftArmData.getGlobalPosition().x - leftArmData.getSize().x / 2f),
                    (int) (leftArmData.getGlobalPosition().y - leftArmData.getSize().y / 2f), 0, 0);
        }

        BodyPart leftForearmData = model.getLeftForearm();
        {
            // Let's get the matrix of the left arm
            float leftArmValues[] = new float[9];
            left_arm.getImageMatrix().getValues(leftArmValues);

            // Let's reset the translation and scale
            leftArmValues[2] = 0f;
            leftArmValues[5] = 0f;
            leftArmValues[0] /= leftArmData.getScale().x;
            leftArmValues[1] /= leftArmData.getScale().x;
            leftArmValues[3] /= leftArmData.getScale().y;
            leftArmValues[4] /= leftArmData.getScale().y;

            // Set initial rotation to the left arm matrix
            Matrix leftForearmMatrix = new Matrix();
            leftForearmMatrix.setValues(leftArmValues);

            // Let's rotate the forearm using our local rotation
            Coordinate defaultPivot = new Coordinate(leftForearmData.getLocalPivot().x * left_forearm.getDrawable().getBounds().width(),
                    leftForearmData.getLocalPivot().y * left_forearm.getDrawable().getBounds().height());
            Coordinate newPivot = new Coordinate(leftArmValues[0] * defaultPivot.x + leftArmValues[1] * defaultPivot.y,
                    leftArmValues[3] * defaultPivot.x + leftArmValues[4] * defaultPivot.y);
            float leftForearmRotation = leftForearmData.getLocalRotation();
            leftForearmMatrix.postRotate(leftForearmRotation, newPivot.x, newPivot.y);

            // Let's scale the forearm
            Coordinate scale = leftForearmData.getScale();
            leftForearmMatrix.postScale(scale.x, scale.y);

            // Set matrix to hand
            left_forearm.setImageMatrix(leftForearmMatrix);

            // Rotate offset of new position
            Coordinate defaultPos = new Coordinate(-leftForearmData.getSize().x / 2f, -leftForearmData.getSize().y / 2f);
            Coordinate newPos = new Coordinate(leftArmValues[0] * defaultPos.x + leftArmValues[1] * defaultPos.y,
                    leftArmValues[3] * defaultPos.x + leftArmValues[4] * defaultPos.y);

            //left_forearm.setPadding(0,0,0,0);
            // Set new position of left hand
            left_forearm.setPadding((int)(leftForearmData.getGlobalRotatedPosition().x + newPos.x),
                    (int)(leftForearmData.getGlobalRotatedPosition().y + newPos.y), 0, 0);
        }

        BodyPart leftHandData = model.getLeftHand();
        {
            // Let's get the matrix of the left forearm
            float leftForearmValues[] = new float[9];
            left_forearm.getImageMatrix().getValues(leftForearmValues);

            // Let's reset the translation and scale
            leftForearmValues[2] = 0f;
            leftForearmValues[5] = 0f;
            leftForearmValues[0] /= leftForearmData.getScale().x;
            leftForearmValues[1] /= leftForearmData.getScale().x;
            leftForearmValues[3] /= leftForearmData.getScale().y;
            leftForearmValues[4] /= leftForearmData.getScale().y;

            // Set initial rotation to the left hand matrix
            Matrix leftHandMatrix = new Matrix();
            leftHandMatrix.setValues(leftForearmValues);

            // Let's rotate the hand using our local rotation
            Coordinate defaultPivot = new Coordinate(leftHandData.getLocalPivot().x * left_forearm.getDrawable().getBounds().width(),
                    leftHandData.getLocalPivot().y * left_forearm.getDrawable().getBounds().height());
            Coordinate newPivot = new Coordinate(leftForearmValues[0] * defaultPivot.x + leftForearmValues[1] * defaultPivot.y,
                    leftForearmValues[3] * defaultPivot.x + leftForearmValues[4] * defaultPivot.y);
            float leftHandRotation = leftHandData.getLocalRotation();
            leftHandMatrix.postRotate(leftHandRotation, newPivot.x, newPivot.y);

            // Let's scale the hand
            Coordinate scale = leftHandData.getScale();
            leftHandMatrix.postScale(scale.x, scale.y);

            // Set matrix to hand
            left_hand.setImageMatrix(leftHandMatrix);

            // Rotate offset of new position
            Coordinate defaultPos = new Coordinate(-leftHandData.getSize().x / 2f, -leftHandData.getSize().y / 2f);
            Coordinate newPos = new Coordinate(leftForearmValues[0] * defaultPos.x + leftForearmValues[1] * defaultPos.y,
                    leftForearmValues[3] * defaultPos.x + leftForearmValues[4] * defaultPos.y);

            // Set new position of left hand
            left_hand.setPadding((int)(leftHandData.getGlobalRotatedPosition().x + newPos.x),
                    (int)(leftHandData.getGlobalRotatedPosition().y + newPos.y), 0, 0);
        }

        BodyPart rightArmData = model.getRightArm();
        {
            right_arm.getImageMatrix().getValues(values);
            float scaleX = (float) Math.sqrt(values[0] * values[0] + values[3] * values[3]);
            float scaleY = (float) Math.sqrt(values[1] * values[1] + values[4] * values[4]);
            float rightArmPivotX = rightArmData.getLocalPivot().x * right_arm.getDrawable().getBounds().width();
            float rightArmPivotY = rightArmData.getLocalPivot().y * right_arm.getDrawable().getBounds().height();
            float rightArmRotation = model.getRightArm().getLocalRotation();
            Matrix rightArmMatrix = new Matrix();
            rightArmMatrix.postRotate(rightArmRotation, rightArmPivotX, rightArmPivotY);
            rightArmMatrix.postScale(scaleX, scaleY);
            right_arm.setImageMatrix(rightArmMatrix);
            right_arm.setPadding((int) (rightArmData.getGlobalPosition().x - rightArmData.getSize().x / 2f),
                    (int) (rightArmData.getGlobalPosition().y - rightArmData.getSize().y / 2f), 0, 0);
        }

        BodyPart rightForearmData = model.getRightForearm();
        {
            // Let's get the matrix of the right arm
            float rightArmValues[] = new float[9];
            right_arm.getImageMatrix().getValues(rightArmValues);

            // Let's reset the translation and scale
            rightArmValues[2] = 0f;
            rightArmValues[5] = 0f;
            rightArmValues[0] /= rightArmData.getScale().x;
            rightArmValues[1] /= rightArmData.getScale().x;
            rightArmValues[3] /= rightArmData.getScale().y;
            rightArmValues[4] /= rightArmData.getScale().y;

            // Set initial rotation to the right arm matrix
            Matrix rightForearmMatrix = new Matrix();
            rightForearmMatrix.setValues(rightArmValues);

            // Let's rotate the forearm using our local rotation
            Coordinate defaultPivot = new Coordinate(rightForearmData.getLocalPivot().x * right_forearm.getDrawable().getBounds().width(),
                    rightForearmData.getLocalPivot().y * right_forearm.getDrawable().getBounds().height());
            Coordinate newPivot = new Coordinate(rightArmValues[0] * defaultPivot.x + rightArmValues[1] * defaultPivot.y,
                    rightArmValues[3] * defaultPivot.x + rightArmValues[4] * defaultPivot.y);
            float rightForearmRotation = rightForearmData.getLocalRotation();
            rightForearmMatrix.postRotate(rightForearmRotation, newPivot.x, newPivot.y);

            // Let's scale the forearm
            Coordinate scale = rightForearmData.getScale();
            rightForearmMatrix.postScale(scale.x, scale.y);

            // Set matrix to hand
            right_forearm.setImageMatrix(rightForearmMatrix);

            // Rotate offset of new position
            Coordinate defaultPos = new Coordinate(-rightForearmData.getSize().x / 2f, -rightForearmData.getSize().y / 2f);
            Coordinate newPos = new Coordinate(rightArmValues[0] * defaultPos.x + rightArmValues[1] * defaultPos.y,
                    rightArmValues[3] * defaultPos.x + rightArmValues[4] * defaultPos.y);

            // Set new position of right hand
            right_forearm.setPadding((int)(rightForearmData.getGlobalRotatedPosition().x + newPos.x),
                    (int)(rightForearmData.getGlobalRotatedPosition().y + newPos.y), 0, 0);
        }

        BodyPart rightHandData = model.getRightHand();
        {
            // Let's get the matrix of the right forearm
            float rightForearmValues[] = new float[9];
            right_forearm.getImageMatrix().getValues(rightForearmValues);

            // Let's reset the translation and scale
            rightForearmValues[2] = 0f;
            rightForearmValues[5] = 0f;
            rightForearmValues[0] /= rightForearmData.getScale().x;
            rightForearmValues[1] /= rightForearmData.getScale().x;
            rightForearmValues[3] /= rightForearmData.getScale().y;
            rightForearmValues[4] /= rightForearmData.getScale().y;

            // Set initial rotation to the right hand matrix
            Matrix rightHandMatrix = new Matrix();
            rightHandMatrix.setValues(rightForearmValues);

            // Let's rotate the hand using our local rotation
            Coordinate defaultPivot = new Coordinate(rightHandData.getLocalPivot().x * right_forearm.getDrawable().getBounds().width(),
                    rightHandData.getLocalPivot().y * right_forearm.getDrawable().getBounds().height());
            Coordinate newPivot = new Coordinate(rightForearmValues[0] * defaultPivot.x + rightForearmValues[1] * defaultPivot.y,
                    rightForearmValues[3] * defaultPivot.x + rightForearmValues[4] * defaultPivot.y);
            float rightHandRotation = rightHandData.getLocalRotation();
            rightHandMatrix.postRotate(rightHandRotation, newPivot.x, newPivot.y);

            // Let's scale the hand
            Coordinate scale = rightHandData.getScale();
            rightHandMatrix.postScale(scale.x, scale.y);

            // Set matrix to hand
            right_hand.setImageMatrix(rightHandMatrix);

            // Rotate offset of new position
            Coordinate defaultPos = new Coordinate(-rightHandData.getSize().x / 2f, -rightHandData.getSize().y / 2f);
            Coordinate newPos = new Coordinate(rightForearmValues[0] * defaultPos.x + rightForearmValues[1] * defaultPos.y,
                    rightForearmValues[3] * defaultPos.x + rightForearmValues[4] * defaultPos.y);

            // Set new position of right hand
            right_hand.setPadding((int)(rightHandData.getGlobalRotatedPosition().x + newPos.x),
                    (int)(rightHandData.getGlobalRotatedPosition().y + newPos.y), 0, 0);
        }

        BodyPart leftThighData = model.getLeftThigh();
        {
            Coordinate scale = leftThighData.getScale();
            left_thigh.getImageMatrix().getValues(values);
            float leftThighPivotX = leftThighData.getLocalPivot().x * left_thigh.getDrawable().getBounds().width();
            float leftThighPivotY = leftThighData.getLocalPivot().y * left_thigh.getDrawable().getBounds().height();
            float leftThighRotation = model.getLeftThigh().getLocalRotation();
            Matrix leftThighMatrix = new Matrix();
            leftThighMatrix.postScale(scale.x, scale.y);
            leftThighMatrix.postRotate(leftThighRotation, leftThighPivotX * scale.x, leftThighPivotY * scale.y);
            left_thigh.setImageMatrix(leftThighMatrix);
            left_thigh.setPadding((int)(leftThighData.getGlobalRotatedPivotLocation().x - leftThighData.getDefaultSize().x / 2f),
                    (int)(leftThighData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        BodyPart leftLegData = model.getLeftLeg();
        {
            // Let's get the rotation matrix of the left thigh
            float leftThighValues[] = new float[9];
            left_thigh.getImageMatrix().getValues(leftThighValues);
            leftThighValues[2] = 0f;
            leftThighValues[5] = 0f;
            leftThighValues[0] /= leftThighData.getScale().x;
            leftThighValues[1] /= leftThighData.getScale().x;
            leftThighValues[3] /= leftThighData.getScale().y;
            leftThighValues[4] /= leftThighData.getScale().y;

            // Set initial scale of leg
            Matrix leftLegMatrix = new Matrix();
            Coordinate scale = leftLegData.getScale();
            leftLegMatrix.postScale(scale.x, scale.y);

            // Get rotation data for leg
            Coordinate defaultPivot = new Coordinate(
                    leftLegData.getLocalPivot().x * left_leg.getDrawable().getBounds().width() * scale.x,
                    leftLegData.getLocalPivot().y * left_leg.getDrawable().getBounds().height() * scale.y);

            // Rotate leg
            float leftLegRotation = leftLegData.getLocalRotation();
            leftLegMatrix.postRotate(leftThighData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            leftLegMatrix.postRotate(leftLegRotation, defaultPivot.x, defaultPivot.y);

            // Set matrix to leg
            left_leg.setImageMatrix(leftLegMatrix);

            // Get rotation matrix from left leg
            float leftLegValues[] = new float[9];
            left_leg.getImageMatrix().getValues(leftLegValues);
            leftLegValues[2] = 0f;
            leftLegValues[5] = 0f;
            leftLegValues[0] /= leftLegData.getScale().x;
            leftLegValues[1] /= leftLegData.getScale().x;
            leftLegValues[3] /= leftLegData.getScale().y;
            leftLegValues[4] /= leftLegData.getScale().y;

            // Set new position
            // Dot product
            Coordinate v1 = new Coordinate(leftLegData.getGlobalPosition().x - leftLegData.getGlobalPivotLocation().x,
                    leftLegData.getGlobalPosition().y - leftLegData.getGlobalPivotLocation().y);
            Coordinate v2 = new Coordinate(leftLegData.getGlobalRotatedPosition().x - leftLegData.getGlobalRotatedPivotLocation().x,
                    leftLegData.getGlobalRotatedPosition().y - leftLegData.getGlobalRotatedPivotLocation().y);
            v1.normalize();
            v2.normalize();
            Coordinate v3 = new Coordinate((v1.x * v2.x) + (v1.y * v2.y),
                    (v1.x * v2.y) - (v1.y * v2.x));
            v3.normalize();

            Coordinate defaultPos = new Coordinate(-leftLegData.getDefaultSize().x / 2f * v3.x, 0f);
            Coordinate newPos = new Coordinate(leftLegValues[0] * defaultPos.x + leftLegValues[1] * defaultPos.y,
                    leftLegValues[3] * defaultPos.x + leftLegValues[4] * defaultPos.y);

            // Set new position of left leg
            left_leg.setPadding((int)(leftLegData.getGlobalRotatedPivotLocation().x + newPos.x),
                    (int)(leftLegData.getGlobalRotatedPivotLocation().y + newPos.y), 0, 0);
        }

        BodyPart leftFootData = model.getLeftFoot();
        {
            // Let's get the matrix of the left leg
            float leftLegValues[] = new float[9];
            left_leg.getImageMatrix().getValues(leftLegValues);

            // Set initial scale of leg
            Matrix leftFootMatrix = new Matrix();
            Coordinate scale = leftFootData.getScale();
            leftFootMatrix.postScale(scale.x, scale.y);

            // Let's rotate the foot using our local rotation
            Coordinate defaultPivot = new Coordinate(
                    leftFootData.getLocalPivot().x * left_leg.getDrawable().getBounds().width() * scale.x,
                    leftFootData.getLocalPivot().y * left_leg.getDrawable().getBounds().height() * scale.y);
            float leftFootRotation = leftFootData.getLocalRotation();
            leftFootMatrix.postRotate(leftThighData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            leftFootMatrix.postRotate(leftLegData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            leftFootMatrix.postRotate(leftFootRotation, defaultPivot.x, defaultPivot.y);

            // Set matrix to foot
            left_foot.setImageMatrix(leftFootMatrix);

            // Get rotation matrix from left foot
            float leftFootValues[] = new float[9];
            left_foot.getImageMatrix().getValues(leftFootValues);
            leftFootValues[2] = 0f;
            leftFootValues[5] = 0f;
            leftFootValues[0] /= leftFootData.getScale().x;
            leftFootValues[1] /= leftFootData.getScale().x;
            leftFootValues[3] /= leftFootData.getScale().y;
            leftFootValues[4] /= leftFootData.getScale().y;

            // Set new position
            // Dot product
            Coordinate v1 = new Coordinate(leftFootData.getGlobalPosition().x - leftFootData.getGlobalPivotLocation().x,
                    leftFootData.getGlobalPosition().y - leftFootData.getGlobalPivotLocation().y);
            Coordinate v2 = new Coordinate(leftFootData.getGlobalRotatedPosition().x - leftFootData.getGlobalRotatedPivotLocation().x,
                    leftFootData.getGlobalRotatedPosition().y - leftFootData.getGlobalRotatedPivotLocation().y);
            v1.normalize();
            v2.normalize();
            Coordinate v3 = new Coordinate((v1.x * v2.x) + (v1.y * v2.y),
                    (v1.x * v2.y) - (v1.y * v2.x));
            v3.normalize();

            Coordinate defaultPos;
            Coordinate newPos;
            if (Math.abs(leftFootData.getTotalRotation()) > 90f) {
                defaultPos = new Coordinate(-leftFootData.getDefaultSize().x * 0.5f, 0f);
                newPos = new Coordinate(leftFootValues[0] * defaultPos.x + leftFootValues[1] * defaultPos.y,
                        leftFootValues[3] * defaultPos.x + leftFootValues[4] * defaultPos.y);
            } else {
                defaultPos = new Coordinate(-leftFootData.getDefaultSize().x * 0.5f
                        - (leftFootData.getDefaultSize().x * 0.5f * v3.x) , 0f);
                newPos = new Coordinate(leftFootValues[0] * defaultPos.x + leftFootValues[1] * defaultPos.y,
                        leftFootValues[3] * defaultPos.x + leftFootValues[4] * defaultPos.y);
            }

            // Set new position of left foot
            left_foot.setPadding((int)(leftFootData.getGlobalRotatedPivotLocation().x + newPos.x),
                    (int)(leftFootData.getGlobalRotatedPivotLocation().y + newPos.y), 0, 0);

            circle.x = leftFootData.getGlobalRotatedPivotLocation().x;
            circle.y = leftFootData.getGlobalRotatedPivotLocation().y;
            circle2.x = leftFootData.getGlobalRotatedPosition().x;
            circle2.y = leftFootData.getGlobalRotatedPosition().y;
        }

        BodyPart rightThighData = model.getRightThigh();
        {
            Coordinate scale = rightThighData.getScale();
            right_thigh.getImageMatrix().getValues(values);
            float rightThighPivotX = rightThighData.getLocalPivot().x * right_thigh.getDrawable().getBounds().width();
            float rightThighPivotY = rightThighData.getLocalPivot().y * right_thigh.getDrawable().getBounds().height();
            float rightThighRotation = model.getRightThigh().getLocalRotation();
            Matrix rightThighMatrix = new Matrix();
            rightThighMatrix.postScale(scale.x, scale.y);
            rightThighMatrix.postRotate(rightThighRotation, rightThighPivotX * scale.x, rightThighPivotY * scale.y);
            right_thigh.setImageMatrix(rightThighMatrix);
            right_thigh.setPadding((int)(rightThighData.getGlobalRotatedPivotLocation().x - rightThighData.getDefaultSize().x / 2f),
                    (int)(rightThighData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        BodyPart rightLegData = model.getRightLeg();
        {
            // Let's get the rotation matrix of the right thigh
            float rightThighValues[] = new float[9];
            right_thigh.getImageMatrix().getValues(rightThighValues);
            rightThighValues[2] = 0f;
            rightThighValues[5] = 0f;
            rightThighValues[0] /= rightThighData.getScale().x;
            rightThighValues[1] /= rightThighData.getScale().x;
            rightThighValues[3] /= rightThighData.getScale().y;
            rightThighValues[4] /= rightThighData.getScale().y;

            // Set initial scale of leg
            Matrix rightLegMatrix = new Matrix();
            Coordinate scale = rightLegData.getScale();
            rightLegMatrix.postScale(scale.x, scale.y);

            // Get rotation data for leg
            Coordinate defaultPivot = new Coordinate(
                    rightLegData.getLocalPivot().x * right_leg.getDrawable().getBounds().width() * scale.x,
                    rightLegData.getLocalPivot().y * right_leg.getDrawable().getBounds().height() * scale.y);

            // Rotate leg
            float rightLegRotation = rightLegData.getLocalRotation();
            rightLegMatrix.postRotate(rightThighData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            rightLegMatrix.postRotate(rightLegRotation, defaultPivot.x, defaultPivot.y);

            // Set matrix to leg
            right_leg.setImageMatrix(rightLegMatrix);

            // Get rotation matrix from right leg
            float rightLegValues[] = new float[9];
            right_leg.getImageMatrix().getValues(rightLegValues);
            rightLegValues[2] = 0f;
            rightLegValues[5] = 0f;
            rightLegValues[0] /= rightLegData.getScale().x;
            rightLegValues[1] /= rightLegData.getScale().x;
            rightLegValues[3] /= rightLegData.getScale().y;
            rightLegValues[4] /= rightLegData.getScale().y;

            // Set new position
            // Dot product
            Coordinate v1 = new Coordinate(rightLegData.getGlobalPosition().x - rightLegData.getGlobalPivotLocation().x,
                    rightLegData.getGlobalPosition().y - rightLegData.getGlobalPivotLocation().y);
            Coordinate v2 = new Coordinate(rightLegData.getGlobalRotatedPosition().x - rightLegData.getGlobalRotatedPivotLocation().x,
                    rightLegData.getGlobalRotatedPosition().y - rightLegData.getGlobalRotatedPivotLocation().y);
            v1.normalize();
            v2.normalize();
            Coordinate v3 = new Coordinate((v1.x * v2.x) + (v1.y * v2.y),
                    (v1.x * v2.y) - (v1.y * v2.x));
            v3.normalize();

            Coordinate defaultPos = new Coordinate(-rightLegData.getDefaultSize().x / 2f * v3.x, 0f);
            Coordinate newPos = new Coordinate(rightLegValues[0] * defaultPos.x + rightLegValues[1] * defaultPos.y,
                    rightLegValues[3] * defaultPos.x + rightLegValues[4] * defaultPos.y);

            // Set new position of right leg
            right_leg.setPadding((int)(rightLegData.getGlobalRotatedPivotLocation().x + newPos.x),
                    (int)(rightLegData.getGlobalRotatedPivotLocation().y + newPos.y), 0, 0);
        }

        BodyPart rightFootData = model.getRightFoot();
        {
            // Let's get the matrix of the right leg
            float rightLegValues[] = new float[9];
            right_leg.getImageMatrix().getValues(rightLegValues);

            // Set initial scale of leg
            Matrix rightFootMatrix = new Matrix();
            Coordinate scale = rightFootData.getScale();
            rightFootMatrix.postScale(scale.x, scale.y);

            // Let's rotate the foot using our local rotation
            Coordinate defaultPivot = new Coordinate(
                    rightFootData.getLocalPivot().x * right_leg.getDrawable().getBounds().width() * scale.x,
                    rightFootData.getLocalPivot().y * right_leg.getDrawable().getBounds().height() * scale.y);
            float rightFootRotation = rightFootData.getLocalRotation();
            rightFootMatrix.postRotate(rightThighData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            rightFootMatrix.postRotate(rightLegData.getLocalRotation(), defaultPivot.x, defaultPivot.y);
            rightFootMatrix.postRotate(rightFootRotation, defaultPivot.x, defaultPivot.y);

            // Set matrix to foot
            right_foot.setImageMatrix(rightFootMatrix);

            // Get rotation matrix from right foot
            float rightFootValues[] = new float[9];
            right_foot.getImageMatrix().getValues(rightFootValues);
            rightFootValues[2] = 0f;
            rightFootValues[5] = 0f;
            rightFootValues[0] /= rightFootData.getScale().x;
            rightFootValues[1] /= rightFootData.getScale().x;
            rightFootValues[3] /= rightFootData.getScale().y;
            rightFootValues[4] /= rightFootData.getScale().y;

            // Set new position
            // Dot product
            Coordinate v1 = new Coordinate(rightFootData.getGlobalPosition().x - rightFootData.getGlobalPivotLocation().x,
                    rightFootData.getGlobalPosition().y - rightFootData.getGlobalPivotLocation().y);
            Coordinate v2 = new Coordinate(rightFootData.getGlobalRotatedPosition().x - rightFootData.getGlobalRotatedPivotLocation().x,
                    rightFootData.getGlobalRotatedPosition().y - rightFootData.getGlobalRotatedPivotLocation().y);
            v1.normalize();
            v2.normalize();
            Coordinate v3 = new Coordinate((v1.x * v2.x) + (v1.y * v2.y),
                    (v1.x * v2.y) - (v1.y * v2.x));
            v3.normalize();

            Coordinate defaultPos;
            Coordinate newPos;
            if (Math.abs(rightFootData.getTotalRotation()) > 90f) {
                defaultPos = new Coordinate(rightFootData.getDefaultSize().x * 0.2f, 0f);
                newPos = new Coordinate(rightFootValues[0] * defaultPos.x + rightFootValues[1] * defaultPos.y,
                        rightFootValues[3] * defaultPos.x + rightFootValues[4] * defaultPos.y);
            } else {
                defaultPos = new Coordinate(rightFootData.getDefaultSize().x * 0.2f
                        - (rightFootData.getDefaultSize().x * 0.2f * v3.x) , 0f);
                newPos = new Coordinate(rightFootValues[0] * defaultPos.x + rightFootValues[1] * defaultPos.y,
                        rightFootValues[3] * defaultPos.x + rightFootValues[4] * defaultPos.y);
            }

            // Set new position of right foot
            right_foot.setPadding((int)(rightFootData.getGlobalRotatedPivotLocation().x + newPos.x),
                    (int)(rightFootData.getGlobalRotatedPivotLocation().y + newPos.y), 0, 0);
        }

        {
            BodyPart torsoData = model.getTorso();
            torso.setPadding((int) (torsoData.getGlobalPosition().x - torsoData.getSize().x / 2f),
                    (int) (torsoData.getGlobalPosition().y - torsoData.getSize().y / 2f), 0, 0);
        }

        invalidate();
    }

    private void resetDoll(int w, int h) {
        BodyPart headData = model.getHead();
        {
            headData.setSize(w * 0.075f, h * 0.18f);
            headData.setPosition((w * 0.461f) + headData.getSize().x / 2f,
                    (h * 0.164f) + headData.getSize().y / 2f);
            headData.setGlobalPivotLocation(headData.getGlobalPosition().x,
                    headData.getGlobalPosition().y + 0.17f * head.getDrawable().getBounds().height());
            headData.setDisplaySize(head.getDrawable().getBounds().width(),
                    head.getDrawable().getBounds().height());
            headData.reset();
        }

        BodyPart torsoData = model.getTorso();
        {
            torsoData.setSize(w * 0.08f, h * 0.24f);
            torsoData.setPosition((w * 0.46f) + torsoData.getSize().x / 2f,
                    (h * 0.34f) + torsoData.getSize().y / 2f);
            torsoData.setGlobalPivotLocation(headData.getGlobalPosition().x,
                    torsoData.getGlobalPosition().y);
            torsoData.setDisplaySize(torso.getDrawable().getBounds().width(),
                    torso.getDrawable().getBounds().height());
            torsoData.reset();
        }

        BodyPart leftArmData = model.getLeftArm();
        {
            leftArmData.setSize(w * 0.033f, h * 0.12f);
            leftArmData.setPosition((w * 0.425f) + leftArmData.getSize().x / 2f,
                    (h * 0.345f) + leftArmData.getSize().y / 2f);
            leftArmData.setGlobalPivotLocation(leftArmData.getGlobalPosition().x
                            + 0.09f * left_arm.getDrawable().getBounds().width(),
                    leftArmData.getGlobalPosition().y
                            - 0.095f * left_arm.getDrawable().getBounds().height());
            leftArmData.setDisplaySize(left_arm.getDrawable().getBounds().width(),
                    left_arm.getDrawable().getBounds().height());
            leftArmData.reset();
        }

        BodyPart leftForearmData = model.getLeftForearm();
        {
            leftForearmData.setSize(w * 0.025f, h * 0.095f);
            leftForearmData.setPosition((w * 0.422f) + leftForearmData.getSize().x / 2f,
                    (h * 0.44f) + leftForearmData.getSize().y / 2f);
            leftForearmData.setGlobalPivotLocation(leftForearmData.getGlobalPosition().x,
                    leftForearmData.getGlobalPosition().y
                            - 0.10f * left_forearm.getDrawable().getBounds().height());
            leftForearmData.setDisplaySize(left_forearm.getDrawable().getBounds().width(),
                    left_forearm.getDrawable().getBounds().height());
            leftForearmData.reset();
        }

        BodyPart leftHandData = model.getLeftHand();
        {
            leftHandData.setSize(w * 0.025f, h * 0.045f);
            leftHandData.setPosition((w * 0.422f) + leftHandData.getSize().x / 2f,
                    (h * 0.53f) + leftHandData.getSize().y / 2f);
            leftHandData.setGlobalPivotLocation(leftHandData.getGlobalPosition().x,
                    leftHandData.getGlobalPosition().y
                            - 0.12f * left_hand.getDrawable().getBounds().height());
            leftHandData.setDisplaySize(left_hand.getDrawable().getBounds().width(),
                    left_hand.getDrawable().getBounds().height());
            leftHandData.reset();
        }

        BodyPart rightArmData = model.getRightArm();
        {
            rightArmData.setSize(w * 0.032f, h * 0.131f);
            rightArmData.setPosition((w * 0.536f) + rightArmData.getSize().x / 2f,
                    (h * 0.3438f) + rightArmData.getSize().y / 2f);
            rightArmData.setGlobalPivotLocation(rightArmData.getGlobalPosition().x
                            - 0.08f * right_arm.getDrawable().getBounds().width(),
                    rightArmData.getGlobalPosition().y
                            - 0.12f * right_arm.getDrawable().getBounds().height());
            rightArmData.setDisplaySize(right_arm.getDrawable().getBounds().width(),
                    right_arm.getDrawable().getBounds().height());
            rightArmData.reset();
        }

        BodyPart rightForearmData = model.getRightForearm();
        {
            rightForearmData.setSize(w * 0.025f, h * 0.095f);
            rightForearmData.setPosition((w * 0.55f) + rightForearmData.getSize().x / 2f,
                    (h * 0.45f) + rightForearmData.getSize().y / 2f);
            rightForearmData.setGlobalPivotLocation(rightForearmData.getGlobalPosition().x,
                    rightForearmData.getGlobalPosition().y
                            - 0.105f * right_forearm.getDrawable().getBounds().height());
            rightForearmData.setDisplaySize(right_forearm.getDrawable().getBounds().width(),
                    right_forearm.getDrawable().getBounds().height());
            rightForearmData.reset();
        }

        BodyPart rightHandData = model.getRightHand();
        {
            rightHandData.setSize(w * 0.025f, h * 0.045f);
            rightHandData.setPosition((w * 0.549f) + rightHandData.getSize().x / 2f,
                    (h * 0.53f) + rightHandData.getSize().y / 2f);
            rightHandData.setGlobalPivotLocation(rightHandData.getGlobalPosition().x,
                    rightHandData.getGlobalPosition().y
                            - 0.12f * right_hand.getDrawable().getBounds().height());
            rightHandData.setDisplaySize(right_hand.getDrawable().getBounds().width(),
                    right_hand.getDrawable().getBounds().height());
            rightHandData.reset();
        }

        BodyPart leftThighData = model.getLeftThigh();
        {
            leftThighData.setSize(w * 0.028f, h * 0.16f);
            leftThighData.setPosition((w * 0.47f) + leftThighData.getSize().x / 2f,
                    (h * 0.572f) + leftThighData.getSize().y / 2f);
            leftThighData.setGlobalPivotLocation(leftThighData.getGlobalPosition().x,
                    leftThighData.getGlobalPosition().y
                            - 0.105f * left_thigh.getDrawable().getBounds().height());
            leftThighData.setDisplaySize(left_thigh.getDrawable().getBounds().width(),
                    left_thigh.getDrawable().getBounds().height());
            leftThighData.reset();
        }

        BodyPart leftLegData = model.getLeftLeg();
        {
            leftLegData.setSize(w * 0.0252f, h * 0.112f);
            leftLegData.setPosition((w * 0.471f) + leftLegData.getSize().x / 2f,
                    (h * 0.728f) + leftLegData.getSize().y / 2f);
            leftLegData.setGlobalPivotLocation(leftLegData.getGlobalPosition().x,
                    leftLegData.getGlobalPosition().y
                            - 0.11f * left_leg.getDrawable().getBounds().height());
            leftLegData.setDisplaySize(left_leg.getDrawable().getBounds().width(),
                    left_leg.getDrawable().getBounds().height());
            leftLegData.reset();
        }

        BodyPart leftFootData = model.getLeftFoot();
        {
            leftFootData.setSize(w * 0.05f, h * 0.035f);
            leftFootData.setPosition((w * 0.44f) + leftFootData.getSize().x / 2f,
                    (h * 0.83f) + leftFootData.getSize().y / 2f);
            leftFootData.setGlobalPivotLocation(leftFootData.getGlobalPosition().x
                            + 0.10f * left_foot.getDrawable().getBounds().width(),
                    leftFootData.getGlobalPosition().y
                            - 0.11f * left_foot.getDrawable().getBounds().height());
            leftFootData.setDisplaySize(left_foot.getDrawable().getBounds().width(),
                    left_foot.getDrawable().getBounds().height());
            leftFootData.reset();
        }

        BodyPart rightThighData = model.getRightThigh();
        {
            rightThighData.setSize(w * 0.028f, h * 0.16f);
            rightThighData.setPosition((w * 0.505f) + rightThighData.getSize().x / 2f,
                    (h * 0.572f) + rightThighData.getSize().y / 2f);
            rightThighData.setGlobalPivotLocation(rightThighData.getGlobalPosition().x,
                    rightThighData.getGlobalPosition().y
                            - 0.10f * right_thigh.getDrawable().getBounds().height());
            rightThighData.setDisplaySize(right_thigh.getDrawable().getBounds().width(),
                    right_thigh.getDrawable().getBounds().height());
            rightThighData.reset();
        }

        BodyPart rightLegData = model.getRightLeg();
        {
            rightLegData.setSize(w * 0.0252f, h * 0.112f);
            rightLegData.setPosition((w * 0.507f) + rightLegData.getSize().x / 2f,
                    (h * 0.728f) + rightLegData.getSize().y / 2f);
            rightLegData.setGlobalPivotLocation(rightLegData.getGlobalPosition().x,
                    rightLegData.getGlobalPosition().y
                            - 0.11f * right_leg.getDrawable().getBounds().height());
            rightLegData.setDisplaySize(right_leg.getDrawable().getBounds().width(),
                    right_leg.getDrawable().getBounds().height());
            rightLegData.reset();
        }

        BodyPart rightFootData = model.getRightFoot();
        {
            rightFootData.setSize(w * 0.05f, h * 0.035f);
            rightFootData.setPosition((w * 0.51f) + rightFootData.getSize().x / 2f,
                    (h * 0.83f) + rightFootData.getSize().y / 2f);
            rightFootData.setGlobalPivotLocation(rightFootData.getGlobalPosition().x
                            - 0.08f * right_foot.getDrawable().getBounds().width(),
                    rightFootData.getGlobalPosition().y
                            - 0.11f * right_foot.getDrawable().getBounds().height());
            rightFootData.setDisplaySize(right_foot.getDrawable().getBounds().width(),
                    right_foot.getDrawable().getBounds().height());
            rightFootData.reset();
        }

        {
            Matrix headMatrix = new Matrix();
            headMatrix.postScale(0.36085218f,0.36085218f);
            head.setImageMatrix(headMatrix);
            head.setPadding((int) (headData.getGlobalPosition().x - headData.getSize().x / 2f),
                    (int) (headData.getGlobalPosition().y - headData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix torsoMatrix = new Matrix();
            torsoMatrix.postScale(torsoData.getSize().x / torso.getDrawable().getIntrinsicWidth(),
                    torsoData.getSize().y / torso.getDrawable().getIntrinsicHeight());
            torso.setImageMatrix(torsoMatrix);
            torso.setPadding((int) (torsoData.getGlobalPosition().x - torsoData.getSize().x / 2f),
                    (int) (torsoData.getGlobalPosition().y - torsoData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix leftArmMatrix = new Matrix();
            leftArmMatrix.postScale(0.22663091f, 0.22663394f);
            left_arm.setImageMatrix(leftArmMatrix);
            left_arm.setPadding((int) (leftArmData.getGlobalPosition().x - leftArmData.getSize().x / 2f),
                    (int) (leftArmData.getGlobalPosition().y - leftArmData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix leftForearmMatrix = new Matrix();
            leftForearmMatrix.postScale(0.22663091f, 0.22663394f);
            left_forearm.setImageMatrix(leftForearmMatrix);
            left_forearm.setPadding((int) (leftForearmData.getGlobalPosition().x - leftForearmData.getSize().x / 2f),
                    (int) (leftForearmData.getGlobalPosition().y - leftForearmData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix leftHandMatrix = new Matrix();
            leftHandMatrix.postScale(0.22663091f, 0.22663394f);
            left_hand.setImageMatrix(leftHandMatrix);
            left_hand.setPadding((int) (leftHandData.getGlobalPosition().x - leftHandData.getSize().x / 2f),
                    (int) (leftHandData.getGlobalPosition().y - leftHandData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix rightArmMatrix = new Matrix();
            rightArmMatrix.postScale(0.22891548f,0.22891548f);
            right_arm.setImageMatrix(rightArmMatrix);
            right_arm.setPadding((int) (rightArmData.getGlobalPosition().x - rightArmData.getSize().x / 2f),
                    (int) (rightArmData.getGlobalPosition().y - rightArmData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix rightForearmMatrix = new Matrix();
            rightForearmMatrix.postScale(0.22663091f, 0.22663394f);
            right_forearm.setImageMatrix(rightForearmMatrix);
            right_forearm.setPadding((int) (rightForearmData.getGlobalPosition().x - rightForearmData.getSize().x / 2f),
                    (int) (rightForearmData.getGlobalPosition().y - rightForearmData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix rightHandMatrix = new Matrix();
            rightHandMatrix.postScale(0.22663091f, 0.22663394f);
            right_hand.setImageMatrix(rightHandMatrix);
            right_hand.setPadding((int) (rightHandData.getGlobalPosition().x - rightHandData.getSize().x / 2f),
                    (int) (rightHandData.getGlobalPosition().y - rightHandData.getSize().y / 2f), 0, 0);
        }

        {
            Matrix leftThighMatrix = new Matrix();
            leftThighMatrix.postScale(0.24663091f, 0.23663394f);
            left_thigh.setImageMatrix(leftThighMatrix);
            left_thigh.setPadding((int)(leftThighData.getGlobalRotatedPivotLocation().x - leftThighData.getDefaultSize().x / 2f),
                    (int)(leftThighData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        {
            Matrix leftLegMatrix = new Matrix();
            leftLegMatrix.postScale(0.24663091f, 0.23663394f);
            left_leg.setImageMatrix(leftLegMatrix);
            left_leg.setPadding((int)(leftLegData.getGlobalRotatedPivotLocation().x - leftLegData.getDefaultSize().x / 2f),
                    (int)(leftLegData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        {
            Matrix leftFootMatrix = new Matrix();
            leftFootMatrix.postScale(0.24663091f, 0.23663394f);
            left_foot.setImageMatrix(leftFootMatrix);
            left_foot.setPadding((int)(leftFootData.getGlobalRotatedPivotLocation().x - leftFootData.getDefaultSize().x),
                    (int)(leftFootData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        {
            Matrix rightThighMatrix = new Matrix();
            rightThighMatrix.postScale(0.24663091f, 0.23663394f);
            right_thigh.setImageMatrix(rightThighMatrix);
            right_thigh.setPadding((int)(rightThighData.getGlobalRotatedPivotLocation().x - rightThighData.getDefaultSize().x / 2f),
                    (int)(rightThighData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        {
            Matrix rightLegMatrix = new Matrix();
            rightLegMatrix.postScale(0.24663091f, 0.23663394f);
            right_leg.setImageMatrix(rightLegMatrix);
            right_leg.setPadding((int)(rightLegData.getGlobalRotatedPivotLocation().x - rightLegData.getDefaultSize().x / 2f),
                    (int)(rightLegData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        {
            Matrix rightFootMatrix = new Matrix();
            rightFootMatrix.postScale(0.24663091f, 0.23663394f);
            right_foot.setImageMatrix(rightFootMatrix);
            right_foot.setPadding((int)(rightFootData.getGlobalRotatedPivotLocation().x),
                    (int)(rightFootData.getGlobalRotatedPivotLocation().y), 0, 0);
        }

        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetDoll(w, h);
    }
}