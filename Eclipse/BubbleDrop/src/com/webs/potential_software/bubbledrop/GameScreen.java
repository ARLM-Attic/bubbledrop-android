package com.webs.potential_software.bubbledrop;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.view.MotionEvent;

public abstract class GameScreen {

	private Context mActivityContext;
	
	public GameScreen(Context activityContext) {
		this.mActivityContext = activityContext;
	}
	
	public Context getActivityContext() {
		return this.mActivityContext;
	}
	
	abstract void initialize();
	abstract void update();
	abstract void draw();
	abstract void surfaceChanged(int width, int height);
	abstract void surfaceCreated(EGLConfig config);
	abstract boolean touchEvent(MotionEvent e);

}
