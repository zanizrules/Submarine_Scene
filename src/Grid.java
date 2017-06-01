import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import org.w3c.dom.css.RGBColor;

import java.io.IOException;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 29/04/2017.
 * The Grid class is used to create 2D grids which can be rendered as a solid or wireframe.
 * Within the Submarine Scene grids are used for the seabed and sea surface.
 */
public class Grid implements Drawable {
    float height; // Grid location in the Y axis
    private Texture gridTexture;
    private float textureOffSet = 0;
    private boolean textured;
    private ColourRGB gridColour;

    // each individual grid is 1 unit;
    private int gridSize; // if 50 then there are 50 1x1 squares

    Grid(float yPos, ColourRGB colour) {
        this(yPos, 50, null, colour);
    }

    Grid(float yPos, String textureFile) {
        this(yPos, 50, textureFile, null);
    }

    private Grid(float yPos, int gridSize, String textureFile, ColourRGB colour) {
        gridColour = colour;
        height = yPos;
        this.gridSize = gridSize;
        textured = !(textureFile == null || textureFile.trim().isEmpty());
        if(textured) {
            try {
                setGridTexture(textureFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void setGridTexture(String file) throws IOException {
       gridTexture = TextureIO.newTexture(this.getClass().getResourceAsStream(file + ".jpg"), true, "jpg");
    }

    void animateTexture() {
        if(textureOffSet == 0) {
            textureOffSet = 1;
        } else textureOffSet = 0;
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        if(!textured) {
            Materials.setMaterial(gl2, gridColour);
        } else {
            gridTexture.enable(gl2);
            gridTexture.bind(gl2);

            /* Uses s,t modulo 1.
            Texture coordinates outside the range 0.0 - 1.0 will produce duplicates of the texture */
            gridTexture.setTexParameteri(gl2, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            gridTexture.setTexParameteri(gl2, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

            gl2.glColor4f(1,1,1,0.95f);
        }

        /* There are four equal sections in each 2D grid, and as such I have used gridSize
           to represent the length of a single quadrant. The entire grid is constructed by
           starting at negative gridSize and ending at positive gridSize.
           For example, if using a grid size of 2 then start at -2 and go to +2.
           Note: (Resulting square will be 4x4) for that example*/

        for(int i = -gridSize; i < gridSize; i++) {
            for(int j = -gridSize; j < gridSize; j++) {
                gl2.glBegin(filled ? GL2.GL_QUADS : GL2.GL_LINE_LOOP); // Solid or Wireframe

                    gl2.glNormal3d(0,1,0);
                    gl2.glTexCoord2d(2 - textureOffSet, 1 + textureOffSet);
                    gl2.glVertex3d(i,height,j);

                    gl2.glNormal3d(0,1,0);
                    gl2.glTexCoord2d(2 - textureOffSet, 2 - textureOffSet);
                    gl2.glVertex3d(i + 1,height,j);

                    gl2.glNormal3d(0,1,0);
                    gl2.glTexCoord2d(1 + textureOffSet, 2 - textureOffSet);
                    gl2.glVertex3d(i + 1,height,j + 1);

                    gl2.glNormal3d(0,1,0);
                    gl2.glTexCoord2d(1 + textureOffSet, 1 + textureOffSet);
                    gl2.glVertex3d(i,height,j + 1);

                gl2.glEnd();
            }
        }
        if(!textured) {
            Materials.clearMaterials(gl2);
        } else gridTexture.disable(gl2);
    }
}
