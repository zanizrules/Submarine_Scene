import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Created by Shane on 01/06/2017.
 * ID: 14870204
 * This class represents the bubbles that are released from the submarine when it dives.
 */
class Bubble implements Drawable {
    private final float speed;
    private float x, z;
    float y, transparency, radius;

    Bubble(float transparency, float radius, float x, float y, float z, float speed) {
        this.transparency = transparency;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        Materials.setBubbleMaterial(gl);
        gl.glPushMatrix();
            gl.glTranslated(x, y, z);
            glu.gluSphere(quadric, radius, 20, 20);
        gl.glPopMatrix();
        Materials.clearMaterials(gl);
        this.y += speed;
        if (transparency > 0) {
            this.transparency -= speed / 2;
        } else transparency = 0;

        if (radius > 0) {
            this.radius -= speed / 50;
        } else radius = 0;
    }
}
