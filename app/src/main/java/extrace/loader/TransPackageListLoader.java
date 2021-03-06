package extrace.loader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageListLoader extends HttpAsyncTask{

	String url;
	IDataAdapter<List<TransPackage>> adapter;
	private Activity context;
	private ArrayList<TransPackage> myTransList;
	
	public TransPackageListLoader(IDataAdapter<List<TransPackage>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		myTransList = JsonUtils.fromJson(json_data, new TypeToken<ArrayList<TransPackage> >(){});
		for(TransPackage tp : myTransList){
			Log.d("******", tp.toString());
		}
		adapter.setData(myTransList);
		
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	public void QueryPackageList(String pkgId){
		url += "getTransPackagesInTransPackage/"+ pkgId + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
