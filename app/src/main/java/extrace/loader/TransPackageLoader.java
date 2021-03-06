package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<TransPackage> adapter;
	private Activity context;
	public Boolean f=false;
	
	public TransPackageLoader(IDataAdapter<TransPackage> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		f=false;
		Log.d("*****package", json_data);
		if(class_name.equals("TransPackage"))
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			Log.d("packageId", ci.getID());
			Log.d("****package", ci.toString());
			adapter.setData(ci);
			adapter.notifyDataSetChanged();

		}
		else if(class_name.equals("R_TransPackage"))		//保存完成
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "包裹信息保存完成!", Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals("O_TransPackage"))
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			Log.d("packageId", ci.getID());
			Log.d("****package", ci.toString());
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		}
		else if(class_name.equals("G_TransPackage"))
		{
			f=true;
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			Log.d("packageId", ci.getID());
			Log.d("****package", ci.toString());
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		}
		else{

		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	public void Load(String id)
	{
		url += "getTransPackage/"+ id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void Save(TransPackage tp)
	{
		String jsonObj = JsonUtils.toJson(tp, true);
		url += "saveTransPackage";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void NewPackage(String pid){
		int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
		url += "newTransPackage/"  + uid + "/" + pid + "?_type=json";
		Log.d("^^^^^", url);
		try {
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void getTransPackage(String pid){
		url += "getTransPackage/" + pid + "?_type=json";
		try {
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}


}
