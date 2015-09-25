package com.longluo.demo;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

/**
 * 
 * DemoApp
 *
 * @author    luolong
 * @date      2015-5-27    12:16:55
 *
 * @version
 */
public class DemoApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Init JPush
        JPushInterface.setDebugMode(true); 	// ���ÿ�����־,����ʱ��ر���־
        JPushInterface.init(this);     		// ��ʼ�� JPush
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        
    }

}

