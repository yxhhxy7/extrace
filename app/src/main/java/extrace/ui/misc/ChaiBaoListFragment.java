package extrace.ui.misc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import extrace.loader.ExpressListLoader;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressEditActivity;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.ExTraceApplication;


public class ChaiBaoListFragment extends ListFragment implements IDataAdapter<ArrayList<ExpressSheet>>{

    private static final String ARG_EX_TYPE = "ExType";

    // TODO: Rename and change types of parameters
    private String mExType;

    private ExpressListAdapter mAdapter;
    private ExpressListLoader mLoader;
    private ArrayList<ExpressSheet> mEsList = new ArrayList<>();
    private Boolean firstTime = false;

    Intent mIntent;

    private extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static ChaiBaoListFragment newInstance(String ex_type) {

        ChaiBaoListFragment fragment = new ChaiBaoListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_EX_TYPE, ex_type);	//构造方法传入参数,使用Bundle来作为参数的容器
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChaiBaoListFragment() {
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getArguments() != null) {	//另一种读出传入参数的方式
            mExType = getArguments().getString(ARG_EX_TYPE);
            Log.d("ChaiBaoListFrag", mExType);
        }
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("快递列表空的!");

        mAdapter = new ExpressListAdapter(new ArrayList<ExpressSheet>(), this.getActivity(), mExType);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(getListView());

        if(!firstTime){
            RefreshList();
            firstTime = ! firstTime;
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mAdapter.getItem(position).getID());
        }
        EditExpress(mAdapter.getItem(position));
    }

    @Override
    public ArrayList<ExpressSheet> getData() {
        return null;
    }

    @Override
    public void setData(ArrayList<ExpressSheet> data) {
        mEsList = data;
    }

    @Override
    public void notifyDataSetChanged() {

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

    private void RefreshList()
    {

        mLoader = new ExpressListLoader(mAdapter, this, this.getActivity());
        mLoader.LoadExpressListInPackage( mExType);
        //mLoader.LoadExpressList();
    }

    public void changeStatus(String id){
        boolean flag = false;
        for(ExpressSheet es : mEsList){
            if(es.getID().equals(id)){
                flag = true;
                es.setAcc2("-1");
                Log.d("******size", mEsList.toString());
                break;
            }
        }
        if(flag == false){
            Toast.makeText(getActivity(), "这个快件不在包裹中！", Toast.LENGTH_SHORT).show();
        } else {
            resetList(mEsList);
        }

    }

    public boolean checkStatus(){
        Log.d("******size", String.valueOf(mEsList.size()));
        Log.d("******size", mEsList.toString());
        for(ExpressSheet es : mEsList){
            if(es.getAcc2() == null ){
                return false;
            }
        }
        for(ExpressSheet es : mEsList){
            es.setAcc2(null);
        }
        return true;

    }


    public void resetList(ArrayList<ExpressSheet> mes){
        mAdapter.setData(mes);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(getListView());
    }

    void EditExpress(ExpressSheet es)
    {
        Intent intent = new Intent();
        intent.putExtra("Action","Edit");
        intent.putExtra("ExpressSheet",es);
        intent.setClass(this.getActivity(), ExpressEditActivity.class);
        startActivityForResult(intent, 0);
    }

}

