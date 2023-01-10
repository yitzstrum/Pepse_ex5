package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The PepseGameManager class which manages the Pepse game
 */
public class PepseGameManager extends GameManager {
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = SKY_LAYER + 1;
    private static final int HALO_LAYER = SUN_LAYER + 1;
    private static final int SEED = 200;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int UPPER_GROUND_LAYER = GROUND_LAYER + 1;
    private static final int STEMS_LAYER = UPPER_GROUND_LAYER + 1;
    private static final int LEAVES_LAYER = STEMS_LAYER + 1;
    private static final int GROUND_GAME_OBJECTS_LAYER = LEAVES_LAYER + 1;
    private static final int NIGHT_LAYER = GROUND_GAME_OBJECTS_LAYER + 1;
    private static final int CYCLE_LENGTH = 30;
    private static final float LEFT_EDGE_ADD_ON = -0.5f;
    private static final float RIGHT_EDGE_ADD_ON = 1.5f;
    private static final String GROUND_TAG = "ground";
    private static final String UPPER_GROUND_TAG = "upper ground";
    private static final String STEM_TAG = "stem";
    private static final String LEAF_TAG = "leaf";
    private static final String WINDOW_TITLE = "Pepse";
    private static final int WINDOW_DIMENSION_X = 1800;
    private static final int WINDOW_DIMENSION_Y = 1000;

    private float avatarXCoord;
    private float leftScreenEdge;
    private float rightScreenEdge;
    private Terrain terrain;
    private Tree tree;
    private Avatar avatar;
    private WindowController windowController;

    /**
     * Constructor for the PepseGameManager class
     * @param windowTitle The title for the game window
     * @param windowDimensions pixel dimensions for game window height x width
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * The initialize game function overrides the function from game manager and is in charge of initializing all of
     * the game objects
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from disk.
     * @param inputListener    Contains a single method: isKeyPressed,
     *                         which returns whether a given key is currently pressed by the user or not.
     * @param windowController Contains an array of helpful, self explanatory methods concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        leftScreenEdge = windowController.getWindowDimensions().x() * LEFT_EDGE_ADD_ON;
        rightScreenEdge = windowController.getWindowDimensions().x() * RIGHT_EDGE_ADD_ON;
        initializeGameObjects((int) leftScreenEdge, (int) rightScreenEdge);
        Vector2 initLocation = new Vector2(windowController.getWindowDimensions().x() / 2,
                terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) - 300);
        initializeAvatar(inputListener, imageReader, initLocation);
        Vector2 deltaRelative = new Vector2(windowController.getWindowDimensions().mult(0.5f).subtract(initLocation));
        setCamera(new Camera(avatar, deltaRelative,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        setGameLayerCollisions();
    }

    /**
     * the main update function for the game, overrides the update from the game manager
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation of this method.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float movement = avatarXCoord - avatar.getTopLeftCorner().x();
        if (Math.abs(movement) < windowController.getWindowDimensions().x() / 2)
            return;

        // Avatar moved right
        if (movement < 0) {
            clearObjectsInRange((int) (leftScreenEdge + movement), (int) leftScreenEdge);
            terrain.createInRange((int) rightScreenEdge, (int) (rightScreenEdge - movement));
            tree.createInRange((int) rightScreenEdge, (int) (rightScreenEdge - movement));
            leftScreenEdge -= movement;
            rightScreenEdge -= movement;
        }

        // Avatar moved left
        else if (movement > 0) {
            clearObjectsInRange((int) rightScreenEdge, (int) (rightScreenEdge + movement));
            terrain.createInRange((int) (leftScreenEdge - movement), (int) (leftScreenEdge));
            tree.createInRange((int) (leftScreenEdge - movement), (int) (leftScreenEdge));
            leftScreenEdge -= movement;
            rightScreenEdge -= movement;
        }
        avatarXCoord = avatar.getTopLeftCorner().x();
    }

    /**
     * The function initializes the game object
     * @param leftScreenEdge The left screen edge for the create in range function
     * @param rightScreenEdge The right screen edge for the create in range function
     */
    private void initializeGameObjects(int leftScreenEdge, int rightScreenEdge) {
        initializeSky();
        initializeNightAndSun();
        initializeTerrain(leftScreenEdge, rightScreenEdge);
        initializeTree(leftScreenEdge, rightScreenEdge);
    }

    /**
     * The function initializes the game sky
     */
    private void initializeSky() {
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
    }

    /**
     * The function initializes the night and sun
     */
    private void initializeNightAndSun() {
        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(), HALO_LAYER, sun, HALO_COLOR);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
    }

    /**
     * The function initializes the terrain
     * @param leftScreenEdge The left screen edge for the create in range function
     * @param rightScreenEdge The right screen edge for the create in range function
     */
    private void initializeTerrain(int leftScreenEdge, int rightScreenEdge) {
        terrain = new Terrain(gameObjects(), GROUND_LAYER, UPPER_GROUND_LAYER,
                windowController.getWindowDimensions(), SEED);
        terrain.createInRange(leftScreenEdge, rightScreenEdge);
    }

    /**
     * The function initializes the tree
     * @param leftScreenEdge The left screen edge for the create in range function
     * @param rightScreenEdge The right screen edge for the create in range function
     */
    private void initializeTree(int leftScreenEdge, int rightScreenEdge) {
        tree = new Tree(gameObjects(), STEMS_LAYER, terrain::groundHeightAt, SEED);
        tree.createInRange(leftScreenEdge, rightScreenEdge);
    }

    /**
     * The function initializes the avatar
     * @param inputListener Contains a single method: isKeyPressed,
     *                      which returns whether a given key is currently pressed by the user or not.
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     * @param initLocation initial location
     */
    private void initializeAvatar(UserInputListener inputListener, ImageReader imageReader, Vector2 initLocation){
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                initLocation,
                inputListener, imageReader);
        avatarXCoord = avatar.getTopLeftCorner().x();
    }

    /**
     * The function sets the game layer collisions
     */
    private void setGameLayerCollisions(){
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, STEMS_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, STEMS_LAYER, false);
    }


    /**
     * The function clears all of the objects in the given range
     *
     * @param leftX  The left X coordinate fot the range
     * @param rightX The right X coordinate fot the range
     */
    private void clearObjectsInRange(int leftX, int rightX) {
        Set<GameObject> setToDelete = new HashSet<>();
        for (GameObject gameObject : gameObjects()) {
            float gameObjectX = gameObject.getTopLeftCorner().x();
            if (gameObjectX <= rightX && gameObjectX >= leftX) {
                setToDelete.add(gameObject);
            }
        }
        for (GameObject gameObject : setToDelete) {
            int layer = getObjLayer(gameObject.getTag());
            gameObjects().removeGameObject(gameObject, layer);
        }
    }

    /**
     * The function gets the objects layer according to its tag
     *
     * @param gameObjectTag The game objects tag
     * @return The layer where it object is set
     */
    private int getObjLayer(String gameObjectTag) {
        switch (gameObjectTag) {
            case GROUND_TAG:
                return GROUND_LAYER;
            case UPPER_GROUND_TAG:
                return UPPER_GROUND_LAYER;
            case STEM_TAG:
                return STEMS_LAYER;
            case LEAF_TAG:
                return LEAVES_LAYER;
            default:
                return Layer.DEFAULT;
        }
    }

    /**
     * The main function of the program which start the game
     *
     * @param args The arguments from the commandline
     */
    public static void main(String[] args) {
        new PepseGameManager(WINDOW_TITLE, new Vector2(WINDOW_DIMENSION_X, WINDOW_DIMENSION_Y)).run();
    }
}