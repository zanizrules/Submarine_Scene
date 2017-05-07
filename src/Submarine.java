import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 27/04/2017.
 * This class combines all of the Submarines components and adds them together using a tree structure.
 * A Submarine also has a state which determines what it is doing within the scene. For example a Submarine
 * could be turning, or moving.
 */
public class Submarine implements Drawable {

    private final double SUBMARINE_RADIUS, SUBMARINE_HEIGHT;
    private static final double ROTATION_SPEED = 1, PROPELLER_ROTATION_SPEED = 6, MOVEMENT_SPEED = 0.1, TILT_AMOUNT = 20;
    private static final ColourRGB SUBMARINE_PRIMARY = new ColourRGB(1,0.2f,0);
    private static final ColourRGB SUBMARINE_SECONDARY = new ColourRGB(1,0.05f,0);

    private SubmarineComponent root;
    double x, y, z, submarineRotation, propellerRotation;
    float[] spotLightDirection = {0, 0, 0};
    private SUBMARINE_STATE turningState;
    private SUBMARINE_STATE verticalMovementState;
    private SUBMARINE_STATE horizontalMovementState;

    private boolean rotateRight; // Used to determine which way the propeller should rotate

    Submarine(float size) {
        SUBMARINE_RADIUS = size * 0.4;
        SUBMARINE_HEIGHT = size * 0.25;
        x = 0;
        y = 0;
        z = 0;
        propellerRotation = 0;
        submarineRotation = 0;
        //spotLightDirection = new float[3];
        turningState = SUBMARINE_STATE.IDLE;
        verticalMovementState = SUBMARINE_STATE.IDLE;
        horizontalMovementState = SUBMARINE_STATE.IDLE;

        root = new SubmarineBody(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);

        // Sail -> Child of root/body
        SubmarineSail sail = new SubmarineSail(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);
        sail.setTranslations(0, (7.5* SUBMARINE_HEIGHT)/10, 0);
        sail.setRotation(-90);

        // Periscope -> Child of Sail
        SubmarinePeriscope periscope = new SubmarinePeriscope(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);
        periscope.setTranslations(0, 0, (2.5* SUBMARINE_HEIGHT)/10);
        SubmarinePeriscope periscopeExtension = new SubmarinePeriscope(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.X);
        periscopeExtension.setTranslations(0,0, SUBMARINE_HEIGHT/3);
        periscope.addChild(periscopeExtension);
        SubmarinePeriscope periscopePart = new SubmarinePeriscope(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        periscopePart.setTranslations(SUBMARINE_RADIUS /15,0, SUBMARINE_HEIGHT /2);
        periscopePart.setRotation(-90);
        periscopeExtension.addChild(periscopePart);
        sail.addChild(periscope);

        root.addChild(sail);

        // Propeller -> Propeller is the child of a connector which is the child of root
        SubmarineConnector connector = new SubmarineConnector(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        connector.setRotation(90);
        connector.setTranslations((9* SUBMARINE_RADIUS)/10, 0, 0);
        SubmarinePropeller propeller1 = new SubmarinePropeller(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Y);
        propeller1.setTranslations(0, 0, SUBMARINE_RADIUS/4);
        connector.addChild(propeller1);
        SubmarinePropeller propeller2 = new SubmarinePropeller(SUBMARINE_RADIUS, SUBMARINE_HEIGHT, ROTATION_AXIS.Z);
        propeller2.setTranslations(0, 0, SUBMARINE_RADIUS/4);
        propeller2.setRotation(90);
        connector.addChild(propeller2);

        root.addChild(connector);
    }

    void changeTurningState(SUBMARINE_STATE state) {
        turningState = state; // Left/Right/Idle
    }

    void changeVerticalMovementState(SUBMARINE_STATE state) {
        verticalMovementState = state; // Up/Down/Idle
    }

    void changeHorizontalMovementState(SUBMARINE_STATE state) {
        horizontalMovementState = state; // Forward/Backward/Idle
    }

    @Override
    public void draw(GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        submarineRotation = submarineRotation % 360; // Reset angles to within 0-360
        propellerRotation = propellerRotation % 360;
        gl2.glPushMatrix();
        animateSubmarine();
        gl2.glTranslated(x, y, z); // Translate based on x,y,z determined above
        gl2.glRotated(90 + submarineRotation, 0, 1, 0); // rotate based on angle determined above
        if(turningState == SUBMARINE_STATE.TURNING_LEFT) {
            gl2.glRotated(TILT_AMOUNT, 1, 0, 0);
        } else if(turningState == SUBMARINE_STATE.TURNING_RIGHT) {
            gl2.glRotated(-TILT_AMOUNT, 1, 0, 0);
        }
        if(verticalMovementState == SUBMARINE_STATE.SURFACING) {
            gl2.glRotated(-TILT_AMOUNT, 0, 0, 1);
        } else if(verticalMovementState == SUBMARINE_STATE.DIVING) {
            gl2.glRotated(TILT_AMOUNT, 0, 0, 1);
        }

        root.draw(gl2, glu, quadric, filled); // Draw root node, which draws all child nodes
        gl2.glPopMatrix();
    }

    // Manipulates the submarines values to determine how it should move
    private void animateSubmarine() {

        // Vertical Movement
        if(verticalMovementState.equals(SUBMARINE_STATE.DIVING)) { // Handle diving movement
            if(y > (-Renderer.SEA_HEIGHT/2) + (SUBMARINE_HEIGHT*2)) {
                y -= MOVEMENT_SPEED/2;
            }
        } else if(verticalMovementState.equals(SUBMARINE_STATE.SURFACING)) { // Handle surfacing movement
            if (y < (Renderer.SEA_HEIGHT / 2) - (SUBMARINE_HEIGHT / 2)) {
                y += MOVEMENT_SPEED / 2;
            }
        } else {
            if(horizontalMovementState != SUBMARINE_STATE.IDLE || turningState != SUBMARINE_STATE.IDLE) {
                propellerRotation += PROPELLER_ROTATION_SPEED; // Rotate propeller when turning or moving
            }
        }

        // Horizontal Movement
        if (horizontalMovementState.equals(SUBMARINE_STATE.MOVING_FORWARD)) { // Handle moving forward
            rotateRight = true;
            x += MOVEMENT_SPEED * Math.sin(Math.toRadians(submarineRotation)); // Move based on current heading
            z += MOVEMENT_SPEED * Math.cos(Math.toRadians(submarineRotation));
        } else if (horizontalMovementState.equals(SUBMARINE_STATE.MOVING_BACKWARD)) { // Handle moving backwards
            rotateRight = false;
            x -= MOVEMENT_SPEED * Math.sin(Math.toRadians(submarineRotation)); // Move based on current heading
            z -= MOVEMENT_SPEED * Math.cos(Math.toRadians(submarineRotation));
        }

        // Turning Movement
        if(turningState.equals(SUBMARINE_STATE.TURNING_LEFT)) { // Handle left turn
            rotateRight = true;
            submarineRotation += ROTATION_SPEED;
        } else if(turningState.equals(SUBMARINE_STATE.TURNING_RIGHT)) { // Handle right turn
            rotateRight = false;
            submarineRotation -= ROTATION_SPEED;
        }
    }

private class SubmarineBody extends SubmarineComponent {

    SubmarineBody(double radius, double height, ROTATION_AXIS axis) {
        super(radius, height, axis);
    }

    @Override
    void drawNode (GL2 gl2, GLU glu, GLUquadric quadric, boolean filled){
        gl2.glPushMatrix();
        gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN,SUBMARINE_PRIMARY.BLUE);
        gl2.glScaled(radius, height, height);
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
        // Draw Cylinder
        gl2.glColor3f(SUBMARINE_SECONDARY.RED,SUBMARINE_SECONDARY.GREEN,SUBMARINE_SECONDARY.BLUE);
        gl2.glScaled(height/5, height/5, radius/4);
        glu.gluCylinder(quadric, 1, 1, 1, 5, 5);

        // Draw Cone
        gl2.glTranslated(0, 0, height*3); // Move
        gl2.glColor3f(1,0.25f,0);
        if(filled) {
            glut.glutSolidCone(1.5f,0.75f,5,5);
        } else {
            glut.glutWireCone(1.5f,0.75f,5,5);
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
        gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN + 0.05f,SUBMARINE_PRIMARY.BLUE);
        gl2.glScaled(radius/15, radius/15, height/2);
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
        gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN + 0.05f,SUBMARINE_PRIMARY.BLUE);
        gl2.glRotated(90, 1, 0, 0);
        if(rotateRight) {
            gl2.glRotated(propellerRotation, 0, 1, 0);
        } else {
            gl2.glRotated(-propellerRotation, 0, 1, 0);
        }
        gl2.glScaled(radius/5, radius/8, 1);
        glu.gluSphere(quadric, 2*radius/3, 20, 20);
        gl2.glPopMatrix();
    }
}

private class SubmarineSail extends SubmarineComponent {

    SubmarineSail(double radius, double height, ROTATION_AXIS axis) {
        super(radius, height, axis);
    }

    @Override
    void drawNode (GL2 gl2, GLU glu, GLUquadric quadric, boolean filled) {
        gl2.glPushMatrix();
        gl2.glColor3f(SUBMARINE_PRIMARY.RED,SUBMARINE_PRIMARY.GREEN,SUBMARINE_PRIMARY.BLUE);
        gl2.glScaled(radius, height, height);
        glu.gluCylinder(quadric, radius, radius/1.5f, height*2,4, 4);
        gl2.glPopMatrix();
    }
}
}
