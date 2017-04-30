import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 29/04/2017.
 * Insert Description...
 */
public class Grid implements Drawable {

    ColourRGB gridColour;
    private boolean filled;
    private float height;
    // GRID_SIZE is 1;
    private final int GRID_SIZE = 25; // 50 1x1 squares

    Grid(ColourRGB colour, float yPos) {
        gridColour = colour;
        height = yPos;
    }



    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glColor4f(gridColour.RED, gridColour.GREEN, gridColour.BLUE, gridColour.ALPHA);

        for(int i = -GRID_SIZE; i < GRID_SIZE; i++) {
            for(int j = -GRID_SIZE; j < GRID_SIZE; j++) {
                gl2.glBegin(filled ? GL2.GL_QUADS : GL2.GL_LINE_LOOP);
                    gl2.glNormal3d(0,1,0);
                    gl2.glVertex3d(i,height,j);
                    gl2.glNormal3d(0,1,0);
                    gl2.glVertex3d(i + 1,height,j);
                    gl2.glNormal3d(0,1,0);
                    gl2.glVertex3d(i + 1,height,j + 1);
                    gl2.glNormal3d(0,1,0);
                    gl2.glVertex3d(i,height,j + 1);
                gl2.glEnd();
            }
        }

    }
}
