package com.webs.potential_software.bubbledrop;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Mike
 * Date: 11/14/13
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestScreen extends GameScreen {

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];

    private Sprite background;
    private Sprite overlay;

    public TestScreen(Context activityContext) {
        super(activityContext);
        initialize();
    }
    @Override
    void initialize() {
        background = new Sprite(getActivityContext(), R.drawable.client_grid);
        overlay = new Sprite(getActivityContext(), R.drawable.game_frame);
    }

    @Override
    void update() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void draw() {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        //Draw Background and Overlay
        background.draw(mMVPMatrix);
        overlay.draw(mMVPMatrix);
    }

    @Override
    void surfaceChanged(int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    void surfaceCreated(EGLConfig config) {
        //Set the background frame color
        GLES20.glClearColor(
                0.3921568627450980392156862745098f,
                0.58431372549019607843137254901961f,
                0.92941176470588235294117647058824f,
                1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    @Override
    boolean touchEvent(MotionEvent e) {
        return false;
    }
}
