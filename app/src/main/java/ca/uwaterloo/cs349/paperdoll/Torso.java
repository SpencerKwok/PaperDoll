package ca.uwaterloo.cs349.paperdoll;

class Torso extends BodyPart {
    Torso(float _x, float _y, float _scale_x, float _scale_y) {
        super(_x, _y, _scale_x, _scale_y, 0f);
    }

    @Override
    void rotate(float _angle) {
    }

    @Override
    boolean isInside(float _x, float _y) {
        Coordinate position = getGlobalPosition();
        Coordinate size = getSize();
        return !((_x < position.x - size.x / 2f) ||
                (_x > (position.x + size.x / 2f)) ||
                (_y < position.y - size.y / 2f) ||
                (_y > (position.y + size.y / 2f)));
    }
}