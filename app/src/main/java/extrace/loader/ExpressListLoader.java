package extrace.loader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressListLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<List<ExpressSheet>> adapter;
	IDataAdapter<ArrayList<ExpressSheet>> fragmentAdapter;
	private Activity context;
	
	public ExpressListLoader(IDataAdapter<List<ExpressSheet>> adpt, IDataAdapter<ArrayList<ExpressSheet>> fragmentAdapter, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		this.fragmentAdapter = fragmentAdapter;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}
	
	@Override
	public void onDataReceive(String class_data, String json_data) {
		if(json_data.equals("Deleted")){
			//adapter.getData().remove(0);	//这个地方不好处理
			Toast.makeText(context, "包裹已拆包!", Toast.LENGTH_SHORT).show();
		}
		else{
			List<ExpressSheet> cstm = JsonUtils.fromJson(json_data, new TypeToken<List<ExpressSheet>>(){});
			for(ExpressSheet e:cstm){
				Log.d("ExpressListLoader", e.toString());
			}
			adapter.setData(cstm);
			fragmentAdapter.setData((ArrayList<ExpressSheet>) cstm);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		Log.i("onStatusNotify", "onStatusNotify: " + str_response);
	}

	public void LoadExpressListInPackage(String pkgId)
	{
		url += "getExpressListInPackage/PackageId/"+pkgId+"?_type=json";
		Log.d("ExpressListLoader", url);
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
