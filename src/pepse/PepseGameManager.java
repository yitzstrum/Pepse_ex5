package pepse;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {

    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = SKY_LAYER + 1;
    private static final int HALO_LAYER = SUN_LAYER + 1;
    private static final int SEED = 200;


    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int TRUNKS_LAYER = GROUND_LAYER + 1;
    private static final int LEAFS_LAYER = TRUNKS_LAYER + 1;
    private static final int GROUND_GAME_OBJECTS_LAYER = LEAFS_LAYER + 1;
    private static final int NIGHT_LAYER = GROUND_GAME_OBJECTS_LAYER + 1;


    private static final int CYCLE_LENGTH = 30;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(), HALO_LAYER, sun, HALO_COLOR);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Terrain terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS, windowController.getWindowDimensions(), SEED);
        terrain.createInRange(0, (int)(windowController.getWindowDimensions().x()));
        Tree tree = new Tree(gameObjects(), TRUNKS_LAYER, windowController.getWindowDimensions(), terrain::groundHeightAt, SEED);
        tree.createInRange(0, (int)(windowController.getWindowDimensions().x()));


    }

    public static void main(String[] args) {

        new PepseGameManager().run();

    }

}