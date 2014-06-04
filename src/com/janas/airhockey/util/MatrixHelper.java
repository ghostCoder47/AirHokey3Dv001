package com.janas.airhockey.util;

public class MatrixHelper {
	
	public static void perspectiveM(float[] matrix, float yFovInDegrees, float aspect, float near, float far) {
		
		final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
		final float focalLength = (float) (1.0 / Math.tan(angleInRadians / 2.0));	//a
		
		matrix[0] = focalLength / aspect;
		matrix[1] = 0f;
		matrix[2] = 0f;
		matrix[3] = 0f;
		
		matrix[4] = 0f;
		matrix[5] = focalLength;
		matrix[6] = 0f;
		matrix[7] = 0f;
		
		matrix[8] = 0f;
		matrix[9] = 0f;
		matrix[10] = - ((far + near) / (far - near));
		matrix[11] = -1f;
		
		matrix[12] = 0f;
		matrix[13] = 0f;
		matrix[14] = - ((2f * far * near) / (far - near));
		matrix[15] = 0f;
	}

}
