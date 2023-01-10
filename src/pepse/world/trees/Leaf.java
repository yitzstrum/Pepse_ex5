package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Component;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

public class Leaf extends Block {
    private static final float TRANSITION_ANGLE = 10f;
    private static final float TRANSITION_TIME = 1.5f;
    private static final float FALLING_TRANSITION = 50f;
    private static final float DIMENSION_CHANGE = 10f;
    private static final float LEAF_RAND_PARAM = 8f;
    private static final float FADEOUT_TIME = 8;
    private static final int LEAF_WIND_RANGE = 400;
    private static final int MAX_LEAF_LIFE = 40;
    private static final int LEAF_Y_VELOCITY = 50;
    private static final int LEAF_MASS = 1;
    private static final int LEAF_X_PARAM = 10;
    private static final int LEAF_X_RANGE = 12;





    private final Vector2 initialTopLeftCorner;
    private Vector2 dimensions;
    private final Random leafWaitTime;


    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable){
        super(topLeftCorner, renderable);
        initialTopLeftCorner = topLeftCorner;
        this.dimensions = dimensions;
        leafWaitTime = new Random();
        physics().setMass(LEAF_MASS);
        float windWaitTime = leafWaitTime.nextInt(LEAF_WIND_RANGE) / LEAF_RAND_PARAM;
        new ScheduledTask(this, windWaitTime, false, this::setLeafRotateAndSize);
        startLife();
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
        if (this.getVelocity().y() > 0){
            this.transform().setTopLeftCornerX((float) (initialTopLeftCorner.x() +
                    Math.sin(this.getCenter().y() / LEAF_X_PARAM) * LEAF_X_RANGE));
        }
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

    private void startLife(){
        resetLeaf();
        float leafLifeTime = (float) (Math.random() * MAX_LEAF_LIFE);
        new ScheduledTask(this, leafLifeTime, false, this::lifeCycle);
    }

    private void lifeCycle(){
        this.transform().setVelocityY(LEAF_Y_VELOCITY);
        this.renderer().fadeOut(FADEOUT_TIME, this::deathScheduledTask);
    }

    private void deathScheduledTask(){
        float leafDeathTime = (float) (Math.random() * MAX_LEAF_LIFE);
        new ScheduledTask(this, leafDeathTime, false, this::startLife);
    }




}
