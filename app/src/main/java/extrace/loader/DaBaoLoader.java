package extrace.loader;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.misc.model.TransPackageExpressSheets;
import extrace.net.NotifyAdapter;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class DaBaoLoader extends HttpAsyncTask {

    String url;
    NotifyAdapter<Boolean> adapter;
    private Activity context;

    public DaBaoLoader(NotifyAdapter<Boolean> adpt, Activity context) {
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


    public void DaBao(ArrayList<ExpressSheet> myList, TransPackage tp){
        Log.d("^^^^^", myList.toString());
        TransPackageExpressSheets tpes = new TransPackageExpressSheets();
        tpes.setExpressSheets(myList);
        tpes.setTp(tp);
        Log.d("^^^^^", tpes.toString());
        String json = JsonUtils.toJson(tpes);
        Log.d("^^^^^", json);
        url += "packTransPackage/"  + "?_type=json";
        Log.d("^^^^^", url);
        try {
            execute(url,"POST",json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
