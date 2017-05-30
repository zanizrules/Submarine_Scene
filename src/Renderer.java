import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.*;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;

/**
 * Author: Shane Birdsall
 * ID: 14870204
 * Date: 25/04/2017.
 * Renderer is the Main class responsible for setting up the view
 * and calling all draw functions through the use of a fps timer.
 */
public class Renderer implements GLEventListener, KeyListener {
	private GLU glu;
	private GLUquadric quadric;
	private Lighting lighting;
	private Origin locator;
	private Grid seaBed, seaSurface;
	private Submarine submarine;
	private double camX, camY, camZ;

	private static final double FIELD_OF_VIEW = 30, NEAR_CLIPPING = 0.1, FAR_CLIPPING = 50;
	static final float SEA_HEIGHT = 10;
	private boolean filled;
	private float dayNightCycle = 0;
	private float timeIncrement = 0.0005f;

	private static final float fogDensity = 0.05f;
	private static final float[] waterColour = new float[]{0, 0.45f, 0.4f};
	private int count = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// Ensure matrix mode is set to model view
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// Set viewpoint depending on user input

		//todo fix
		gl.glClearColor(waterColour[0], waterColour[1], waterColour[2], 0.5f);
		gl.glClear(16640);
		gl.glBlendFunc(770, 771);
		gl.glMatrixMode(5888);

		// Setup camera
		camX = submarine.x - 4.5 * Math.sin(Math.toRadians(submarine.submarineRotation));
		camY = submarine.y + 2.25;
		camZ = submarine.z - 4.5 * Math.cos(Math.toRadians(submarine.submarineRotation));
		glu.gluLookAt(camX, camY, camZ, // Camera positioned behind the submarine
				submarine.x, submarine.y, submarine.z, // Focus on the centre of the submarine
				0.0, 1.0, 0.0);

		// Draw sunlight
		if(dayNightCycle > 0.7f || dayNightCycle < -0.2f) { // 1.1f allows for a longer time to be spent at pitch black
			timeIncrement *= -1;
		} dayNightCycle += timeIncrement; // increment day/night cycle
		lighting.triggerSunLight(gl, submarine.y > 2, dayNightCycle);

		// Draw Sub Spotlight
		float[] spotLightPosition = {0, 0, 0, 1};
		spotLightPosition[0] = submarine.x + 1.5f * (float) Math.sin(Math.toRadians(submarine.submarineRotation));
		spotLightPosition[1] = submarine.y;
		spotLightPosition[2] = submarine.z + 1.5f * (float) Math.cos(Math.toRadians(submarine.submarineRotation));
		lighting.drawSubmarineSpotLight(gl, spotLightPosition);

		// Draw origin locator
		locator.draw(gl, glu, quadric, filled);

		// Set up rendering style
		int style = filled ? GLU.GLU_FILL : GLU.GLU_LINE;
		glu.gluQuadricDrawStyle(quadric, style);

		// Draw everything here
		gl.glEnable(GL_BLEND); // Blending required for Sea surface
		gl.glBlendFunc(GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		seaBed.draw(gl, glu, quadric, filled);

		gl.glDisable(GL2.GL_COLOR_MATERIAL);
			submarine.draw(gl, glu, quadric, filled);
			Materials.clearMaterials(gl);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);

		seaSurface.draw(gl, glu, quadric, filled);

		gl.glDisable(GL_BLEND);
		gl.glFlush();

		count++;
		if(count % 50 == 0) {
			count = 0;
			seaSurface.animateTexture();
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		glu.gluDeleteQuadric(quadric);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Initialise all variables
		glu = new GLU();
		quadric = glu.gluNewQuadric();
		lighting = new Lighting(gl);
		locator = new Origin();
		submarine = new Submarine(1);
		seaBed = new Grid(-SEA_HEIGHT/2, "images/seaFloor");
		seaSurface = new Grid(SEA_HEIGHT/2, "images/PortofTaganrog");

		// Enable lighting
		lighting.enableSceneLighting(gl);

		// Setup the drawing area and shading mode
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);

