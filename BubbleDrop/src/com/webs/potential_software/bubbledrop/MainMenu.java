package com.webs.potential_software.bubbledrop;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

public class MainMenu extends GameScreen {

	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	
	private int mWidth;
	private int mHeight;
	
	private Sprite background;
	private Sprite menuBase;
	private Sprite playButton;
	private boolean pressPlayButton;
	
	public MainMenu(Context activityContext) {
		super(activityContext);
		
		initialize();
	}

	@Override
	void initialize() {
		
		//Background
		background = new Sprite(this.getActivityContext(), R.drawable.background);		
		background.setTextureCoords(new float[] {
				0f, 0f,
	            0f, 5f,
	            5f, 5f,
	            5f, 0f
		});
		
		//Play Button
		playButton = new Sprite(this.getActivityContext(), R.drawable.button_play, R.drawable.button_play_pressed);
		playButton.setVertexCoords(new float[] { //4:1 Ratio (Width:Height)
				-0.4f,  0.1f, 0,
				-0.4f, -0.1f, 0,
				 0.4f, -0.1f, 0,
				 0.4f,  0.1f, 0
		});
		
		//Menu Base
		menuBase = new Sprite(this.getActivityContext(), R.drawable.menu_base);
	}

	@Override
	void update() {
		
		//Animate Background
		long bkdTime = SystemClock.uptimeMillis() % 8000L;
		float bkdInc = (float) (5.0f / 8000L) * bkdTime;
		
		background.setTextureCoords(new float[] {
				0, -bkdInc,
				0, 5.0f - bkdInc,
				5.0f, 5.0f - bkdInc,
				5.0f, -bkdInc
				
		});
		
		//Animate Button
		if(pressPlayButton) {
			playButton.setVertexCoords(new float[] { //4:1 Ratio (Width:Height)
					-0.4f, -0.2f, -0.025f, 	//Top Left
					-0.4f, -0.4f, -0.025f,	//Bottom Left
				 	 0.4f, -0.4f, -0.025f,	//Bottom Right
				 	 0.4f, -0.2f, -0.025f	//Top Right
			});
		} else {
			long bttnTime = SystemClock.uptimeMillis() % 1000L;
			float bttnInc = 0;
		
			if(bttnTime <= 500) {		//pulse out
				bttnInc = (float) (0.05f / 500) * bttnTime;
			} else {					//puse in
				bttnInc = 0.05f - ((float) (0.05f / 500) * (bttnTime - 500));
			}
		
			playButton.setVertexCoords(new float[] { //4:1 Ratio (Width:Height)
					-0.4f, -0.2f, bttnInc, 	//Top Left
					-0.4f, -0.4f, bttnInc,	//Bottom Left
				 	 0.4f, -0.4f, bttnInc,	//Bottom Right
				 	 0.4f, -0.2f, bttnInc	//Top Right
			});
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
        background.draw(mMVPMatrix);
        menuBase.draw(mMVPMatrix);
        playButton.draw(mMVPMatrix);
	}

	@Override
	void surfaceChanged(int width, int height) {
		// Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        
        mWidth = width;
        mHeight = height;
        
        Log.i("MainMenu", "ViewPort: 0, 0, " + String.valueOf(width) + ", " + String.valueOf(height));

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
		
		float X = e.getX();
		float Y = e.getY();
		
		float cenX = this.mWidth / 2;
		float cenY = this.mHeight / 2;
		
		float nX = 0, nY = 0;
		
		nX = (X - cenX);
		nY = (Y - cenY);
		
		float glX = nX / cenY;
		float glY = nY / -cenY;
		
		switch(e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(playButton.contains(glX, glY)) {
				playDown(); return true;
			} break;
			
		case MotionEvent.ACTION_UP:
			if(playButton.contains(glX, glY)) {
				playUp(); return true;
			} break;
			
		case MotionEvent.ACTION_MOVE:
			if(!playButton.contains(glX, glY)) {
				playCancel();
				return true;
			}
			break;
		}
		
		return false;
	}
	
	void playDown() {
		playButton.setTextureIndex(1);
		pressPlayButton = true;
	}
	
	void playCancel() {
		playButton.setTextureIndex(0);
		pressPlayButton = false;
	}
	
	void playUp() {
		playButton.setTextureIndex(0);
		pressPlayButton = false;
		Log.i("MainMenu", "Play Clicked!");
		GLSurface.setGameScreen(new TestGameScreen(this.getActivityContext()));
	}

}


