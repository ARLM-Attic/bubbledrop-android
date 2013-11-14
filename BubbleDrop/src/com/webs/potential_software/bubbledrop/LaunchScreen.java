package com.webs.potential_software.bubbledrop;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;

public class LaunchScreen extends GameScreen {
	
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	
	private Sprite logo;
	private Timer timer;
	
	public LaunchScreen(Context activityContext) {
		super(activityContext);
	}

	@Override
	void initialize() {
		logo = new Sprite(this.getActivityContext(), R.drawable.logo);
	    timer = new Timer(2000L);
	    timer.start();
	}

	@Override
	void update() {
		if(timer.isExpired()) {
			GLSurface.setGameScreen(new MainMenu(this.getActivityContext()));
		}
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
        logo.draw(mMVPMatrix);
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
