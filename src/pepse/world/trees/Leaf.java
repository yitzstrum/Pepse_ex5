package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

/**
 * Leaf class extends the Block class and represents a single leaf in the game
 */
public class Leaf extends Block {
    private static final float TRANSITION_ANGLE = 10f;
    private static final float TRANSITION_TIME = 1.5f;
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

    /**
     * Leaf constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object.
     *                   Can be null, in which case the GameObject will not be rendered.
     */
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
     * update function which is in charge of the horizontal movement of the leaf
     * @param deltaTime the update delta time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (this.getVelocity().y() > 0){
            this.transform().setTopLeftCornerX((float) (initialTopLeftCorner.x() +
                    Math.sin(this.getCenter().y() / LEAF_X_PARAM) * LEAF_X_RANGE));
        }
    }

    /**
     * The function is responsible for resetting the leaf to its original parameters
     */
    private void resetLeaf(){
        this.setVelocity(Vector2.ZERO);
        this.setTopLeftCorner(initialTopLeftCorner);
        this.renderer().setOpaqueness(1);
    }

    /**
     * The function calls the rotate function and the scaling function
     */
    private void setLeafRotateAndSize(){
        setRotateTrans();
        setScalingTrans();
    }

    /**
     * The function creates a new transition for rotating the leaf's angle
     */
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

    /**
     * The function creates a new transition for resizing the leaf
     */
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

    /**
     * The function starts the leaf's life cycle and creates a scheduled task for the falling of the leaf
     */
    private void startLife(){
        resetLeaf();
        float leafLifeTime = (float) (Math.random() * MAX_LEAF_LIFE);
        new ScheduledTask(this, leafLifeTime, false, this::lifeCycle);
    }

    /**
     * The function is in charge of the falling of the leaf, one the fadeout time is over we call the death scheduled
     * function
     */
    private void lifeCycle(){
        this.transform().setVelocityY(LEAF_Y_VELOCITY);
        this.renderer().fadeOut(FADEOUT_TIME, this::deathScheduledTask);
    }

    /**
     * The function is in charge of taking care of the leaf once its fadeout time is over and to start the leaf's life
     * once the death time is over
     */
    private void deathScheduledTask(){
        float leafDeathTime = (float) (Math.random() * MAX_LEAF_LIFE);
        new ScheduledTask(this, leafDeathTime, false, this::startLife);
    }




}
