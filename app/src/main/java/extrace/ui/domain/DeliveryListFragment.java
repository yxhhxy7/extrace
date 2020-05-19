package extrace.ui.domain;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import extrace.loader.TransPackageListLoader;
import extrace.loader.ZhuanYunLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.PackageRouteWrapper;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.net.NotifyAdapter;
import extrace.net.PositionAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import extrace.ui.misc.TransPackageListAdapter;


public class DeliveryListFragment extends ListFragment implements IDataAdapter<List<TransPackage>>, PositionAdapter<ArrayList<PackageRoute>>, NotifyAdapter<Boolean> {

	private static final String ARG_EX_TYPE = "ExType";

	// TODO: Rename and change types of parameters
	private String mExType;

	private MyService services;
	private ServiceConnection conn;

	private ArrayList<PackageRoute> myRoute = new ArrayList<>();
	private TransPackageListAdapter mAdapter;
	private TransPackageListLoader mLoader;
	private ArrayList<TransPackage> transList;
	private Button startBtn;
	private Button endBtn;
	private Button uploadBtn;
	private PackageRouteWrapper myWrapper = new PackageRouteWrapper();
	private ZhuanYunLoader myLoader;

	private MyService.LocalBinder binder;

	Intent mIntent;

	private OnFragmentInteractionListener mListener;

	// TODO: Rename and change types of parameters
	public static DeliveryListFragment newInstance(String ex_type) {

		DeliveryListFragment fragment = new DeliveryListFragment();

		Bundle args = new Bundle();
		args.putString(ARG_EX_TYPE, ex_type);	//构造方法传入参数,使用Bundle来作为参数的容器
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DeliveryListFragment() {
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {	//另一种读出传入参数的方式
			mExType = getArguments().getString(ARG_EX_TYPE);
		}
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        // setEmptyText("包裹列表空的!");


        mAdapter = new TransPackageListAdapter(new ArrayList<TransPackage>(), this.getActivity(), mExType);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        registerForContextMenu(getListView());

        initService();

        RefreshList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}*/
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.package_list_fragment, container, false);
		startBtn = (Button) rootView.findViewById(R.id.start_trans_package);
		endBtn = (Button) rootView.findViewById(R.id.end_trans_package);
		uploadBtn = (Button) rootView.findViewById(R.id.upload_position_data);

		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("start*******", "***");
                Toast.makeText(getActivity(), "开始转运！", Toast.LENGTH_SHORT).show();
				Intent start = new Intent(getActivity(),MyService.class);
				getActivity().startService(start);
				getActivity().bindService(start, conn, Service.BIND_AUTO_CREATE);
			}
		});

		endBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getPosition();
			}
		});

		uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadInfo();
                // Toast.makeText(getActivity(), "已成功上传数据信息！", Toast.LENGTH_SHORT).show();
            }
        });

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener.onFragmentInteraction(mAdapter.getItem(position).getID());
		}
		//EditExpress(mAdapter.getItem(position));
		// DeliveExpress(mAdapter.getItem(position));
		//Toast.makeText(getActivity(),"别崩了",Toast.LENGTH_SHORT).show();
	}

	@Override
	public List<TransPackage> getData() {
		return null;
	}

	@Override
	public void setData(List<TransPackage> data) {
		transList = (ArrayList<TransPackage>) data;
		for(TransPackage tp: transList){
			Log.d("*****pack", tp.toString());
		}
		refresh();
	}

	@Override
	public void notifyDataSetChanged() {

	}

    @Override
    public void setPosition(ArrayList<PackageRoute> data) {
        Toast.makeText(getActivity(), "成功获取位置数据信息!", Toast.LENGTH_SHORT).show();
        myRoute = data;
        Log.d("end*******", Boolean.toString(myRoute.isEmpty()));
        for(PackageRoute ll : myRoute){
            Log.d("route***********", ll.toString());
        }

        myWrapper.setPositionInfo(myRoute);
        ArrayList<String> packageId = new ArrayList<>();
        for(TransPackage tp : transList){
            packageId.add(tp.getID());
        }
        myWrapper.setPackageId(packageId);
        Log.d("*****wrapper", myWrapper.toString());

        Intent stop = new Intent(getActivity(),MyService.class);
        getActivity().stopService(stop);
    }

    @Override
    public void notifyResult(Boolean result) {
        if(result == true){
            Toast.makeText(getActivity(), "数据已成功上传！！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "数据上传失败，请重新上传！！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public void getPosition(){
        binder.setAdapter(this);
        binder.stop();
    }

    public void uploadInfo(){
        myLoader = new ZhuanYunLoader(this, getActivity());
        myLoader.UploadPositionInfo(myWrapper);
    }

	public void refresh(){
		mAdapter = new TransPackageListAdapter(transList, this.getActivity(), mExType);
		setListAdapter(mAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		registerForContextMenu(getListView());
	}

	private void RefreshList()
	{
		((ExTraceApplication)getActivity().getApplication()).refresh();
		String pkgId = ((ExTraceApplication)this.getActivity().getApplication()).getLoginUser().geTransPackageID();

		mLoader = new TransPackageListLoader(this, this.getActivity());
		mLoader.QueryPackageList(pkgId);
		//mLoader.LoadExpressList();
	}

	public void initService(){
		conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				binder = (MyService.LocalBinder) iBinder;
				/*.start();
				binder.end();
				services = binder.getservices();
				services.myway();*/
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				services = null;
			}
		};
	}

	void EditExpress(ExpressSheet es)
    {
		Intent intent = new Intent();
		intent.putExtra("Action","Edit");
		intent.putExtra("ExpressSheet",es);
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);  	
    }

    void DeliveExpress(ExpressSheet es){
		Intent intent = new Intent();
		intent.putExtra("Action","Delive");
		intent.putExtra("ExpressSheet",es);
		intent.setClass(this.getActivity(),ExpressDeliveActivity.class);
		startActivity(intent);
	}

}
