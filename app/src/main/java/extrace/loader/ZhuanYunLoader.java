package extrace.loader;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.PackageRouteWrapper;
import extrace.misc.model.TransPackages;
import extrace.net.NotifyAdapter;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ZhuanYunLoader extends HttpAsyncTask {

    String url;
    NotifyAdapter<Boolean> adapter;
    private Activity context;

    public ZhuanYunLoader(NotifyAdapter<Boolean> adpt, Activity context) {
        super(context);
        this.context = context;
        adapter = adpt;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        Boolean bo = JsonUtils.fromJson(json_data, new TypeToken<Boolean>(){});
        Log.d("^^^^", bo.toString());
        adapter.notifyResult(bo);
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }


    public void ZhuanYun(TransPackages tps){

        String json = JsonUtils.toJson(tps);
        Log.d("^^^^^", json);
        url += "deliveryTransPackage/"  + "?_type=json";
        Log.d("^^^^^", url);
        try {
            execute(url,"POST",json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void UploadPositionInfo(PackageRouteWrapper prw){

        Log.d("****wrapper before", prw.toString());
        String json = JsonUtils.toJson(prw);
        Log.d("****wrapper after", json);
        url += "uploadingPackageRoutes/"  + "?_type=json";
        Log.d("^^^^^", url);
        try {
            execute(url,"POST",json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
