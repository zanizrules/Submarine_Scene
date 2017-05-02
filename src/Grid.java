import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 29/04/2017.
 * The Grid class is used to create 2D grids which can be rendered as a solid or wireframe.
 * Within the Submarine Scene grids are used for the seabed and sea surface.
 */
public class Grid implements Drawable {
    private ColourRGB gridColour;
    float height; // Grid location in the Y axis

    // GRID_SIZE is 1;
    private static final int GRID_SIZE = 50; // 50 1x1 squares

    Grid(ColourRGB colour, float yPos) {
        gridColour = colour;
        height = yPos;
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glColor4f(gridColour.RED, gridColour.GREEN, gridColour.BLUE, gridColour.ALPHA);

        /* There are four equal sections in each 2D grid, and as such I have used GRID_SIZE
           to represent the length of a single quadrant. The entire grid is constructed by
           starting at negative GRID_SIZE and ending at positive GRID_SIZE.
           For example, if using a grid size of 2 then start at -2 and go to +2.
           Note: (Resulting square will be 4x4) for that example*/
        for(int i = -GRID_SIZE; i < GRID_SIZE; i++) {
            for(int j = -GRID_SIZE; j < GRID_SIZE; j++) {
                gl2.glBegin(filled ? GL2.GL_QUADS : GL2.GL_LINE_LOOP); // Solid or Wireframe
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
