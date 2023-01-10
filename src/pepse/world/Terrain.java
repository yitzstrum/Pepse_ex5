package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {
    private static final int TERRAIN_DEPTH = 20;
    private static final float X0_HEIGHT_RATIO = (float) (2.0 / 3.0);
    private static final float SCALE_RATIO = (float) (4.0 / 9.0);
    private static final String GROUND_TAG = "ground";
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final NoiseGenerator noiseGenerator;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private int upperGroundLayer;
    private float groundHeightAtX0;
    private float scalingParameter;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, int upperGroundLayer, Vector2 windowDimensions, int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.upperGroundLayer = upperGroundLayer;
        groundHeightAtX0 = windowDimensions.y() * X0_HEIGHT_RATIO;
        noiseGenerator = new NoiseGenerator(seed);
        scalingParameter = windowDimensions.y() * SCALE_RATIO;
    }

    public float groundHeightAt(float x){
        return (float) (groundHeightAtX0 + (scalingParameter * noiseGenerator.noise(0.05 * x)));
    }

    public void createInRange(int minX, int maxX){
        for (int currX = roundMinX(minX); currX <= maxX; currX += Block.SIZE){
            createVerticalTerrain(currX);
        }
    }

    private void createVerticalTerrain(int x){
        int y = (int) (Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE);
        createBlock(x, y, upperGroundLayer);
        y += Block.SIZE;
        for (int i = 1; i < TERRAIN_DEPTH; i++){
            createBlock(x, y, groundLayer);
            y += Block.SIZE;
        }
    }

    private void createBlock(int x, int y, int layer){
        GameObject ground_object = new Block(
                new Vector2(x, y),
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
        ground_object.setTag(GROUND_TAG);
        gameObjects.addGameObject(ground_object, layer);
    }

    private int roundMinX(int minX){
        if (minX < 0){
            return ((minX / Block.SIZE) - 1) * Block.SIZE;
        }
        return (minX / Block.SIZE) * Block.SIZE;
    }
}
