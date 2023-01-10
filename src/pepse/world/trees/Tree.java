package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Tree class, the class is responsible for creating and managing the trees
 */
public class Tree {
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final String STEM_TAG = "stem";
    private static final String LEAF_TAG = "leaf";
    private static final int RANDOM_RANGE = 12;
    private static final int RANDOM_PARAM = 0;
    private static final int LEAF_RANDOM_RANGE = 10;
    private static final int LEAF_RANDOM_PARAM = 1;
    private static final int MIN_STEM_LEN = 8;
    private static final int STEM_RANDOM_RANGE = 4;

    private final GameObjectCollection gameObjects;
    private final int layer;
    private final Function<Float, Float> groundHeightAt;
    private final int seed;

    /**
     * Tree constructor
     * @param gameObjects The game object collection
     * @param layer The layer in which we create the tree
     * @param groundHeightAt callable for calculating the height at a given X coordinate
     * @param seed seed for random
     */
    public Tree(GameObjectCollection gameObjects, int layer,
                Function<Float, Float> groundHeightAt, int seed){
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.groundHeightAt = groundHeightAt;
        this.seed = seed;
    }

    /**
     * Creates trees in the given range according to the bool function shouldPlantTree
     * @param minX the min X coordinate
     * @param maxX the max X coordinate
     */
    public void createInRange(int minX, int maxX){
        for(int currX = roundMinX(minX); currX <= maxX; currX += Block.SIZE){
            if (shouldPlantTree(currX)){
                createTree(currX);
            }
        }
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

    /**
     * A boolean function that decides if we should plant the tree according to a random function
     * @param x The X coordinate we want to plant the tree in
     * @return true if we should plant the tree in the X coordinate, false otherwise
     */
    private boolean shouldPlantTree(int x){
        Random plantTreeRandom = new Random(Objects.hash(x, seed));
        return plantTreeRandom.nextInt(RANDOM_RANGE) == RANDOM_PARAM;
    }

    /**
     * The function is in charge of creating the tree (stem and leaves) in the X coordinate.
     * @param x The X coordinate we want to plant the tree in
     */
    private void createTree(int x){
        float floatTreeBase = groundHeightAt.apply((float) x);
        int treeBase = (int) (Math.floor(floatTreeBase / Block.SIZE) * Block.SIZE);
        treeBase -= Block.SIZE;
        Random stemRandom = new Random(x);
        int stemSize = stemRandom.nextInt(STEM_RANDOM_RANGE) + MIN_STEM_LEN;
        createTreeStem(x, treeBase, stemSize);
        createLeaves(x, treeBase - (stemSize * Block.SIZE), stemSize);
    }

    /**
     * The function is in charge of creating the tree stem in the X coordinate.
     * @param x The X coordinate we want to create the stem in
     * @param stemTop The top of the stem
     * @param stemSize The size of the stem
     */
    private void createTreeStem(int x, int stemTop, int stemSize){
        int y = stemTop;
        for (int i = 0; i < stemSize; ++i){
            GameObject stemBlock = new Block(
                    new Vector2(x, y),
                    new RectangleRenderable(ColorSupplier.approximateColor(STEM_COLOR)));
            stemBlock.setTag(STEM_TAG);
            gameObjects.addGameObject(stemBlock, layer);
            y -= Block.SIZE;
        }
    }

    /**
     * The function is in charge of creating the tree leaves in the X coordinate.
     * @param x The X coordinate we want to create the leaves in
     * @param stemTop The top of the stem
     * @param stemSize The size of the stem
     */
    private void createLeaves(int x, int stemTop, int stemSize){
        Random leavesRandom = new Random(x);
        int leavesRatio = roundMinX(stemSize * Block.SIZE / 3);
        for (int i = stemTop - leavesRatio; i <= stemTop + leavesRatio; i += Block.SIZE){
            for (int j = x - leavesRatio; j <= x + leavesRatio; j += Block.SIZE){
                if (leavesRandom.nextInt(LEAF_RANDOM_RANGE) > LEAF_RANDOM_PARAM){
                    createLeaf(j, i);
                }
            }
        }
    }

    /**
     * The function is in charge of creating a single leaf
     * @param x The X coordinate on the screen where we create the leaf
     * @param y The Y coordinate on the screen where we create the leaf
     */
    private void createLeaf(int x, int y){
        GameObject leaf = new Leaf(
                new Vector2(x, y),
                new Vector2(Block.SIZE, Block.SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        leaf.setTag(LEAF_TAG);
        gameObjects.addGameObject(leaf, layer + 1);
    }
}