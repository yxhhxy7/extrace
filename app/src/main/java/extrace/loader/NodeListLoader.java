package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import extrace.misc.model.CustomerInfo;
import extrace.misc.model.TransNode;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class NodeListLoader extends HttpAsyncTask {

//	private static final String PREFS_NAME = "ExTrace.cfg";
	String url;// = "http://192.168.7.100:8080/TestCxfHibernate/REST/Misc/";
	IDataAdapter<List<TransNode>> adapter;
	private Activity context;

	public NodeListLoader(IDataAdapter<List<TransNode>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
		Log.d("_____________", url);
//	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//	    url = prefs.getString("ServerUrl", "") + prefs.getString("MiscService", "");
	}
	
	@Override
	public void onDataReceive(String class_data, String json_data) {
		if(json_data.equals("Deleted")){
			//adapter.getData().remove(0);	//这个地方不好处理
			Toast.makeText(context, "客户信息已删除!", Toast.LENGTH_SHORT).show();
		}
		else{
			if(json_data == null || json_data.length() == 0)
			{
				Toast.makeText(context, "没有符合条件的客户信息!", Toast.LENGTH_SHORT).show();
				adapter.setData(null);
				adapter.notifyDataSetChanged();
			}
			else
			{
				List<TransNode> cstm = JsonUtils.fromJson(json_data, new TypeToken<List<TransNode>>(){});
				adapter.setData(cstm);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		Log.i("onStatusNotify", "onStatusNotify: " + str_response);
	}
	
	public void LoadCustomerListByTelCode(String telCode)
	{
		url += "getCustomerListByTelCode/"+ telCode + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadTransNodeListByName(String name)
	{
		url += "getNodesListByName/"+ name + "?_type=json";
		Log.d("_____________", url);
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void DeleteCustomer(int id)
	{
		url += "deleteCustomerInfo/"+ id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
