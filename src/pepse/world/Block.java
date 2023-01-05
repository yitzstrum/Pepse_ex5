package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * The Block class extends the GameObject class, the class is in charge of creating the different objects in the game
 * which are shaped in the block format
 */
public class Block extends GameObject{
    public static final int SIZE = 30;

    /**
     * constructor for the block class
     * @param topLeftCorner The top left corner coordinates of the object
     * @param renderable The renderable representing the object.
     *                   Can be null, in which case the GameObject will not be rendered.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }


}
