package com.janas.airhockey;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.janas.airhockey.util.LoggerConfig;
import com.janas.airhockey.util.MatrixHelper;
import com.janas.airhockey.util.ShaderHelper;
import com.janas.airhockey.util.TextResourceReader;

public class AirHockeyRenderer implements Renderer {
    
    private static final String TAG = "AirHokeyRenderer";
    
    private static final int POSITION_COMPONENT_COUNT = 4;	// X Y Z W
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final FloatBuffer mVertexData;
    private final Context mContext;
    
    private int mProgram;
    private int mAPositionLocation;
    private int mAColorLocation;
    private int mUMatrixLocation;
    
    
    
    public AirHockeyRenderer(Context context) {
        
        this.mContext = context;
        
        float[] tableVerticesWithTriangles = {                
                
                // coordinates order: X, Y, Z, W, R, G, B
                // triangle fan
                
                
                // blended_value = (vertex_0_value * (100% – distance_ratio)) + (vertex_1_value * distance_ratio)
                
             // Triangle Fan
                0f,    0f, 0f, 1.5f,  1f,   1f,   1f,         
             -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,            
              0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
              0.5f,  0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
             -0.5f,  0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
             -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,

             // Line 1
             -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
              0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

             // Mallets
             0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
             0f,  0.4f, 0f, 1.75f, 1f, 0f, 0f
                
                
        };
        
        mVertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        
        mVertexData.put(tableVerticesWithTriangles);
    }
    

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.svs);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.sfs);
        
        int vertexShaderId = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        
        mProgram = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(mProgram);
        }
        
        glUseProgram(mProgram);
        
        mUMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);        
        mAPositionLocation = glGetAttribLocation(mProgram, A_POSITION);   
        mAColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        
        
        mVertexData.position(0);
        glVertexAttribPointer(mAPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);    
        glEnableVertexAttribArray(mAPositionLocation);
        
        
        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(mAColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(mAColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        
        glViewport(0, 0, width, height);
        
        MatrixHelper.perspectiveM(mProjectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setIdentityM(mModelMatrix, 0);
        translateM(mModelMatrix, 0, 0f, 0f, -2f);
        
        final float[] tmp = new float[16];
        multiplyMM(tmp, 0, mProjectionMatrix, 0, mModelMatrix, 0);
        System.arraycopy(tmp, 0, mProjectionMatrix, 0, tmp.length);
                
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        

        
        
        
        glClear(GL_COLOR_BUFFER_BIT);   
        
        glUniformMatrix4fv(mUMatrixLocation, 1, false, mProjectionMatrix, 0);
        
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);        
        
        glDrawArrays(GL_LINES, 6, 2);        
        
        glDrawArrays(GL_POINTS, 8, 1);        
       
        glDrawArrays(GL_POINTS, 9, 1);        
        
//        glDrawArrays(GL_TRIANGLES, 10, 6);        
        
    }

}
