package com.janas.airhockey;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

public class AirHockeyActivity extends Activity {
    
    private GLSurfaceView m_glSurfaceView;
    private boolean m_rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        m_glSurfaceView = new GLSurfaceView(this);
        
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        
        if (supportsEs2) {
            
            // request opengl es 2.0 context
            m_glSurfaceView.setEGLContextClientVersion(2);
            
            //assign our renderer
            m_glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            setContentView(m_glSurfaceView);
            m_rendererSet = true;
            
            
        } else {
            Toast.makeText(this, "no opengl es 2.0 support", Toast.LENGTH_SHORT).show();
            return;
        }
        
        
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // pause background thread
        if (m_rendererSet) {
            m_glSurfaceView.onPause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // resume backgroun dthread
        if (m_rendererSet) {
            m_glSurfaceView.onResume();
        }
    }



}
