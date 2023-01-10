package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

/**
 * Terrain class
 */
public class Terrain {
    private static final int TERRAIN_DEPTH = 30;
    private static final float X0_HEIGHT_RATIO = (float) (2.0 / 3.0);
    private static final float SCALE_RATIO = (float) (4.0 / 9.0);
    private static final float NOISE = (float) 0.01;
    private static final String GROUND_TAG = "ground";
    private static final String UPPER_GROUND_TAG = "upper ground";
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    
    private final NoiseGenerator noiseGenerator;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final int upperGroundLayer;
    private final float groundHeightAtX0;
    private final float scalingParameter;

    /**
     * Terrain constructor
     * @param gameObjects The game object collection
     * @param groundLayer The layer in which we create the ground
     * @param upperGroundLayer The layer in which we create the upper ground
     * @param windowDimensions The games window dimensions
     * @param seed seed for random
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, int upperGroundLayer, Vector2 windowDimensions, int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.upperGroundLayer = upperGroundLayer;
        groundHeightAtX0 = windowDimensions.y() * X0_HEIGHT_RATIO;
        noiseGenerator = new NoiseGenerator(seed);
        scalingParameter = windowDimensions.y() * SCALE_RATIO;
    }

    /**
     * The function calculates the height for a given X coordinate using the Perlin noise algorithm
     * @param x The X coordinate for which we calculate the height
     * @return The height (Y coordinate) for the given X coordinate
     */
    public float groundHeightAt(float x){
        return (float) (groundHeightAtX0 + (scalingParameter * noiseGenerator.noise(NOISE * x)));
    }

    /**
     * Creates terrain in the given range
     * @param minX the min X coordinate
     * @param maxX the max X coordinate
     */
    public void createInRange(int minX, int maxX){
        for (int currX = roundMinX(minX); currX <= maxX; currX += Block.SIZE){
            createVerticalTerrain(currX);
        }
    }

    /**
     * Create a vertical set of terrain blocks
     * @param x The X coordinate where we create the blocks
     */
    private void createVerticalTerrain(int x){
        int y = (int) (Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE);
        createBlock(x, y, upperGroundLayer, UPPER_GROUND_TAG);
        y += Block.SIZE;
        for (int i = 1; i < TERRAIN_DEPTH; i++){
            createBlock(x, y, groundLayer, GROUND_TAG);
            y += Block.SIZE;
        }
    }

    /**
     * The function is in charge of creating a single terrain block
     * @param x The X coordinate where we create the block
     * @param y The Y coordinate where we create the block
     * @param layer The layer in which we create the block
     * @param tag The tag for the block
     */
    private void createBlock(int x, int y, int layer, String tag){
        GameObject ground_object = new Block(
                new Vector2(x, y),
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
        ground_object.setTag(tag);
        gameObjects.addGameObject(ground_object, layer);
    }

    /**
     * The function rounds the given X coordinate to match the block size
     * @param minX The X coordinate to round
     * @return returns the rounded X coordinate
     */
    private int roundMinX(int minX){
        if (minX < 0){
            return ((minX / Block.SIZE) - 1) * Block.SIZE;
        }
        return (minX / Block.SIZE) * Block.SIZE;
    }
}
