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

    // each individual grid is 1 unit;
    private int gridSize; // if 50 then there are 50 1x1 squares

    Grid(ColourRGB colour, float yPos) {
        this(colour, yPos, 50);
    }

    private Grid(ColourRGB colour, float yPos, int gridSize) {
        gridColour = colour;
        height = yPos;
        this.gridSize = gridSize;
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glColor4f(gridColour.RED, gridColour.GREEN, gridColour.BLUE, gridColour.ALPHA);

        /* There are four equal sections in each 2D grid, and as such I have used gridSize
           to represent the length of a single quadrant. The entire grid is constructed by
           starting at negative gridSize and ending at positive gridSize.
           For example, if using a grid size of 2 then start at -2 and go to +2.
           Note: (Resulting square will be 4x4) for that example*/
        for(int i = -gridSize; i < gridSize; i++) {
            for(int j = -gridSize; j < gridSize; j++) {
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
