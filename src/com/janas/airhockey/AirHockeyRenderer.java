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
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.janas.airhockey.util.LoggerConfig;
import com.janas.airhockey.util.ShaderHelper;
import com.janas.airhockey.util.TextResourceReader;

public class AirHockeyRenderer implements Renderer {
    
    private static final String TAG = "AirHokeyRenderer";
    
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final float[] m_projectionMatrix = new float[16];
    private final FloatBuffer m_vertexData;
    private final Context m_context;
    
    private int m_program;
    private int m_aPositionLocation;
    private int m_aColorLocation;
    private int m_uMatrixLocation;
    
    
    
    public AirHockeyRenderer(Context context) {
        
        this.m_context = context;
        
        float[] tableVerticesWithTriangles = {                
                
                // coordinates order: X, Y, R, G, B
                // triangle fan
                
                
                // blended_value = (vertex_0_value * (100% – distance_ratio)) + (vertex_1_value * distance_ratio)
                
             // Triangle Fan
                0f,    0f,   1f,   1f,   1f,         
             -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,            
              0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
              0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
             -0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
             -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

             // Line 1
             -0.5f, 0f, 1f, 0f, 0f,
              0.5f, 0f, 1f, 0f, 0f,

             // Mallets
             0f, -0.4f, 0f, 0f, 1f,
             0f,  0.4f, 1f, 0f, 0f
                
                
        };
        
        m_vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        
        m_vertexData.put(tableVerticesWithTriangles);
    }
    

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(m_context, R.raw.svs);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(m_context, R.raw.sfs);
        
        int vertexShaderId = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        
        m_program = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(m_program);
        }
        
        glUseProgram(m_program);
        
        m_uMatrixLocation = glGetAttribLocation(m_program, U_MATRIX);        
        m_aPositionLocation = glGetAttribLocation(m_program, A_POSITION);   
        m_aColorLocation = glGetAttribLocation(m_program, A_COLOR);
        
        
        m_vertexData.position(0);
        glVertexAttribPointer(m_aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, m_vertexData);    
        glEnableVertexAttribArray(m_aPositionLocation);
        
        
        m_vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(m_aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, m_vertexData);
        glEnableVertexAttribArray(m_aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        
        glViewport(0, 0, width, height);
        
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        
                if (width > height) {
                 // Landscape
                    orthoM(m_projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
                } else {
                 // Portrait or square
                    orthoM(m_projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
                }      
                
              for ( float f : m_projectionMatrix) {
                  Log.d(TAG, "matrix: " + f);
              }
              
              Log.d(TAG, "ratio: " + aspectRatio + " " + width + " " + height);
                
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        

        
        
        
        glClear(GL_COLOR_BUFFER_BIT);   
        
        glUniformMatrix4fv(m_uMatrixLocation, 1, false, m_projectionMatrix, 0);
        
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);        
        
        glDrawArrays(GL_LINES, 6, 2);        
        
        glDrawArrays(GL_POINTS, 8, 1);        
       
        glDrawArrays(GL_POINTS, 9, 1);        
        
//        glDrawArrays(GL_TRIANGLES, 10, 6);        
        
    }

}
