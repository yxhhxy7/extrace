package extrace.loader;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressDeliveLoader extends HttpAsyncTask {

    String url;
    IDataAdapter<Boolean> adapter;
    private Activity context;

    public ExpressDeliveLoader(IDataAdapter<Boolean> adpt, Activity context) {
        super(context);
        this.context = context;
        adapter = adpt;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        Boolean bo = JsonUtils.fromJson(json_data, new TypeToken<Boolean>(){});
        adapter.setData(bo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }

    public void ReadyDispatch(String id){
        ((ExTraceApplication)context.getApplication()).refresh();
        String telCode=((ExTraceApplication)context.getApplication()).getLoginUser().getTelCode();
        url += "readyDispatchExpressSheetId/id/"+ id + "/telCode/"+ telCode +"?_type=json";
        try {
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Dispatch(String id){
        ((ExTraceApplication)context.getApplication()).refresh();
        String telCode=((ExTraceApplication)context.getApplication()).getLoginUser().getTelCode();
        url += "dispatchExpressSheetId/id/"+ id + "/telCode/"+ telCode +"?_type=json";
        try {
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
