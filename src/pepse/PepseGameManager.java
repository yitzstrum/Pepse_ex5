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

public class PepseGameManager extends GameManager {

    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = SKY_LAYER + 1;
    private static final int HALO_LAYER = SUN_LAYER + 1;
    private static final int SEED = 200;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int UPPER_GROUND_LAYER = GROUND_LAYER + 1 ;
    private static final int STEMS_LAYER = UPPER_GROUND_LAYER + 1;
    private static final int LEAVES_LAYER = STEMS_LAYER + 1;
    private static final int GROUND_GAME_OBJECTS_LAYER = LEAVES_LAYER + 1;
    private static final int NIGHT_LAYER = GROUND_GAME_OBJECTS_LAYER + 1;
    private static final int CYCLE_LENGTH = 30;
    private static final String GROUND_TAG = "ground";
    private static final String STEM_TAG = "stem";
    private static final String LEAF_TAG = "leaf";
    private float avatarXCoord;
    private float leftEdge;
    private float rightEdge;
    private Terrain terrain;
    private Tree tree;

    private Avatar avatar;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        leftEdge = 0;
        rightEdge = (float) (windowController.getWindowDimensions().x() * 1.1);
        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(), HALO_LAYER, sun, HALO_COLOR);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        terrain = new Terrain(gameObjects(), GROUND_LAYER, UPPER_GROUND_LAYER, windowController.getWindowDimensions(), SEED);
        terrain.createInRange((int) leftEdge, (int) rightEdge);
        tree = new Tree(gameObjects(), STEMS_LAYER, windowController.getWindowDimensions(), terrain::groundHeightAt, SEED);
        tree.createInRange((int) leftEdge, (int) rightEdge);

        Vector2 initLocation = new Vector2(windowController.getWindowDimensions().x() / 2,
                terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) - 300);


        avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                initLocation,
                inputListener, imageReader);
        avatarXCoord = avatar.getTopLeftCorner().x();

        Vector2 deltaRelative = new Vector2(windowController.getWindowDimensions().mult(0.5f).subtract(initLocation));
        setCamera(new Camera(avatar, deltaRelative,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, STEMS_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, STEMS_LAYER, false);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        System.out.println(avatar.getAvatarPos());

        float movement = avatarXCoord - avatar.getTopLeftCorner().x();
        // Avatar moved right
        if (movement < 0){
            clearObjectsInRange((int) leftEdge, (int) (leftEdge - movement));
            terrain.createInRange((int) rightEdge, (int) (rightEdge - movement));
            tree.createInRange((int) rightEdge, (int) (rightEdge - movement));
            leftEdge -= movement;
            rightEdge -= movement;
        }

        // Avatar moved left
        else if (movement > 0){
            clearObjectsInRange((int) (rightEdge - movement), (int) rightEdge);
            terrain.createInRange((int) (leftEdge - movement), (int) (leftEdge));
            tree.createInRange((int) (leftEdge - movement), (int) (leftEdge));
            leftEdge -= movement;
            rightEdge -= movement;
        }

        avatarXCoord = avatar.getTopLeftCorner().x();
    }

    private void clearObjectsInRange(int leftX, int rightX){
        for (GameObject gameObject: gameObjects()){
            float gameObjectX = gameObject.getTopLeftCorner().x();
            if (gameObjectX <= rightX && gameObjectX >= leftX){
                int layer = getObjLayer(gameObject.getTag());
                gameObjects().removeGameObject(gameObject, layer);
            }
        }
    }

    private int getObjLayer(String gameObjectTag){
        switch (gameObjectTag){
            case GROUND_TAG:
                return GROUND_LAYER;
            case STEM_TAG:
                return STEMS_LAYER;
            case LEAF_TAG:
                return LEAVES_LAYER;
            default:
                return Layer.DEFAULT;
        }
    }

    public static void main(String[] args) {

        new PepseGameManager().run();

    }

}