package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

public class Leaf extends Block {
    private static final float TRANSITION_ANGLE = 10f;
    private static final float TRANSITION_TIME = 1.5f;
    private static final float DIMENSION_CHANGE = 10f;
    private static final float LEAF_RAND_PARAM = 8f;
    private static final int LEAF_WIND_RANGE = 400;

    private final Vector2 initialTopLeftCorner;
    private Vector2 dimensions;
    private final Random leafWaitTime;

    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable){
        super(topLeftCorner, renderable);
        initialTopLeftCorner = topLeftCorner;
        this.dimensions = dimensions;
        leafWaitTime = new Random();
        float windWaitTime = leafWaitTime.nextInt(LEAF_WIND_RANGE) / LEAF_RAND_PARAM;
        new ScheduledTask(this, windWaitTime, false, this::setLeafRotateAndSize);
    }

    /**
     * Should be called once per frame.
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
//        if (getVelocity().y() > 0){
//            transform().setTopLeftCorner();
//        }
    }

    private void resetLeaf(){
        this.setVelocity(Vector2.ZERO);
        this.setTopLeftCorner(initialTopLeftCorner);
        this.renderer().setOpaqueness(1);
    }

    private void setLeafRotateAndSize(){
        setRotateTrans();
        setScalingTrans();
    }

    private void setRotateTrans(){
        new Transition<>(
                this,
                angle -> this.renderer().setRenderableAngle(angle),
                TRANSITION_ANGLE,
                -TRANSITION_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    private void setScalingTrans(){
        new Transition<>(
                this,
                this::setDimensions,
                dimensions,
                dimensions.add(new Vector2(DIMENSION_CHANGE, DIMENSION_CHANGE)),
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    private void deathScheduledTask(){

    }




}
