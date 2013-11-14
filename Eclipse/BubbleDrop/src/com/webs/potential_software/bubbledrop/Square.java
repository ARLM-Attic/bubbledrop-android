package com.webs.potential_software.bubbledrop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Square {
	
	private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +

	        "attribute vec4 vPosition;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = vPosition * uMVPMatrix;" +
	        "}";
	private final String fragmentShaderCode =
	        "precision mediump float;" +
	        "uniform vec4 vColor;" +
	        "void main() {" +
	        "  gl_FragColor = vColor;" +
	        "}";
	
	private final FloatBuffer vertexBuffer;
	private final ShortBuffer indexBuffer;
	private final int COORDS_PER_VERTEX = 3;
	
	private float[] color = { 0.2f, 0.5f, 0.2f, 1.0f };
	private float vertices[] = {
			0, 0.5f, 0,
			0, 0, 0,
			0.5f, 0, 0,
			0.5f, 0.5f, 0
	};
	private short indices[] = {
			0, 1, 2, 0, 2, 3
	};

	private int shaderProgram;
	private int mPositionHandle;
	private int vertexStride = COORDS_PER_VERTEX * 4;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	
	public Square() {
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
		
		//Prepare Shader Program
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		shaderProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(shaderProgram, vertexShader);
		GLES20.glAttachShader(shaderProgram, fragmentShader);
		GLES20.glLinkProgram(shaderProgram);
	}
	
	public void draw(float[] mvpMatrix) {

		// Add program to OpenGL environment
        GLES20.glUseProgram(shaderProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                              GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable vertex array
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
}