		// Enable Fog
		setUpFog(gl, fogDensity, waterColour);
	}

	private void setUpFog(GL2 gl, float fogDensity, float[] colour) {
		gl.glEnable(GL2.GL_FOG);
		gl.glFogfv(GL2.GL_FOG_COLOR, colour, 0);
		gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP2);
		gl.glFogf(GL2.GL_FOG_DENSITY, fogDensity);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		height = (height == 0) ? 1 : height; // prevent divide by zero

		// Set the viewport to cover the new window
		gl.glViewport(0, 0, width, height);

		// Set up the default 2x2x2 orthographic projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(FIELD_OF_VIEW, (double) width/height, NEAR_CLIPPING, FAR_CLIPPING);
		gl.glMatrixMode(GL2.GL_MODELVIEW); // Return to model view matrix
	}

	private static void printControls() {
		System.out.println("---Key Mappings---\n");
		System.out.println("Submarine Controls:");
		System.out.println("UP ARROW: Surface (Decrease Depth)");
		System.out.println("DOWN ARROW: Dive (Increase Depth)");
		System.out.println("W: Move forward with pitch");
		System.out.println("S: Move backward with pitch");
		System.out.println("A: Strafe left with roll");
		System.out.println("D: Strafe right with roll\n");

		System.out.println("Other Controls:");
		System.out.println("L: Toggle Wireframe Mode (On/Off)");
	}

	private void printInformation() {
		System.out.println("\n--- Submarine ---");
		System.out.println("Submarine Location (X, Y, Z): (" + submarine.x + ", " + submarine.y + ", " + submarine.z + ")");
		System.out.println("Submarine Rotation Angle: " + submarine.submarineRotation);
		System.out.println("Submarine Propeller Rotation Angle: " + submarine.propellerRotation);
		System.out.println("\n--- Camera ---");
		System.out.println("Camera Location: (X, Y, Z): (" + camX + ", " + camY + ", " + camZ + ")");
		System.out.println("\n--- Other ---");
		System.out.println("Sea Depth: " + (Math.abs(seaBed.height) + Math.abs(seaSurface.height)));
		System.out.println(filled ? "Wireframe Mode: Off" : "Wireframe Mode: On");
	}

	public static void main(String[] args) {
		Frame frame = new Frame("Submarine Build");
		GLCanvas canvas = new GLCanvas();
		Renderer app = new Renderer();
		canvas.addGLEventListener(app);
		canvas.addKeyListener(app);
		canvas.setFocusable(true);

		frame.add(canvas);

		frame.setSize(700, 700);
		final FPSAnimator animator = new FPSAnimator(canvas, 60);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to make sure the call to Animator.stop()
				// completes before exiting.
				new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
			}
		});
		// Center frame
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Maintain 1:1 ratio
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				Component c = (Component)evt.getSource();
				c.setSize(c.getWidth(), c.getWidth());
				frame.setSize(frame.getWidth(), frame.getWidth());
			}
		});

		animator.start();
		printControls();
	}

	private void toggleRenderingStyle() {
		filled = !filled;
	}

	@Override
	public void keyTyped(KeyEvent ignored) {}
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_A) {
			submarine.changeTurningState(SUBMARINE_STATE.TURNING_LEFT);
		} else if(key == KeyEvent.VK_D) {
			submarine.changeTurningState(SUBMARINE_STATE.TURNING_RIGHT);
		} else if (key == KeyEvent.VK_W) {
			submarine.changeHorizontalMovementState(SUBMARINE_STATE.MOVING_FORWARD);
		} else if (key == KeyEvent.VK_S) {
			submarine.changeHorizontalMovementState(SUBMARINE_STATE.MOVING_BACKWARD);
		} else if(key == KeyEvent.VK_UP) {
			submarine.changeVerticalMovementState(SUBMARINE_STATE.SURFACING);
		} else if(key == KeyEvent.VK_DOWN) {
			submarine.changeVerticalMovementState(SUBMARINE_STATE.DIVING);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if(key == KeyEvent.VK_L) {
			toggleRenderingStyle();
		} else if(key == KeyEvent.VK_I) {
			printInformation();
		} else if (key == KeyEvent.VK_A | key == KeyEvent.VK_D) {
			submarine.changeTurningState(SUBMARINE_STATE.IDLE);
		} else if(key == KeyEvent.VK_W | key == KeyEvent.VK_S) {
			submarine.changeHorizontalMovementState(SUBMARINE_STATE.IDLE);
		} else if(key == KeyEvent.VK_UP | key == KeyEvent.VK_DOWN) {
			submarine.changeVerticalMovementState(SUBMARINE_STATE.IDLE);
		}
	}
}
