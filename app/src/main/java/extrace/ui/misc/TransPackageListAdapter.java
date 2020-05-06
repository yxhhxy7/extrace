package extrace.ui.misc;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class TransPackageListAdapter extends ArrayAdapter<TransPackage> implements IDataAdapter<List<TransPackage>>{

	private List<TransPackage> itemList;
	private Context context;
	private String ex_type;

	public TransPackageListAdapter(List<TransPackage> itemList, Context ctx, String ex_type) {
		super(ctx, R.layout.express_list_item, itemList);
		//for(TransPackage tp : itemList){
		//	Log.d("^^^^transpackage", tp.toString());
		//}
		this.itemList = itemList;
		this.context = ctx;	
		this.ex_type = ex_type;
	}
	
	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	public TransPackage getItem(int position) {
		if (itemList != null)
			return itemList.get(position);
		return null;
	}

	public long getItemId(int position) {
		if (itemList != null)
			return itemList.get(position).hashCode();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		hold hd = null;
		if(v==null){
			hd = new hold();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.express_list_item, null);
			hd.name = (TextView)v.findViewById(R.id.name);
			hd.telCode = (TextView)v.findViewById(R.id.tel);
			hd.address = (TextView)v.findViewById(R.id.addr);
			hd.time = (TextView)v.findViewById(R.id.time);
			hd.status = (TextView)v.findViewById(R.id.st);
			
			v.setTag(hd);
		}else{
			hd = (hold)v.getTag();
		}

		TransPackage tp = getItem(position);
		/*switch(ex_type){
            case "ExTAN":
			case "ExDLV":	//派送
			default:*/
				if(tp.getID() != null ){
					hd.name.setText("包裹ID：" + tp.getID());
				}

				if(tp.getTargetNode() != null){
					hd.address.setText("目标节点：" + tp.getTargetNode());
				}
				hd.telCode.setText("");
				hd.status.setText("");
			/*	break;

		}*/

		return v;		
	}

	@Override
	public List<TransPackage> getData() {
		return itemList;
	}

	@Override
	public void setData(List<TransPackage> data) {
		this.itemList = data;
		if(!itemList.isEmpty()){
			for(TransPackage tp : itemList){
				Log.d("*****************",tp.toString());
			}

		}
	}	
	
	private class hold{
		TextView name;
		TextView telCode;
		TextView address;
		TextView time;
		TextView status;
	}
}