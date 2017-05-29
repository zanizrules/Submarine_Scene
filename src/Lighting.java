import com.jogamp.opengl.GL2;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 24/05/2017.
 * This class was made to store all of the scenes lights and lightning settings. This includes the submarines spotlight.
 * The methods within this class are called as needed.
 */
class Lighting {
    static final int SUBMARINE_SPOTLIGHT = GL2.GL_LIGHT2;
    private static final int SUBMARINE_SPOTLIGHT_2 = GL2.GL_LIGHT3;
    private static final float[] SUBMARINE_SPOTLIGHT_DIRECTION = {0, -0.2f, 0};
    private static final float SUBMARINE_SPOTLIGHT_ANGLE = 35;
    private static final float SUBMARINE_SPOTLIGHT_EXPONENT = 5;

    Lighting(GL2 gl) {
        gl.glLightf(SUBMARINE_SPOTLIGHT, GL2.GL_SPOT_CUTOFF, SUBMARINE_SPOTLIGHT_ANGLE); // Set cutoff angle
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_SPOT_DIRECTION, SUBMARINE_SPOTLIGHT_DIRECTION, 0);
        gl.glLightf(SUBMARINE_SPOTLIGHT, GL2.GL_SPOT_EXPONENT, SUBMARINE_SPOTLIGHT_EXPONENT);
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f, 1}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_SPECULAR, new float[]{1, 1, 1, 1}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_AMBIENT, new float[]{0.5f,0.5f,0.5f,0.5f}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_EMISSION, new float[]{1,1,1,1}, 0);

        gl.glLightf(SUBMARINE_SPOTLIGHT_2, GL2.GL_SPOT_CUTOFF, SUBMARINE_SPOTLIGHT_ANGLE); // Set cutoff angle
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_SPOT_DIRECTION, SUBMARINE_SPOTLIGHT_DIRECTION, 0);
        gl.glLightf(SUBMARINE_SPOTLIGHT_2, GL2.GL_SPOT_EXPONENT, SUBMARINE_SPOTLIGHT_EXPONENT);
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f, 1}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_SPECULAR, new float[]{1, 1, 1, 1}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_AMBIENT, new float[]{0.5f,0.5f,0.5f,0.5f}, 0);
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_EMISSION, new float[]{1,1,1,1}, 0);
        gl.glEnable(SUBMARINE_SPOTLIGHT_2);
    }

    void drawSubmarineSpotLight(GL2 gl, float[] positions, float rotationAngle) {
        gl.glLightfv(SUBMARINE_SPOTLIGHT_2, GL2.GL_POSITION, positions, 0); // 0 INDICATES TO START AT POS 0
        positions[0] -= 1f * (float) Math.sin(Math.toRadians(rotationAngle));
        positions[2] -= 1f * (float) Math.cos(Math.toRadians(rotationAngle));
        gl.glLightfv(SUBMARINE_SPOTLIGHT, GL2.GL_POSITION, positions, 0); // 0 INDICATES TO START AT POS 0
        if(positions[1] < -1) {
            gl.glEnable(SUBMARINE_SPOTLIGHT_2);
        } else {
            gl.glDisable(SUBMARINE_SPOTLIGHT_2);
        }
    }

    void enableSceneLighting(GL2 gl) {
            float ambient[] = {0, 0, 0, 1};
            float diffuse[] = {0.05f, 0.05f, 0.05f, 1};
            float specular[] = {0.5f, 0.5f, 0.5f, 1};
            float position0[] = {1, 1, 1, 0};

            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

            enableSunLighting(gl, 0.5f);

            gl.glEnable(GL2.GL_LIGHTING);
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_COLOR_MATERIAL);
    }

    private void enableSunLighting(GL2 gl, float darkness) {
        float ambient[] = {darkness, darkness, darkness, 1};
        float diffuse[] = {darkness, darkness, darkness, 1};
        float specular[] = {0.5f, 0.5f, 0.5f, 1};

        float position1[] = { 0, 8, 0, 0};
        float direction1[] = {0, -1, 0};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position1, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, direction1, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);
    }

    void triggerSunLight(GL2 gl2, boolean turnOn, float darkness) {
        if(turnOn) {
            enableSunLighting(gl2, darkness);
            gl2.glEnable(GL2.GL_LIGHT1);
        } else {
            gl2.glDisable(GL2.GL_LIGHT1);
        }
    }
}
