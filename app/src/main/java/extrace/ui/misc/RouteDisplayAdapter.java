package extrace.ui.misc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import extrace.misc.model.RouteItem;
import extrace.ui.main.R;

public class RouteDisplayAdapter extends ArrayAdapter {

    private int layoutId;
    private ArrayList<RouteItem> data;
    private Context context;

    public RouteDisplayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        layoutId = resource;
        data = (ArrayList<RouteItem>) objects;
        Log.d("*****data", data.toString());
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RouteItem item = (RouteItem) getItem(position);
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.route_list_item, null);
        TextView time1 = view.findViewById(R.id.route_time1);
        TextView time2 = view.findViewById(R.id.route_time2);
        TextView routeDetail = view.findViewById(R.id.route_detail);

        Date date = item.getTime();
        time1.setText(String.format("%tm", date) + "-" + String.format("%td", date));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        time2.setText(formatter.format(date));
        routeDetail.setText("从【" + item.getFrom()+"】出发，到【" + item.getTo() + "】的路上。");

        return view;
    }
}
