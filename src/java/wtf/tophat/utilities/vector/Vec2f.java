package wtf.tophat.utilities.vector;

public class Vec2f {
    private float x, y;

    public Vec2f() {
        this(0, 0);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
