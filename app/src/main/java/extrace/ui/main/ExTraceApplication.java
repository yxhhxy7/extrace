package extrace.ui.main;

import extrace.misc.model.UserInfo;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ExTraceApplication extends Application {
	//private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//	String mServerUrl;
//	String mMiscService,mDomainService;
    private static UserInfo userInfo;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    public String getServerUrl() {  
        return settings.getString("ServerUrl", "");  
    }  
    public String getMiscServiceUrl() {  
        return getServerUrl() + settings.getString("MiscService", ""); 
    }  
    public String getDomainServiceUrl() {  
        return getServerUrl() + settings.getString("DomainService", ""); 
    }  
  
    public static UserInfo getLoginUser(){
    	return userInfo;
    }

    public static void setUserInfo(UserInfo ui){
        userInfo = ui;
    }
    
    @Override  
    public void onCreate() {  
        super.onCreate();  
        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		//临时造一个用户
		userInfo = new UserInfo();
		sp=getSharedPreferences("UserInfo", MODE_PRIVATE);//获取对象，并且命名文件的名称
        String receivePackageID = sp.getString("receivePackageID", "");; //获取文件中的数据
        String delivePackageID =sp.getString("delivePackageID","");
        String transPackageID =sp.getString("transPackageID", "");
        String telCode=sp.getString("telCode", "");
        String PWD=sp.getString("PWD", "");
		//userInfo.setID(12);
		//userInfo.setReceivePackageID("1111113333");
		//userInfo.setTransPackageID("1111115555");
		//userInfo.setDelivePackageID("1111113331");
        userInfo.setReceivePackageID(receivePackageID);
        userInfo.setTransPackageID(transPackageID);
        userInfo.setDelivePackageID(delivePackageID);
        userInfo.setTelCode(telCode);
        //Toast.makeText(getApplicationContext(),delivePackageID,Toast.LENGTH_LONG).show();
    }  
      
    public void onTerminate() {  
        super.onTerminate();  
          
        //save data of the map  
    }  
}
