package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class for the Night object which extends gameObject
 */
public class Night extends GameObject {

    private static final Color NIGHT_COLOR = Color.BLACK;
    private static final String NIGHT_TAG = "night";
     private static final float MIDNIGHT_OPACITY = 0.5f;

     private static final float HALF_DAY = 2;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Night(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * The function creates a night object
     * @param gameObjects The game object collection
     * @param layer The layer in which we create the night object
     * @param windowDimensions The games window dimensions
     * @param cycleLength The length of the cycle
     * @return returns the night game object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
            float cycleLength) {

        Renderable nightRenderable = new RectangleRenderable(NIGHT_COLOR);
        GameObject nightObj = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
        nightObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(nightObj, layer);
        nightObj.setTag(NIGHT_TAG);
        new Transition<>(
                nightObj, // the game object being changed
                nightObj.renderer()::setOpaqueness, // the method to call
                0f, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength / HALF_DAY, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value
        return nightObj;
    }
}
