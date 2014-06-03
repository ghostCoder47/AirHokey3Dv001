package com.janas.airhockey.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
    
    public static String readTextFileFromResource(Context context, int resourceId) {
        
        StringBuilder body = new StringBuilder();
        
        try {
            
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
            String nextLine;
            
            while ( (nextLine = bufferedReader.readLine()) != null ) {
                
                body.append(nextLine);
                body.append('\n');
            }
            
        } catch (IOException ex) {
            throw new RuntimeException("Could not open resource:"  + resourceId, ex);
        } catch (Resources.NotFoundException ex) {
            throw new RuntimeException("Resource not found: " + resourceId, ex);
        }
        
        return body.toString();
    }
}
