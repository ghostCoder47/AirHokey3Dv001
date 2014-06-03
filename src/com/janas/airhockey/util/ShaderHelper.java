package com.janas.airhockey.util;

import static android.opengl.GLES20.*;


import android.util.Log;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";
    
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }
    
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }
    
    private static int compileShader(int type, String shaderCode) {
        
        // create shader
        final int shaderObjectId = glCreateShader(type);
        
        // check creation
        if ( 0 == shaderObjectId ) {
            if ( LoggerConfig.ON ) Log.w(TAG, "could not create new shader");
            return 0;
        }
        
        //pass shader source to shaderObject
        glShaderSource(shaderObjectId, shaderCode);
        
        //compile the shader
        glCompileShader(shaderObjectId);
        
        // get info about compile status
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        
        if ( LoggerConfig.ON ) {
            Log.v(TAG, "results of compiling source: " + "\n" + shaderCode + "\n"
                + glGetShaderInfoLog(shaderObjectId));
        }
        
        // check compile status
        if ( 0 == compileStatus[0]) {
            
            glDeleteShader(shaderObjectId);
            
            if (LoggerConfig.ON){
                Log.w(TAG, "compilation of shader failed, status: " + compileStatus[0]);
            }
            return 0;
        }
        
        // get back shaderObject
        return shaderObjectId;
    }
    
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        
        final int programObjectId = glCreateProgram();
        
        if ( 0 == programObjectId) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }
        
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        
        glLinkProgram(programObjectId);
        
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        
        if (LoggerConfig.ON) {
            Log.v(TAG, "results of linking program: \n"
                    + glGetProgramInfoLog(programObjectId));
        }
        
        if ( 0 == linkStatus[0]) {
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "linking of program failed");
            }
            return 0;
        }
        
        return programObjectId;
    }
    
    public static boolean validateProgram(int programObjectId) {
        
        glValidateProgram(programObjectId);
        
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog: " + glGetProgramInfoLog(programObjectId));
        
        return validateStatus[0] != 0;
    }
    
}
