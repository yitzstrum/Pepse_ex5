package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class for the Sun halo object which extends gameObject
 */
public class SunHalo extends GameObject {

    private static final String HALO_TAG = "halo";
    private static final float HALO_SIZE = 1.3f;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public SunHalo(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * The function creates a sun halo object
     * @param gameObjects The game object collection
     * @param layer The layer in which we create the sun halo object
     * @param sun The sun for which we create the halo
     * @param color The color of the sun halo
     * @return The sun halo game object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun,
                                    Color color) {

        Renderable haloRenderable = new OvalRenderable(color);
        GameObject haloObj = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(HALO_SIZE),
                haloRenderable);
        haloObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        haloObj.setTag(HALO_TAG);
        gameObjects.addGameObject(haloObj, layer);
        return haloObj;
    }
}
