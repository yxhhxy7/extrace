package extrace.ui.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import extrace.misc.model.CustomerInfo;
import extrace.misc.model.TransNode;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class NodeListAdapter extends ArrayAdapter<TransNode> implements IDataAdapter<List<TransNode>>{

	private List<TransNode> itemList;
	private Context context;
//	private GenFilter filter;

	public NodeListAdapter(List<TransNode> itemList, Context ctx) {
		super(ctx, R.layout.customer_select_list, itemList);
		//super(ctx, android.R.layout.simple_list_item_single_choice, itemList);
		
		this.itemList = itemList;
		this.context = ctx;		
	}
	
	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	public TransNode getItem(int position) {
		if (itemList != null)
			return itemList.get(position);
		return null;
	}

	public void setItem(TransNode tn, int position) {
		if (itemList != null)
			itemList.set(position,tn);
	}

	public long getItemId(int position) {
		if (itemList != null)
			return itemList.get(position).hashCode();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.customer_select_list, null);
		}

		TransNode c = itemList.get(position);
		TextView text = (TextView) v.findViewById(android.R.id.text1);
		text.setText(c.getNodeName());
		text.setTag(position);
		return v;		
	}

	@Override
	public List<TransNode> getData() {
		return itemList;
	}

	@Override
	public void setData(List<TransNode> data) {
		this.itemList = data;
	}	
}