package com.webs.potential_software.bubbledrop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Sprite {

	private final String vertexShaderCode =
			"attribute vec2 a_TexCoordinate;" +
			"varying vec2 v_TexCoordinate;" +
			"uniform mat4 uMVPMatrix;" +
			"attribute vec4 vPosition;" +
			"void main() {" +
			"  gl_Position = vPosition * uMVPMatrix;" +
			    "v_TexCoordinate = a_TexCoordinate;" +
			"}";
	private final String fragmentShaderCode =
			"precision mediump float;" +
			"uniform vec4 vColor;" +
			"uniform sampler2D u_Texture;" +
			"varying vec2 v_TexCoordinate;" +
			"void main() {" +
			"gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
			"}";
	
	private final FloatBuffer textureBuffer;
	private final FloatBuffer vertexBuffer;
	private final ShortBuffer indexBuffer;
	private final int mTextureCoordinateDataSize = 2;
	private final int COORDS_PER_VERTEX = 3;
	
	private int textureIndex = 0;
	private int shaderProgram;
	private int mPositionHandle;
	private int vertexStride = COORDS_PER_VERTEX * 4;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mTextureUniformHandle;
	private int mTextureCoordinateHandle;
	private int[] mTextureDataHandle;
	
	private float[] color = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float textureCoords[] = {
			0f,  0f,
            0f, 1f,
            1f, 1f,
            1f,  0f
	};
	private float vertices[] = {
			-1f, 1f, 0,
			-1f, -1f, 0,
			1f, -1f, 0,
			1f, 1f, 0
	};
	private short indices[] = {
			0, 1, 2, 0, 2, 3
	};
	
	public void setTextureCoords(float[] texCoords){
		this.textureCoords = texCoords;
		
		textureBuffer.clear();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
	}
	
	public void setVertexCoords(float[] vertexCoords) {
		this.vertices = vertexCoords;
		
		vertexBuffer.clear();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}
	
	public Sprite(final Context activityContext, int... resourceIds) {
		
		//Prepare Vertex Buffer
		ByteBuffer vBb = ByteBuffer.allocateDirect(vertices.length * 4);
		vBb.order(ByteOrder.nativeOrder());
		vertexBuffer = vBb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		//Prepare Index Buffer
		ByteBuffer iBb = ByteBuffer.allocateDirect(indices.length * 2);
		iBb.order(ByteOrder.nativeOrder());
		indexBuffer = iBb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		//Prepare Texture Buffer
		ByteBuffer tBb = ByteBuffer.allocateDirect(textureCoords.length * 4);
		tBb.order(ByteOrder.nativeOrder());
		textureBuffer = tBb.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		//Prepare Shader Program
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		shaderProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(shaderProgram, vertexShader);
		GLES20.glAttachShader(shaderProgram, fragmentShader);
	    GLES20.glBindAttribLocation(shaderProgram, 0, "a_TexCoordinate");
	    GLES20.glLinkProgram(shaderProgram);
	    
	    //Prepare TextureDataHandles
	    mTextureDataHandle = new int[resourceIds.length];
	    for (int i = 0; i < resourceIds.length; i++) {
	    	mTextureDataHandle[i] = Sprite.loadTexture(activityContext, resourceIds[i]);
	    }
	}
	
	public static int loadTexture(final Context context, final int resourceId)
	{
	    final int[] textureHandle = new int[1];

	    GLES20.glGenTextures(1, textureHandle, 0);

	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	        
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }

	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }

	    return textureHandle[0];
	}
	
	public boolean contains(float glX, float glY) {
		Rectangle rect = new Rectangle(
				vertices[3] + 0.5f, vertices[4] + 0.5f,
				Math.abs(vertices[9]) + Math.abs(vertices[0]),
				Math.abs(vertices[10]) + Math.abs(vertices[1]));
		
		return rect.contains(glX + 0.5f, glY + 0.5f);
	}
	
	public void draw(float[] mvpMatrix) {

		//Add program to OpenGL ES Environment
	    GLES20.glUseProgram(shaderProgram);

	    //Get handle to vertex shader's vPosition member
	    mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

	    //Enable a handle to the triangle vertices
	    GLES20.glEnableVertexAttribArray(mPositionHandle);

	    //Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

	    //Get Handle to Fragment Shader's vColor member
	    mColorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

	    //Set the Color for drawing the triangle
	    GLES20.glUniform4fv(mColorHandle, 1, color, 0);

	    //Set Texture Handles and bind Texture
	    mTextureUniformHandle = GLES20.glGetAttribLocation(shaderProgram, "u_Texture");
	    mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
	    
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle[textureIndex]);
	    GLES20.glUniform1i(mTextureUniformHandle, 0);
	    GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, textureBuffer);
	    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
	    
	    GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	    GLES20.glEnable(GLES20.GL_BLEND);

	    //Get Handle to Shape's Transformation Matrix
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

	    //Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

	    //Draw the triangle
	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

	    //Disable Vertex Array
	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	public static int loadShader(int type, String shaderCode)
	{
	    //Create a Vertex Shader Type Or a Fragment Shader Type (GLES20.GL_VERTEX_SHADER OR GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    //Add The Source Code and Compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	}

	public void setTextureIndex(int i) {
		textureIndex = i;
	}
}

class Rectangle {
	
	private final float mX;
	private final float mY;
	private final float mWidth;
	private final float mHeight;
	
	public Rectangle(float x, float y, float width, float height) {
		this.mX = x;
		this.mY = y;
		this.mWidth = width;
		this.mHeight = height;
	}
	
	public boolean contains(float glX, float glY) {
		
		if(glX > mX && glX < (mWidth - mX)) {
			if(glY > mY && glY < (mHeight - mY)){
				return true;
			}
		}
		return false;
	}
}
