package extrace.loader;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class QueryRouteLoader extends HttpAsyncTask{

	String url;
	IDataAdapter<List<TransPackage>> adapter;
	private Activity context;
	private ArrayList<TransPackage> myRoute;

	public QueryRouteLoader(IDataAdapter<List<TransPackage>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		myRoute = JsonUtils.fromJson(json_data, new TypeToken<ArrayList<TransPackage> >(){});
		for(TransPackage pr : myRoute){
			Log.d("*********query", pr.toString());
		}
		adapter.setData(myRoute);
		
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	public void QueryRoute(String id){
		url += "getExpressSheetRoute/"+ id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
