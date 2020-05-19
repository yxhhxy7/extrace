package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressLoader extends HttpAsyncTask {

    String url;
    IDataAdapter<ExpressSheet> adapter;
    private Activity context;

    public ExpressLoader(IDataAdapter<ExpressSheet> adpt, Activity context) {
        super(context);
        this.context = context;
        adapter = adpt;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        Log.d("-----------", class_name);
        if(class_name.equals("ExpressSheet"))
        {
            ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
            adapter.setData(ci);
            adapter.notifyDataSetChanged();
        }
        else if(class_name.equals("E_ExpressSheet"))		//已经存在
        {

            Toast.makeText(context,json_data, Toast.LENGTH_LONG).show();
//			ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
//			adapter.setData(ci);
//			adapter.notifyDataSetChanged();
//			Toast.makeText(context, "快件运单信息已经存在!", Toast.LENGTH_SHORT).show();
        }
        else if(class_name.equals("R_ExpressSheet"))		//保存完成
        {
            Log.d("-----------", class_name);
            ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
			/*adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();*/
            Toast.makeText(context, "快件运单信息保存完成!", Toast.LENGTH_LONG).show();
        }else if(class_name.equals("true"))
        {
            ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
            adapter.setData(ci);
            adapter.notifyDataSetChanged();
        }
        else if(class_name.equals("unableDispach"))
        {

            //ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
            ExpressSheet ci=new ExpressSheet();
            ci.setID(null);
            adapter.setData(ci);
            adapter.notifyDataSetChanged();


        }
        else
        {
        }
    }

    @Override
    public void onStatusNotify(RETURN_STATUS status, String str_response) {
        // TODO Auto-generated method stub

    }

    public void Load(String id)
    {
        url += "getExpressSheet/"+ id + "?_type=json";
        try {
            execute(url, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void New(String id)
    {
        ((ExTraceApplication)context.getApplication()).refresh();
        int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
        url += "newExpressSheet/id/"+ id + "/uid/"+ uid + "?_type=json";

        url = url.replace("\"","%22");
        url = url.replace(" ","%20");
        url = url.replace("{","%7B");
        url = url.replace("}","%7D");

        try {
            execute(url, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Save(ExpressSheet es)
    {
        Log.d("-----------", "-----");
        String jsonObj = JsonUtils.toJson(es, true);
        url += "saveExpressSheet";
        try {
            execute(url, "POST", jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Receive(String id)
    {
        ((ExTraceApplication)context.getApplication()).refresh();
        String telCode = ((ExTraceApplication)context.getApplication()).getLoginUser().getTelCode();
        url += "receiveExpressSheetId/id/"+ id + "/telCode/"+ telCode + "?_type=json";
        try {
            execute(url, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Delivery(String id)
    {
        ((ExTraceApplication)context.getApplication()).refresh();
        int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
        url += "deliveryExpressSheetId/id/"+ id + "/uid/"+ uid + "?_type=json";
        try {
            execute(url, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
