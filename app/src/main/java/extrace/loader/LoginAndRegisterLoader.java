package extrace.loader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.TypeVariable;

import extrace.misc.model.UserInfo;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.net.LoginDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.LoginActivity;

public class LoginAndRegisterLoader extends HttpAsyncTask {

    String url;
    UserInfo thisUserInfo;
    LoginDataAdapter<String> myAdpter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public LoginAndRegisterLoader(IDataAdapter<String> adapter, UserInfo userInfo, Activity context) {
        super(context);
        myAdpter = (LoginDataAdapter<String>)adapter;
        thisUserInfo = userInfo;
        url = "http://39.105.163.115:80/TestCxfHibernate/REST/Misc/";
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        Log.d("&&&&&json_data", json_data);
        if(class_name.equals("0")){
             myAdpter.setData("0");
             UserInfo userInfo = JsonUtils.fromJson(json_data, new TypeToken<UserInfo>(){});
             myAdpter.setUserInfo(userInfo);
             myAdpter.notifyDataSetChanged();
        }else if(class_name.equals("1")){
            myAdpter.setData("1");
            myAdpter.notifyDataSetChanged();
        }else if(class_name.equals("2")){
            myAdpter.setData("2");
            myAdpter.notifyDataSetChanged();
        } else if (class_name.equals("E_UserRegister")) {
            myAdpter.setData("2");
            myAdpter.notifyDataSetChanged();
        } else if(class_name.equals("R_UserRegister")){
            myAdpter.setData("1");
            myAdpter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {
    }

    public void login(String tele, String pwd){
        Log.d("!!!!", url);
        url+= "doLogin/" + tele + "/" + pwd;
        try {
            execute(url, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(UserInfo userInfo){
        url += "userRegister";
        Log.d("^^^^^^^^^^^^", userInfo.toString());
        String jsonObj = JsonUtils.toJson(userInfo,true);
        Log.d("^^^^^^^^^^^^^^^", jsonObj);
        try {
            execute(url,"POST",jsonObj);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
