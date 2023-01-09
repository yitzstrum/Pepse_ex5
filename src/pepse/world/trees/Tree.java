package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class    Tree {
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final String STEM_TAG = "stem";
    private static final String LEAF_TAG = "leaf";
    private static final Renderable stemRenderable = new RectangleRenderable(ColorSupplier.approximateColor(STEM_COLOR));
    private static final Renderable leafRenderable = new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR));
    private static final int RANDOM_RANGE = 10;
    private static final int RANDOM_PARAM = 0;
    private static final int LEAF_RANDOM_RANGE = 10;
    private static final int LEAF_RANDOM_PARAM = 1;

    private static final int MIN_STEM_LEN = 8;
    private static final int STEM_RANDOM_RANGE = 4;


    private GameObjectCollection gameObjects;
    private int layer;
    private Vector2 windowDimensions;
    private Function<Float, Float> groundHeightAt;
    private int seed;

    public Tree(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                Function<Float, Float> groundHeightAt, int seed){
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.windowDimensions = windowDimensions;
        this.groundHeightAt = groundHeightAt;
        this.seed = seed;
    }

    public void createInRange(int minX, int maxX){
        for(int currX = roundMinX(minX); currX <= maxX; currX += Block.SIZE){
            if (shouldPlantTree(currX)){
                createTree(currX);
            }
        }
    }

    private int roundMinX(int minX){
        if (minX < 0){
            return ((minX / Block.SIZE) - 1) * Block.SIZE;
        }
        return (minX / Block.SIZE) * Block.SIZE;
    }

    private boolean shouldPlantTree(int x){
        Random plantTreeRandom = new Random(Objects.hash(x, seed));
        return plantTreeRandom.nextInt(RANDOM_RANGE) == RANDOM_PARAM;
    }

    private void createTree(int x){
        float floatTreeBase = groundHeightAt.apply((float) x);
        int treeBase = (int) (Math.floor(floatTreeBase / Block.SIZE) * Block.SIZE);
        treeBase -= Block.SIZE;
        Random stemRandom = new Random(x);
        int stemSize = stemRandom.nextInt(STEM_RANDOM_RANGE) + MIN_STEM_LEN;
        createTreeStem(x, treeBase, stemSize);
        createLeaves(x, treeBase - (stemSize * Block.SIZE), stemSize);
    }

    private void createTreeStem(int x, int treeBase, int stemSize){
        int y = treeBase;
        for (int i = 0; i < stemSize; ++i){
            GameObject stemBlock = new Block(new Vector2(x, y), stemRenderable);
            stemBlock.setTag(STEM_TAG);
            gameObjects.addGameObject(stemBlock, layer);
            y -= Block.SIZE;
        }
    }


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

    private void createLeaf(int x, int y){
        GameObject leaf = new Leaf(new Vector2(x, y), new Vector2(Block.SIZE, Block.SIZE), leafRenderable);
        leaf.setTag(LEAF_TAG);
        gameObjects.addGameObject(leaf, layer + 1);
    }
}