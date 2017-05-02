import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.util.LinkedList;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 25/04/2017.
 * This abstract class was created to be used as the nodes in a linked tree structure to
 * allow for a hierarchical submarine model to be created.
 */
public abstract class SubmarineComponent implements Drawable {
    private LinkedList<SubmarineComponent> children;
    double radius, height;
    private double rotationAngle, transX, transY, transZ;
    private ROTATION_AXIS axis;

    SubmarineComponent(double radius, double height, ROTATION_AXIS axis) {
        children = new LinkedList<>();
        this.radius = radius;
        this.height = height;
        this.axis   = axis;
    }

    void addChild(SubmarineComponent newChild) {
        children.add(newChild);
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glPushMatrix();
            transformNode(gl2);
            drawNode(gl2, glu, quadric, filled);
            for(SubmarineComponent child : children) {
                child.draw(gl2, glu, quadric, filled);
            }
        gl2.glPopMatrix();
    }

    private void transformNode(GL2 gl2) {
        // do the translation relative to the parent
        gl2.glTranslated(transX, transY, transZ);

        // Rotate node
        switch (axis) {
            case X:
                gl2.glRotated(rotationAngle, 1, 0, 0);
                break;
            case Y:
                gl2.glRotated(rotationAngle, 0, 1, 0);
                break;
            case Z:
                gl2.glRotated(rotationAngle, 0, 0, 1);
                break;
        }
    }

    void setTranslations(double x, double y, double z) {
        transX = x;
        transY = y;
        transZ = z;
    }

    void setRotation(double theta) {
        rotationAngle = theta;
    }

    abstract void drawNode(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled);
}
