package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    private static final String HALO_TAG = "halo";
    private static final float HALO_SIZE = 1.3f;

    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun,
                                    Color color) {

        Renderable haloRenderable = new OvalRenderable(color);
        GameObject haloObj = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(HALO_SIZE), haloRenderable);
        haloObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        haloObj.setTag(HALO_TAG);
        gameObjects.addGameObject(haloObj, layer);

        return haloObj;

    }
}
