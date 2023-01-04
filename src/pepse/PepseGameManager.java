package pepse;
import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;

public class PepseGameManager extends GameManager {

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = SKY_LAYER + 1;
    private static final int HALO_LAYER = SUN_LAYER + 1;

    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int TRUNKS_LAYER = GROUND_LAYER + 1;
    private static final int LEAFS_LAYER = TRUNKS_LAYER + 1;
    private static final int GROUND_GAME_OBJECTS_LAYER = LEAFS_LAYER + 1;
    private static final int NIGHT_LAYER = GROUND_GAME_OBJECTS_LAYER + 1;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), 5);
        Sun.create(gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), 5);
    }

    public static void main(String[] args) {

        new PepseGameManager().run();

    }

}
