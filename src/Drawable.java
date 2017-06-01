import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 25/04/2017.
 * This Interface specifies the common methods that all drawable objects must have.
 */
interface Drawable {
    void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled);
}
