package ca.uwaterloo.cs349.paperdoll;

class Coordinate {
    float x;
    float y;

    Coordinate() {}
    Coordinate(float _x, float _y) { x = _x; y = _y; }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }

    public String toString() {
        return "{" + Float.toString(x) + "," + Float.toString(y) + "}";
    }
}
