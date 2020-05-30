package extrace.ui.domain;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;
import zxing.util.CaptureActivity;

public class ExpressQueryActivity extends AppCompatActivity implements ActionBar.TabListener, IDataAdapter<ExpressSheet> {

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_RCV = 101;
    public static final int REQUEST_SND = 102;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ExpressSheet mItem;

    private ExpressLoader mLoader;
    private Intent mIntent;
    private ExpressEditFragment1 baseFragment;
    private ExpressEditFragment2 externFragment;
    private MenuItem action_menu_item;
    private boolean new_es = false;	//新建

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_query);
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        mIntent = getIntent();
        if (mIntent.hasExtra("Action")) {
            if(mIntent.getStringExtra("Action").equals("New")){
                new_es = true;
                StartCapture();
            }
            else if(mIntent.getStringExtra("Action").equals("Query")){
                StartCapture();
            }
            else if(mIntent.getStringExtra("Action").equals("Edit")){
                ExpressSheet es;
                if (mIntent.hasExtra("ExpressSheet")) {
                    es = (ExpressSheet) mIntent.getSerializableExtra("ExpressSheet");
                    Refresh(es.getID());
                } else {
                    this.setResult(RESULT_CANCELED, mIntent);
                    this.finish();
                }
            }
            else{
                this.setResult(RESULT_CANCELED, mIntent);
                this.finish();
            }
        }
        else{
            this.setResult(RESULT_CANCELED, mIntent);
            this.finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.express_edit, menu);
        action_menu_item = menu.findItem(R.id.action_action);
        action_menu_item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_action:
                if(item.getTitle().equals("收件")){
                    //Toast.makeText(getApplicationContext(),"交付",Toast.LENGTH_SHORT).show();
                    Receive(mItem.getID());
                }
                else if(item.getTitle().equals("交付")){
                    Delivery(mItem.getID());
                }
                else if(item.getTitle().equals("追踪")){
                    Tracert(mItem.getID());
                }
                return true;
            case R.id.action_ok:
                Save();
                return true;
            case R.id.action_refresh:
                if (mItem != null) {
                    Refresh(mItem.getID());
                }
                return true;
            case (android.R.id.home):
                mIntent.putExtra("ExpressSheet",mItem);
                this.setResult(RESULT_OK, mIntent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public ExpressSheet getData() {
        return mItem;
    }

    @Override
    public void setData(ExpressSheet data) {
        //String jj= JsonUtils.toJson(mItem, true);
        Log.d("查询", "suxun");
        mItem = data;
        String jj= JsonUtils.toJson(mItem, true);
        Log.d("查询", jj);
    }

    @Override
    public void notifyDataSetChanged() {
        if(baseFragment != null){
            baseFragment.RefreshUI(mItem);
        }

        if(externFragment !=null){
            externFragment.RefreshUI(mItem);
        }



        MenuDisplay(mItem.getStatus());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        CustomerInfo customer;

        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {
                            String id = data.getStringExtra("BarCode");
                            try {
                                mLoader = new ExpressLoader(this, this);
                                if(new_es){
                                    new_es = false;
                                    mLoader.New(id);
                                }
                                else{
                                    mLoader.Load(id);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case REQUEST_RCV:
                        if (data.hasExtra("CustomerInfo")) {
                            customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
                            mItem.setRecever(customer);
                            baseFragment.displayRcv(mItem);
                        }
                        break;
                    case REQUEST_SND:
                        if (data.hasExtra("CustomerInfo")) {
                            customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
                            mItem.setSender(customer);
                            baseFragment.displaySnd(mItem);
                        }
                        break;
                }
                break;
            default:
                break;
        }
    }

    void MenuDisplay(int status){
        action_menu_item.setVisible(true);
        action_menu_item.setEnabled(true);
        switch(status){
            case ExpressSheet.STATUS.STATUS_CREATED:
                action_menu_item.setTitle("收件");
                break;
            case ExpressSheet.STATUS.STATUS_TRANSPORT:
                action_menu_item.setTitle("交付");
                break;
            case ExpressSheet.STATUS.STATUS_DELIVERIED:
                action_menu_item.setTitle("追踪");
                break;
            default:
                action_menu_item.setVisible(false);
                break;
        }
    }

    void Refresh(String id){
        try {
            mLoader = new ExpressLoader(this, this);
            mLoader.Load(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void Receive(String id){
        try {
            mLoader = new ExpressLoader(this, this);
            mLoader.Receive(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void Delivery(String id){
        try {
            mLoader = new ExpressLoader(this, this);
            mLoader.Delivery(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void Tracert(String id){
        //快件追踪
    }

    void Save(){
        mLoader = new ExpressLoader(this, this);
        mLoader.Save(mItem);
    }

    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    private void GetCustomer(int intent_code) {
        Intent intent = new Intent();
        intent.setClass(this, CustomerListActivity.class);
        if(intent_code == REQUEST_RCV){
            if(baseFragment.mRcvNameView.getTag() == null){
                intent.putExtra("Action","New");
            }
            else{
                intent.putExtra("Action","New");
                intent.putExtra("CustomerInfo",(CustomerInfo)baseFragment.mRcvNameView.getTag());
            }
        }
        else if(intent_code == REQUEST_SND){
            if(baseFragment.mSndNameView.getTag() == null){
                intent.putExtra("Action","New");
            }
            else{
                intent.putExtra("Action","New");
                intent.putExtra("CustomerInfo",(CustomerInfo)baseFragment.mSndNameView.getTag());
            }
        }
        startActivityForResult(intent, intent_code);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    baseFragment = ExpressEditFragment1.newInstance();
                    return baseFragment;
                case 1:
                    externFragment = ExpressEditFragment2.newInstance();
                    return externFragment;
            }
            return ExpressEditFragment1.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_ex_edit1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_ex_edit2).toUpperCase(l);
            }
            return null;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ExpressEditFragment1 extends Fragment implements IDataAdapter<ExpressSheet>{

        private TextView mIDView;
        private TextView mRcvNameView;
        private TextView mRcvTelCodeView;
        private TextView mRcvDptView;
        private TextView mRcvAddrView;
        private TextView mRcvRegionView;

        private TextView mSndNameView;
        private TextView mSndTelCodeView;
        private TextView mSndDptView;
        private TextView mSndAddrView;
        private TextView mSndRegionView;

        private TextView mRcverView;
        private TextView mRcvTimeView;

        private TextView mSnderView;
        private TextView mSndTimeView;

        private TextView mStatusView;

        private ImageView mbtnCapture;
        private ImageView mbtnRcv;
        private ImageView mbtnSnd;

        private Button mbtnDelive;
        private ExpressSheet expressSheet;
        private ExpressLoader mLoader;

        public static ExpressEditFragment1 newInstance() {
            ExpressEditFragment1 fragment = new ExpressEditFragment1();
            return fragment;
        }

        public ExpressEditFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_query_express,
                    container, false);
            mIDView = (TextView) rootView.findViewById(R.id.expressId);
            mRcvNameView = (TextView) rootView.findViewById(R.id.expressRcvName);
            mRcvTelCodeView = (TextView) rootView.findViewById(R.id.expressRcvTel);
            mRcvAddrView = (TextView) rootView.findViewById(R.id.expressRcvAddr);
            //mRcvDptView = (TextView) rootView.findViewById(R.id.expressRcvDpt);
            //mRcvRegionView = (TextView) rootView.findViewById(R.id.expressRcvRegion);

            mRcvDptView = (TextView) rootView.findViewById(R.id.RcvExpressDept);
            mSndDptView = (TextView) rootView.findViewById(R.id.SndExpressDept);
            mSndNameView = (TextView) rootView.findViewById(R.id.expressSndName);
            mSndTelCodeView = (TextView) rootView.findViewById(R.id.expressSndTel);
            mSndAddrView = (TextView) rootView.findViewById(R.id.expressSndAddr);
            //mSndDptView = (TextView) rootView.findViewById(R.id.expressSndDpt);
            //mSndRegionView = (TextView) rootView.findViewById(R.id.expressSndRegion);

            mRcvTimeView = (TextView) rootView.findViewById(R.id.expressAccTime);
            mSndTimeView = (TextView) rootView.findViewById(R.id.expressDlvTime);

            mStatusView =  (TextView) rootView.findViewById(R.id.expressStatus);
            /*
            mbtnCapture = (ImageView) rootView.findViewById(R.id.action_ex_capture_icon);
            //mbtnDelive=rootView.findViewById(R.id.btnDelivry);
            mbtnCapture.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ExpressQueryActivity) getActivity()).StartCapture();
                        }
                    });

             */
            /*
            mbtnRcv = (ImageView) rootView.findViewById(R.id.action_ex_rcv_icon);
            mbtnRcv.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ExpressQueryActivity) getActivity()).GetCustomer(REQUEST_RCV);
                        }
                    });
            mbtnSnd = (ImageView) rootView.findViewById(R.id.action_ex_snd_icon);
            mbtnSnd.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ExpressQueryActivity) getActivity()).GetCustomer(REQUEST_SND);
                        }
                    });

             */
            /*
            mbtnDelive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //readyDispatch();
                    //Toast.makeText(getActivity(),"准备派送",Toast.LENGTH_LONG).show();
                }
            });*/
            return rootView;
        }

        void RefreshUI(ExpressSheet es){
            mIDView.setText(es.getID());
            displayRcv(es);
            displaySnd(es);
            if(es.getAccepteTime() != null)
                mRcvTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", es.getAccepteTime()));
            else
                mRcvTimeView.setText(null);
            if(es.getDeliveTime() != null)
                mSndTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", es.getDeliveTime()));
            else
                mSndTimeView.setText(null);


            String stText = "";
            switch(es.getStatus()){
                case ExpressSheet.STATUS.STATUS_CREATED:
                    stText = "正在创建";
                    break;
                //case ExpressSheet.STATUS.STATUS_TRANSPORT:
                //  stText = "运送途中";
                //break;
                case ExpressSheet.STATUS.STATUS_DELIVERIED:
                    stText = "运送中";
                    break;
                case 3:
                    stText = "派件中";
                    break;
                case 4:
                    stText="已送达";
                    break;
            }
            mStatusView.setText(stText);
            //displayBtn(es);
            //readyDispatch(es);
        }
    /*
        void displayBtn(ExpressSheet es){	//按钮状态控制
            if(es.getStatus() == ExpressSheet.STATUS.STATUS_CREATED){
                mbtnRcv.setVisibility(View.VISIBLE);
                mbtnSnd.setVisibility(View.VISIBLE);
            }
            else{
                mbtnRcv.setVisibility(View.INVISIBLE);
                mbtnSnd.setVisibility(View.INVISIBLE);
            }
        }

     */

        void displayRcv(ExpressSheet es){
            if(es.getRecever() != null){
                mRcvNameView.setText(es.getRecever().getName());
                mRcvTelCodeView.setText(es.getRecever().getTelCode());
                mRcvNameView.setTag(es.getRecever());
                mRcvAddrView.setText(es.getRecever().getAddress());
                mRcvDptView.setText(es.getRecever().getDepartment());
                //mRcvDptView.setText(es.getRecever().getDepartment());
                //mRcvRegionView.setText(es.getRecever().getRegionString());
            }
            else{
                mRcvNameView.setText(null);
                mRcvTelCodeView.setText(null);
                mRcvNameView.setTag(null);
                mRcvAddrView.setText(null);
                mRcvDptView.setText(null);
                //mRcvDptView.setText(null);
                //mRcvRegionView.setText(null);
            }
        }

        void displaySnd(ExpressSheet es){
            if(es.getSender() != null){
                mSndNameView.setText(es.getSender().getName());
                mSndTelCodeView.setText(es.getSender().getTelCode());
                mSndNameView.setTag(es.getSender());
                mSndAddrView.setText(es.getSender().getAddress());
                mSndDptView.setText(es.getSender().getDepartment());
                //mSndDptView.setText(es.getSender().getDepartment());
                //mSndRegionView.setText(es.getSender().getRegionString());
            }
            else{
                mSndNameView.setText(null);
                mSndTelCodeView.setText(null);
                mSndNameView.setTag(null);
                mSndAddrView.setText(null);
                mSndDptView.setText(null);
                //mSndDptView.setText(null);
                //mSndRegionView.setText(null);
            }
        }

        @Override
        public ExpressSheet getData() {
            return null;
        }

        @Override
        public void setData(ExpressSheet data) {
            expressSheet=data;
        }



        @Override
        public void notifyDataSetChanged() {
            if(expressSheet.getID()!=null){
                Toast.makeText(getActivity(),"准备派送!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(),"准备派送失败!",Toast.LENGTH_LONG).show();
            }
        }
        /*
        public void readyDispatch(ExpressSheet es){
            mLoader=new ExpressLoader(this,this.getActivity());
            mbtnDelive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoader.ReadyDispatch(es.getID());
                    //Toast.makeText(getActivity(),es.getID(),Toast.LENGTH_LONG).show();
                }
            });


        }*/
    }

    public static class ExpressEditFragment2 extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section number.
         */

        private TextView mTypeView;
        private TextView mWeightView;
        private TextView mTransFeeView;
        private TextView mStatusView;
        private TextView mIsuFeeView;


        public static ExpressEditFragment2 newInstance() {
            ExpressEditFragment2 fragment = new ExpressEditFragment2();
//			Bundle args = new Bundle();
//			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//			fragment.setArguments(args);
            return fragment;
        }

        public ExpressEditFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_express_edit2,
                    container, false);
//			TextView textView = (TextView) rootView
//					.findViewById(R.id.section_label);
//			textView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
            mTypeView = rootView.findViewById(R.id.ExpressSheetType);
            mWeightView = rootView.findViewById(R.id.ExpressSheetWeught);
            mTransFeeView = rootView.findViewById(R.id.ExpressSheetTransFee);
            mIsuFeeView = rootView.findViewById(R.id.ExpressSheetIsuFee);
            mStatusView = rootView.findViewById(R.id.ExpressSheetStatus);
            return rootView;
        }


        void disPlayExetrn(ExpressSheet es) {
            setType(es.getType());
            String weight = es.getWeight() + "";
            mWeightView.setText(weight);
            String TransFee = es.getTranFee() + "";
            mTransFeeView.setText(TransFee);
            String IsuFee = es.getInsuFee()+"";
            mIsuFeeView.setText(IsuFee);
        }


        void setType(int i){
            if(i==0){
                mTypeView.setText("鞋包衣帽");
            }
            else if(i==1){
                mTypeView.setText("化妆品");
            }
            else if(i==2){
                mTypeView.setText("电子数码产品");
            }
            else if(i==3){
                mTypeView.setText("办公用品");
            }
            else if(i==4){
                mTypeView.setText("五金配件");
            }
            else if(i==5){
                mTypeView.setText("文件");
            }
            else if(i==6){
                mTypeView.setText("速食品");
            }
            else if(i==7){
                mTypeView.setText("水果");
            }
            else if(i==8){
                mTypeView.setText("药品");
            }
            else if(i==9){
                mTypeView.setText("日常生活用品");
            }
            else if(i==10){
                mTypeView.setText("艺术品");
            }
            else if(i==11){
                mTypeView.setText("其他");
            }

        }


        void RefreshUI(ExpressSheet es) {
            disPlayExetrn(es);


            String stText = "";
            switch (es.getStatus()) {
                case ExpressSheet.STATUS.STATUS_CREATED:
                    stText = "正在创建";
                    break;
                case 2:
                    stText = "运送中";
                    break;
                case 3:
                    stText = "派件中";
                    break;
                case 4:
                    stText = "已送达";
                    break;
            }
            mStatusView.setText(stText);

        }

    }


}
