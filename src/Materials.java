import com.jogamp.opengl.GL2;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 28/05/2017.
 * This class was created to store all of the scenes materials and material settings.
 * The methods within this class can be called to switch between different materials.
 */
class Materials {
    private static final float[] noMaterial = { 0.0f, 0.0f, 0.0f, 1};
    private static final float[] orangeAmbiance = { 0.3f, 0.1f, 0.0f, 1};

    private static final float[] orangeDiffuse = { 1, 0.2f, 0, 1};
    private static final float[] yellowDiffuse = { 1, 0.5f, 0, 1};

    private static final float[] whiteSpecular = {1, 1, 0, 1};

    private static final float noShine[] = {0};
    private static final float highShine[] = {20};

    // Spotlight material
    private static final float[] lightAmbiance = { 1, 0.7f, 0, 0.02f};
    private static final float[] lightEmission = {1, 0.7f, 0, 1};
    private static final float[] lightDiffuse = { 1, 0.7f, 0, 0.02f};

    // Bubble material
    private static final float[] bubbleAmbiance = { 0.4f, 0.65f, 1, 0.3f};
    private static final float[] bubbleDiffuse = { 0.4f, 0.65f, 1, 0.3f};
    private static final float[] bubbleEmission = {0.075f, 0.1f, 0.15f, 0.3f};

    static void setSubmarinePrimaryMaterial(GL2 gl2) {
        setMaterial(gl2, orangeAmbiance, orangeDiffuse, noMaterial, noShine, noMaterial);
    }

    static void setSubmarineSecondaryMaterial(GL2 gl2) {
        setMaterial(gl2, orangeAmbiance, yellowDiffuse, noMaterial, noShine, noMaterial);
    }

    static void setSubmarineLightMaterial(GL2 gl2) {
        setMaterial(gl2, lightAmbiance, lightDiffuse, whiteSpecular, highShine, lightEmission);
    }

    static void setBubbleMaterial(GL2 gl2) {
        setMaterial(gl2, bubbleAmbiance, bubbleDiffuse, whiteSpecular, highShine, bubbleEmission);
    }

    static void clearMaterials(GL2 gl2) {
        setMaterial(gl2, noMaterial, noMaterial, noMaterial, noShine, noMaterial);
    }

    static void setMaterial(GL2 gl2, ColourRGB colours) {
        float[] colour = {colours.RED, colours.GREEN, colours.BLUE, 1};
        setMaterial(gl2, colour, colour, colour, noMaterial, noMaterial);
    }

    private static void setMaterial(GL2 gl2, float[] ambiance, float[] diffuse,
                                    float[] specular, float[] shine, float[] emission) {
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambiance, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shine, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emission, 0);
    }
}
