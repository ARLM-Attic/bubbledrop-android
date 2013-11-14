package com.webs.potential_software.bubbledrop;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends Activity {
	
	private GlView mGlView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        GLSurface.setGameScreen(new LaunchScreen(this));
		
		mGlView = new GlView(this);
        mGlView.setEGLContextClientVersion(2);
        mGlView.setRenderer(new GLSurface(this));
        
		setContentView(mGlView);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	mGlView.onResume();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	mGlView.onPause();
    }
}

class GlView extends GLSurfaceView {

	public GlView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return GLSurface.onTouchEvent(e);
		
	}
	
}

class GLSurface implements Renderer {
	
	private static EGLConfig mConfig;
	private static int mWidth;
	private static int mHeight;
	
	private static final String TAG = "GLSurface";
	private Context mActivityContext;
	private static GameScreen mGameScreen;
	
	public GLSurface(Context activityContext) {
		this.mActivityContext = activityContext;
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		mGameScreen.update();
		mGameScreen.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		mWidth = width;
		mHeight = height;
		mGameScreen.surfaceChanged(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		mConfig = config;
		mGameScreen.initialize();
		mGameScreen.surfaceCreated(config);
	}
	
	public Context getActivityContext() {
		return this.mActivityContext;
	}
	
	public static boolean onTouchEvent(MotionEvent e) {
		return mGameScreen.touchEvent(e);
	}
	
	public static void setGameScreen(GameScreen gameScreen) {
		Log.d(TAG, "Setting Game Screen...");
		
		mGameScreen = gameScreen;
		mGameScreen.surfaceCreated(mConfig);
		mGameScreen.surfaceChanged(mWidth, mHeight);
	}
	
}
