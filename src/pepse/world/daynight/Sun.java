package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun extends GameObject {

    private static final Color SUN_COLOR = Color.YELLOW;
    private static final String SUN_TAG = "sun";
    private static final float SUN_SIZE = 100f;
    private static final Vector2 SUN_DIMENSION = new Vector2(SUN_SIZE, SUN_SIZE);

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Sun(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }


    /**
     *
     * @param gameObjects
     * @param layer
     * @param windowDimensions
     * @param cycleLength
     * @return
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,  float cycleLength) {

        Renderable sunRenderable = new OvalRenderable(SUN_COLOR);
        GameObject subObj = new GameObject(Vector2.ZERO, SUN_DIMENSION, sunRenderable);
        subObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        subObj.setTag(SUN_TAG);
        gameObjects.addGameObject(subObj, layer);


        float x = (float) ((4 / 2) * Math.PI);


        new Transition<>(
                subObj, // the game object being changed
                (angle) ->  subObj.setCenter(getSunPos(windowDimensions, angle)), // the function that changes the game object
                0f, // initial transition value
                x, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a linear interpolator
                cycleLength , // transtion fully over a day
                Transition.TransitionType.TRANSITION_LOOP, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value

        
        return subObj;
    }

    private static Vector2 getSunPos(Vector2 windowDimensions, float angleInSky) {
        float x = (float) ((windowDimensions.x() / 2) - (windowDimensions.y() - SUN_DIMENSION.y()) / 2 * 2 * Math.sin(angleInSky));
        float y = (float) ((windowDimensions.y() / 2) - (windowDimensions.y() - SUN_DIMENSION.y()) / 2 * Math.cos(angleInSky));
        return new Vector2(x, y);
    }
}
