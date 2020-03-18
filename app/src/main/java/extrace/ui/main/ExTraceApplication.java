package extrace.ui.main;

import extrace.misc.model.UserInfo;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ExTraceApplication extends Application {
	//private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//	String mServerUrl;
//	String mMiscService,mDomainService;
    UserInfo userInfo;
    
    public String getServerUrl() {  
        return settings.getString("ServerUrl", "");  
    }  
    public String getMiscServiceUrl() {  
        return getServerUrl() + settings.getString("MiscService", ""); 
    }  
    public String getDomainServiceUrl() {  
        return getServerUrl() + settings.getString("DomainService", ""); 
    }  
  
    public UserInfo getLoginUser(){
    	return userInfo;
    }
    
    @Override  
    public void onCreate() {  
        super.onCreate();  
        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		//临时造一个用户
		userInfo = new UserInfo();
		userInfo.setID(12);
		userInfo.setReceivePackageID("1111112222");
		userInfo.setTransPackageID("1111115555");
		userInfo.setDelivePackageID("1111113333");
    }  
      
    public void onTerminate() {  
        super.onTerminate();  
          
        //save data of the map  
    }  
}
