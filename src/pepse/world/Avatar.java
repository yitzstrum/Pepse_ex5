package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * Avatar class which extends the Game object
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float JUMP_VELOCITY_Y = -350;
    private static final float FLY_VELOCITY_Y = -450;
    private static final float GRAVITY = 600;
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(80, 80);
    private static final int INIT_ENERGY = 100;
    private static final int RUN_ANIMATION_SPEED = 5;
    private static final int NUM_OF_WAIT_RENDERABLES = 10;
    private static final int NUM_OF_FLY_RENDERABLES = 16;
    private final Renderable[] runRenderables;
    private final Renderable[] waitRenderables;
    private final Renderable[] flyRenderables;
    private static final int ZERO_ENERGY = 0;
    private static final float ENERGY_FACTOR = 0.5f;
    private static final int MAX_ENERGY = 100;
    private static final int NUM_OF_RUN_RENDERABLES = 8;
    private static final String RUN_RENDERABLES_PATH = "src/assets/Run/Run<num>.png";
    private static final String WAIT_RENDERABLES_PATH = "src/assets/Wait/Idle<num>.png";
    private static final String FLY_RENDERABLES_PATH = "src/assets/Fly/Jump<num>.png";
    private static final String NUM_FORMAT = "<num>";
    private static final boolean LEFT_DIRECTION = true;
    private static final boolean RIGHT_DIRECTION = false;

    private float energy;
    private Status status;
    private int updateFrames;
    private int counter;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;

    /**
     * Called on the first frame of a collision.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals("upper ground"))
        setVelocity(Vector2.ZERO);
    }

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.energy = INIT_ENERGY;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.counter = 0;
        this.updateFrames = 0;
        this.status = Status.WAIT;
        this.runRenderables = loadRennderAssets(RUN_RENDERABLES_PATH, NUM_OF_RUN_RENDERABLES);
        this.waitRenderables = loadRennderAssets(WAIT_RENDERABLES_PATH, NUM_OF_WAIT_RENDERABLES);
        this.flyRenderables = loadRennderAssets(FLY_RENDERABLES_PATH, NUM_OF_FLY_RENDERABLES);
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * The function creates an avatar instance
     * @param gameObjects The game object collection
     * @param layer The layer in which we create the avatar object
     * @param topLeftCorner Location where we put the avatar
     * @param inputListener User input listener
     * @param imageReader Reads the images
     * @return The avatar which was created
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS, imageReader.readImage("src" +
                "/assets/Wait/Idle1.png", true), inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * The update function overridden from game object
     * @param deltaTime update delta time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        xVel += goDirectionHandler(KeyEvent.VK_LEFT, LEFT_DIRECTION);
        xVel += goDirectionHandler(KeyEvent.VK_RIGHT, RIGHT_DIRECTION);
        transform().setVelocityX(xVel);
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            transform().setVelocityY(JUMP_VELOCITY_Y);
        }
        energyHandler();
        flyHandler();
        updateAvatar();
    }

    /**
     * Handles the avatars energy
     */
    private void energyHandler() {
        // add energy if the avatar in reset
        if (getVelocity().x() == ZERO_ENERGY && getVelocity().y() == 0 && energy < MAX_ENERGY) {
            energy += ENERGY_FACTOR;
        }
    }

    /**
     * Handles the avatar flying capability
     */
    private void flyHandler() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && energy > ZERO_ENERGY) {
            energy -= ENERGY_FACTOR;
            transform().setVelocityY(FLY_VELOCITY_Y / 2);
            status = Status.FLY;
        }
    }

    /**
     * Handles the avatar direction
     * @param keyEvent The press of a key event
     * @param direction The avatars direction
     * @return The direction in which the avatar should move
     */
    private float goDirectionHandler(int keyEvent, boolean direction) {
        if (inputListener.isKeyPressed(keyEvent)) {
            status = Status.RUN;
            renderer().setIsFlippedHorizontally(direction);
            return direction ? -VELOCITY_X : VELOCITY_X;
        }
        return 0;
    }

    /**
     * The function handles the avatar renders
     * @param renderables The different avatar renderables
     */
    void rendersHandler(Renderable[] renderables) {
        Renderable img;
        if (counter < renderables.length) {
            img = renderables[counter];
            renderer().setRenderable(img);
            counter++;
        } else {
            counter = 0;
            status = Status.WAIT;
        }
    }

    /**
     * updates the avatar according to the different scenarios
     */
    private void updateAvatar() {
        updateFrames++;
        if ((updateFrames % RUN_ANIMATION_SPEED) != 0) {
            return;
        }
        switch (status) {
            case WAIT:
                rendersHandler(waitRenderables);
                break;
            case RUN:
                rendersHandler(runRenderables);
                break;
            case FLY:
                rendersHandler(flyRenderables);
        }
    }

    /**
     * The function loads the avatars assets
     * @param path The path where we load the assets from
     * @param numOfImg Number of images we load
     * @return renderables
     */
    private Renderable[] loadRennderAssets(String path, int numOfImg) {
        Renderable[] renderables = new Renderable[numOfImg];
        for (int i = 0; i < numOfImg; i++) {
            renderables[i] = imageReader.readImage(path.replace(NUM_FORMAT, String.valueOf(i)),
                    true);
        }
        return renderables;
    }

    /**
     * Enum for the avatar status
     */
    enum Status {
        WAIT, RUN, FLY
    }
}
