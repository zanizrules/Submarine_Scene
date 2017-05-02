import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 3/04/2017. Edited: 23/04/2017
 * When drawn this object is used to easily identify the origin within a 3D view
 */
class Origin implements Drawable {
    private static final float LINE_SIZE = 6;

    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        // Draw ball
        gl.glColor3f(1,1,1);
        glu.gluSphere(quadric, 0.05f,20,10);

        gl.glBegin(GL.GL_LINES);
            // Draw y line
            gl.glColor3f(0,1,0); // green
            gl.glVertex3f(0,LINE_SIZE/2,0);
            gl.glVertex3f(0,-LINE_SIZE/2,0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINES);
            // Draw x line
            gl.glColor3f(1,0,0); // red
            gl.glVertex3f(LINE_SIZE/2,0,0);
            gl.glVertex3f(-LINE_SIZE/2,0,0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINES);
            // Draw z line
            gl.glColor3f(0,0,1); // blue
            gl.glVertex3f(0,0,-LINE_SIZE/2);
            gl.glVertex3f(0,0,LINE_SIZE/2);
        gl.glEnd();
    }
}
