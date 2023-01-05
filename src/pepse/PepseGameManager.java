package pepse;
import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;

public class PepseGameManager extends GameManager {

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = SKY_LAYER + 1;
    private static final int HALO_LAYER = SUN_LAYER + 1;
    private static final int SEED = 200;

    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int TRUNKS_LAYER = GROUND_LAYER + 1;
    private static final int LEAFS_LAYER = TRUNKS_LAYER + 1;
    private static final int GROUND_GAME_OBJECTS_LAYER = LEAFS_LAYER + 1;
    private static final int NIGHT_LAYER = GROUND_GAME_OBJECTS_LAYER + 1;

    /**
     * The method will be called once when a GameGUIComponent is created,
     * and again after every invocation of windowController.resetGame().
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), 5);
        Sun.create(gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), 5);
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Terrain terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS, windowController.getWindowDimensions(), SEED);
        terrain.createInRange(0, (int)(windowController.getWindowDimensions().x()));
    }

    public static void main(String[] args) {

        new PepseGameManager().run();

    }

}