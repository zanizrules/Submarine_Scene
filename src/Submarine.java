import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;


/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 27/04/2017.
 * This class combines all of the Submarines components and adds them together using a tree structure
 */
public class Submarine implements Drawable {

    private final double SUBMARINE_RADIUS, SUBMARINE_HEIGHT;
    private final double ROTATION_SPEED = 2.5, MOVEMENT_SPEED = 0.05;
    private final ColourRGB SUBMARINE_PRIMARY = new ColourRGB(1,0.2f,0);
    private final ColourRGB SUBMARINE_SECONDARY = new ColourRGB(1,0.05f,0);

    private SubmarineComponent root;
    double x, y, z, submarineRotation;
    private double propellerRotation;
    private SUBMARINE_STATE state;

    Submarine(float size) {
        SUBMARINE_RADIUS = size * 0.4;
        SUBMARINE_HEIGHT = size * 0.25;
        x = 0;
        y = 0;
        z = 0;
        propellerRotation = 0;
        submarineRotation = 0;
        state = SUBMARINE_STATE.IDLE;

        root = new SubmarineBody(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        root.setRotation(90);

        SubmarineSail sail = new SubmarineSail(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);
        sail.setTranslations(0, (9* SUBMARINE_HEIGHT)/10, 0);
        sail.setRotation(-90);

        SubmarinePeriscope periscope = new SubmarinePeriscope(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);
        periscope.setTranslations(0, 0, SUBMARINE_HEIGHT /3);
        sail.setRotation(-90);

        SubmarinePeriscope periscopePart = new SubmarinePeriscope(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        periscopePart.setTranslations(SUBMARINE_RADIUS /15,0, SUBMARINE_HEIGHT /2);
        periscopePart.setRotation(-90);
        periscope.addChild(periscopePart);
        sail.addChild(periscope);

        root.addChild(sail);

        SubmarineConnector connector = new SubmarineConnector(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        connector.setRotation(90);
        connector.setTranslations((9* SUBMARINE_RADIUS)/10, 0, 0);

        SubmarinePropeller propeller1 = new SubmarinePropeller(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        propeller1.setTranslations(0, 0, SUBMARINE_RADIUS /4);
        connector.addChild(propeller1);

        SubmarinePropeller propeller2 = new SubmarinePropeller(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Z);
        propeller2.setTranslations(0, 0, SUBMARINE_RADIUS /4);
        propeller2.setRotation(90);
        connector.addChild(propeller2);

        root.addChild(connector);
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glPushMatrix();
        if(!state.equals(SUBMARINE_STATE.IDLE)) {
            if(state.equals(SUBMARINE_STATE.DIVING)) {
                if(y > (-Renderer.SEA_HEIGHT/2) + (SUBMARINE_HEIGHT*2)) {
                    y -= MOVEMENT_SPEED;
                }
            } else if(state.equals(SUBMARINE_STATE.SURFACING)) {
                if(y < (Renderer.SEA_HEIGHT/2) - (SUBMARINE_HEIGHT/2)) {
                    y += MOVEMENT_SPEED;
                }
            } else {
                propellerRotation += ROTATION_SPEED;
                if(state.equals(SUBMARINE_STATE.MOVING_FORWARD)) {
                    if(propellerRotation < 0) {
                        // TODO: fix
                        propellerRotation *= -1.0;
                    }
                    x += MOVEMENT_SPEED * Math.sin(Math.toRadians(submarineRotation));
                    z += MOVEMENT_SPEED * Math.cos(Math.toRadians(submarineRotation));
                } else if(state.equals(SUBMARINE_STATE.MOVING_BACKWARD)) {
                    if(propellerRotation > 0) {
                        // TODO: fix
                        propellerRotation *= -1.0;
                    }
                    x -= MOVEMENT_SPEED * Math.sin(Math.toRadians(submarineRotation));
                    z -= MOVEMENT_SPEED * Math.cos(Math.toRadians(submarineRotation));
                } else if(state.equals(SUBMARINE_STATE.TURNING_LEFT)) {
                    submarineRotation -= ROTATION_SPEED;
                } else if(state.equals(SUBMARINE_STATE.TURNING_RIGHT)) {
                    submarineRotation += ROTATION_SPEED;
                }
            }
        }

        gl2.glTranslated(x, y, z);
        gl2.glRotated(submarineRotation, 0, 1, 0);
        root.draw(gl2, glu, quadric, filled);
        gl2.glPopMatrix();
    }

    public void changeState(SUBMARINE_STATE state) {
        this.state = state;
    }

    private class SubmarineBody extends SubmarineComponent {

        SubmarineBody(double radius, double height, ROTATION_AXIS axis) {
            super(radius, height, axis);
        }

        @Override
        void drawNode (GL2 gl2, GLU glu, GLUquadric quadric, boolean filled){
            gl2.glPushMatrix();
            // Set component colour
            gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN,SUBMARINE_PRIMARY.BLUE);

            // Set the size of the component (X, Y, Z)
            gl2.glScaled(radius, height, height);

            // Draw the component
            glu.gluSphere(quadric,1,25,20);
            gl2.glPopMatrix();
        }
    }

    private class SubmarineConnector extends SubmarineComponent {

        private GLUT glut;

        SubmarineConnector(double radius, double height, ROTATION_AXIS axis) {
            super(radius, height, axis);
            glut = new GLUT();
        }

        @Override
        void drawNode(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
            gl2.glPushMatrix();
            // Set component colour
            gl2.glColor3f(SUBMARINE_SECONDARY.RED, SUBMARINE_SECONDARY.GREEN, SUBMARINE_SECONDARY.BLUE);

            // Set the size of the component (X, Y, Z)
            gl2.glScaled(height/5, height/5, radius/4);

            // Draw the component
            glu.gluCylinder(quadric, 1, 1, 1, 5, 5);

            gl2.glTranslated(0, 0, height*3);

            gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN + 0.05f,SUBMARINE_PRIMARY.BLUE);

            if(filled) {
                glut.glutSolidCone(1.5f,0.75f,5,5);
            } else {
                glut.glutWireCone(1.5f, 0.75f, 5, 5);
            }
            gl2.glPopMatrix();
        }
    }

    private class SubmarinePeriscope extends SubmarineComponent {

        SubmarinePeriscope(double radius, double height, ROTATION_AXIS axis) {
            super(radius, height, axis);
        }

        @Override
        void drawNode (GL2 gl2, GLU glu, GLUquadric quadric, boolean filled){
            gl2.glPushMatrix();
            // Set component colour
            gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN + 0.05f,SUBMARINE_PRIMARY.BLUE);

            // Set the size of the component (X, Y, Z)
            gl2.glScaled(radius/15, radius/15, height/2);

            // Draw the component
            glu.gluCylinder(quadric, 1, 1, 1,8, 6);
            gl2.glPopMatrix();
        }
    }

    private class SubmarinePropeller extends SubmarineComponent {

        SubmarinePropeller(double radius, double height, ROTATION_AXIS axis) {
            super(radius, height, axis);
        }

        @Override
        void drawNode(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
            gl2.glPushMatrix();
            // Set component colour
            gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN + 0.05f,SUBMARINE_PRIMARY.BLUE);

            // Rotate
            gl2.glRotated(90, 1, 0, 0);
            gl2.glRotated(propellerRotation, 0, 1, 0);

            // Set the size of the component (X, Y, Z)
            gl2.glScaled(radius/5, radius/8, 0.5f);

            // Draw the component
            glu.gluSphere(quadric, radius, 15, 15);

            gl2.glPopMatrix();
        }
    }

    private class SubmarineSail extends SubmarineComponent {

        SubmarineSail(double radius, double height, ROTATION_AXIS axis) {
            super(radius, height, axis);
        }

        @Override
        void drawNode (GL2 gl2, GLU glu, GLUquadric quadric, boolean filled){
            gl2.glPushMatrix();
            // Set component colour
            gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN,SUBMARINE_PRIMARY.BLUE);

            // Set the size of the component (X, Y, Z)
            gl2.glScaled(radius, height, height);

            // Draw the component
            glu.gluCylinder(quadric, radius, radius/1.5f, height*1.5,4, 4);
            gl2.glPopMatrix();
        }
    }
}
