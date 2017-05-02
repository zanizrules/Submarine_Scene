import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	private Origin locator;
	private Grid seaBed, seaSurface;
	private Submarine submarine;
	private double camX, camY, camZ;

	private static final double FIELD_OF_VIEW = 30, NEAR_CLIPPING = 0.1, FAR_CLIPPING = 50;
	static final float SEA_HEIGHT = 10;

	private static final ColourRGB SEABED_COLOUR = new ColourRGB(0.66f,0.47f, 0.37f);
	private static final ColourRGB SEASURFACE_COLOUR = new ColourRGB(0.13f,0.7f, 0.67f, 0.5f);

	private boolean filled;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// Ensure matrix mode is set to model view
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// Set viewpoint depending on user input

		// Setup camera
		camX = submarine.x - 6 * Math.sin(Math.toRadians(submarine.submarineRotation));
		camY = submarine.y+3;
		camZ = submarine.z - 6 * Math.cos(Math.toRadians(submarine.submarineRotation));
		glu.gluLookAt(camX, camY, camZ, // Camera positioned behind the submarine
				submarine.x, submarine.y, submarine.z, // Focus on the centre of the submarine
				0.0, 1.0, 0.0);

		// Draw origin locator
		locator.draw(gl, glu, quadric, filled);

		// Set up rendering style
		int style = filled ? GLU.GLU_FILL : GLU.GLU_LINE;
		glu.gluQuadricDrawStyle(quadric, style);

		// Draw everything here
		gl.glEnable(GL_BLEND); // Blending required for Sea surface
		gl.glBlendFunc(GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		seaBed.draw(gl, glu, quadric, filled);
		submarine.draw(gl, glu, quadric, filled);
		seaSurface.draw(gl, glu, quadric, filled);

		gl.glDisable(GL_BLEND);
		gl.glFlush();
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
		locator = new Origin();
		submarine = new Submarine(1);
		seaBed = new Grid(SEABED_COLOUR, -SEA_HEIGHT/2);
		seaSurface = new Grid(SEASURFACE_COLOUR, SEA_HEIGHT/2);

		// Enable lighting
		lights(gl);

		// Setup the drawing area and shading mode
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	private void lights(GL2 gl) {
		float ambient[] = { 0, 0, 0, 1 };
		float diffuse[] = { 1, 1, 1, 1 };
		float specular[] = { 1, 1, 1, 1 };
		float position0[] = { 1, 1, 1, 0 };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);
		float position1[] = { -1, -1, -1, 0 };
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position1, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
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
		System.out.println("W: Move forward");
		System.out.println("S: Move backward");
		System.out.println("A: Turn left");
		System.out.println("D: Turn right\n");

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

		frame.setSize(750, 750);
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
			submarine.changeState(SUBMARINE_STATE.TURNING_LEFT);
		} else if(key == KeyEvent.VK_D) {
			submarine.changeState(SUBMARINE_STATE.TURNING_RIGHT);
		} else if (key == KeyEvent.VK_W) {
			submarine.changeState(SUBMARINE_STATE.MOVING_FORWARD);
		} else if (key == KeyEvent.VK_S) {
			submarine.changeState(SUBMARINE_STATE.MOVING_BACKWARD);
		} else if(key == KeyEvent.VK_UP) {
			submarine.changeState(SUBMARINE_STATE.SURFACING);
		} else if(key == KeyEvent.VK_DOWN) {
			submarine.changeState(SUBMARINE_STATE.DIVING);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if(key == KeyEvent.VK_L) {
			toggleRenderingStyle();
		} else if(key == KeyEvent.VK_I) {
			printInformation();
		} else if (key == KeyEvent.VK_A | key == KeyEvent.VK_D | key == KeyEvent.VK_W | key == KeyEvent.VK_S
				| key == KeyEvent.VK_UP | key == KeyEvent.VK_DOWN) {
			submarine.changeState(SUBMARINE_STATE.IDLE);
		}
	}
}
