package com.webs.potential_software.bubbledrop;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

public class TestGameScreen extends GameScreen {

	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	
	private int mWidth;
	private int mHeight;
	
	private Sprite background;
	private Sprite overlay;
	
	public TestGameScreen(Context activityContext) {
		super(activityContext);
		
		initialize();
	}

	@Override
	void initialize() {
		
		//Background
		background = new Sprite(this.getActivityContext(), R.drawable.client_grid);
		overlay = new Sprite(this.getActivityContext(), R.drawable.game_frame);
	}

	@Override
	void update() {
		// TODO Auto-generated method stub

	}

	@Override
	void draw() {
		// Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        // Draw
        background.draw(mMVPMatrix);
        overlay.draw(mMVPMatrix);
	}

	@Override
	void surfaceChanged(int width, int height) {
		// Adjust the viewport based on geometry changes,
        // such as screen rotation
		mWidth = width;
        mHeight = height;
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
	}

	@Override
	boolean touchEvent(MotionEvent e) {
		float X = e.getX();
		float Y = e.getY();
		
		float cenX = this.mWidth / 2;
		float cenY = this.mHeight / 2;
		
		float nX = 0, nY = 0;
		
		nX = (X - cenX);
		nY = (Y - cenY);
		
		float glX = nX / cenY;
		float glY = nY / -cenY;
		
		return false;
	}

}
