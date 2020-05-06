package extrace.loader;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ChaiBaoLoader extends HttpAsyncTask {

    String url;
    IDataAdapter<Boolean> adapter;
    private Activity context;

    public ChaiBaoLoader(IDataAdapter<Boolean> adpt, Activity context) {
        super(context);
        this.context = context;
        adapter = adpt;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        Boolean bo = JsonUtils.fromJson(json_data, new TypeToken<Boolean>(){});
        Log.d("^^^^", bo.toString());
        adapter.setData(bo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }


    public void ChaiBao(String id){
        url += "unpackTransPackage/"+ id + "?_type=json";
        try {
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
