package extrace.ui.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import extrace.net.JsonUtils;
import extrace.ui.misc.RegionListActivity;
import zxing.util.CaptureActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;

public class StartExpressActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<ExpressSheet> {

//	public static final int INTENT_NEW = 1;
//	public static final int INTENT_EDIT = 2;

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_RCV = 101;
    public static final int REQUEST_SND = 102;
    public static final int REQUEST_FIND_RECEIVER_ADDR = 103;
    public static final  int REQUEST_FIND_SENDER_ADDR = 104;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ExpressSheet mItem = new ExpressSheet();

    private ExpressLoader mLoader;
    private Intent mIntent;
    private ExpressEditFragment3 baseFragment;
    private ExpressEditFragment2 externFragment;
    private CustomerInfo sndCustomer = new CustomerInfo();
    private CustomerInfo rcvCustomer = new CustomerInfo();
    private MenuItem action_menu_item;
    private boolean new_es = false;	//新建

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_express_edit);
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

    /*@Override
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
    }*/

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
        // mItem = data;
        mItem.setSender(data.getSender());
        mItem.setRecever(data.getRecever());
    }

    @Override
    public void notifyDataSetChanged() {
        Toast.makeText(getApplicationContext(), "快件运单信息保存完成!", Toast.LENGTH_LONG).show();
        return ;
        /*if(baseFragment != null){
            baseFragment.RefreshUI(mItem);
        }
        MenuDisplay(mItem.getStatus());*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        if (data.hasExtra("BarCode")) {
            String id = data.getStringExtra("BarCode");
            mItem.setID(id);
            baseFragment.mIDView.setText(id);
        } else if(data.hasExtra("CustomerInfo") && requestCode == REQUEST_SND){
            sndCustomer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
            baseFragment.mSndNameView.setText(sndCustomer.getName() );
            baseFragment.mSndTelCodeView.setText(sndCustomer.getTelCode());
            baseFragment.mSndAddrView.setText(sndCustomer.getAddress() );
            baseFragment.mSndDeptView.setText(sndCustomer.getDepartment());
        }else if(data.hasExtra("CustomerInfo" ) && requestCode == REQUEST_RCV){
            rcvCustomer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
            baseFragment.mRcvNameView.setText(rcvCustomer.getName());
            baseFragment.mRcvTelCodeView.setText(rcvCustomer.getTelCode());
            baseFragment.mRcvAddrView.setText(rcvCustomer.getAddress());
            baseFragment.mRcvDeptView.setText(rcvCustomer.getDepartment());
        } else if(resultCode == RESULT_OK) {
            String regionId,regionString;
            if (data.hasExtra("RegionId")) {
                regionId = data.getStringExtra("RegionId");
                regionString = data.getStringExtra("RegionString");
            } else {
                regionId = "";
                regionString = "";
            }

            Log.d("!!!!!!", regionString);
            Log.d("!!!", String.valueOf(requestCode));
            Log.d("!!", regionId);
            if(requestCode == REQUEST_FIND_RECEIVER_ADDR){
                Log.d("!!!", "!!!");
                baseFragment.mRcvAddrView.setText(regionString);
                rcvCustomer.setRegionCode(regionId);
            } else if(requestCode == REQUEST_FIND_SENDER_ADDR){
                baseFragment.mSndAddrView.setText(regionString);
                sndCustomer.setRegionCode(regionId);
            }
        }
         // mItem.setID("123");
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
        /*mLoader = new ExpressLoader(this, this);
        mLoader.Save(mItem);*/
        if(externFragment.mExpSheetType.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件类型！", Toast.LENGTH_LONG).show();

            return ;
        } else {
            mItem.setType(Integer.parseInt(externFragment.mExpSheetType.getText().toString()));
        }

        if(externFragment.mExpSheetWeight.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件重量！", Toast.LENGTH_LONG).show();
            return ;
        } else {
            mItem.setWeight(Float.parseFloat(externFragment.mExpSheetWeight.getText().toString()));
        }

        if(externFragment.mExpSheetFees.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件费用！", Toast.LENGTH_LONG).show();
            return ;
        } else {
            mItem.setTranFee(Float.parseFloat(externFragment.mExpSheetFees.getText().toString()));
        }

        if(externFragment.mExpressSheetStatus.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件状态！", Toast.LENGTH_LONG).show();
            return ;
        }else {
            mItem.setStatus(Integer.parseInt(externFragment.mExpressSheetStatus.getText().toString()));
        }

        if(externFragment.mExpressSheetIsuFees.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件保险金！", Toast.LENGTH_LONG).show();
            return ;
        } else{
            mItem.setInsuFee(Float.parseFloat(externFragment.mExpressSheetIsuFees.getText().toString()));
        }

        if (sndCustomer.getID() != 0){
            CustomerInfo tmp = new CustomerInfo();
            tmp = mItem.getSender();
            tmp.setID(sndCustomer.getID());
            mItem.setSender(tmp);
        }

        if(!sndCustomer.getRegionCode().isEmpty()){
            CustomerInfo tmp = new CustomerInfo();
            tmp = mItem.getSender();
            tmp.setRegionCode(sndCustomer.getRegionCode());
            mItem.setSender(tmp);
        }

        if(rcvCustomer.getID() != 0){
            CustomerInfo tmp = new CustomerInfo();
            tmp = mItem.getRecever();
            tmp.setID(rcvCustomer.getID());
            mItem.setRecever(tmp);
        }

        if(!rcvCustomer.getRegionCode().isEmpty()){
            CustomerInfo tmp = new CustomerInfo();
            tmp = mItem.getRecever();
            tmp.setRegionCode(rcvCustomer.getRegionCode());
            mItem.setRecever(tmp);
        }

        try{
            mItem.setAccepteTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(baseFragment.mRcvTimeView.getText().toString()));
        }catch (Exception e){
            e.printStackTrace();
        }

        String res = JsonUtils.toJson(mItem);
        Log.d("!!!!!", res);
        res = res.trim();
        mLoader = new ExpressLoader(this, this);
        mLoader.New(res);
        // Toast.makeText(getApplicationContext(), "已成功创建快递单!", Toast.LENGTH_SHORT).show();

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
                    baseFragment = ExpressEditFragment3.newInstance();
                    return baseFragment;
                case 1:
                    externFragment = ExpressEditFragment2.newInstance();
                    return externFragment;
            }
            return ExpressEditFragment3.newInstance();
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
    public static class ExpressEditFragment3 extends Fragment {

        private TextView mIDView;
        private EditText mRcvNameView;
        private EditText mRcvTelCodeView;
        private EditText mRcvAddrView;

        private EditText mSndNameView;
        private EditText mSndTelCodeView;
        private EditText mSndAddrView;
        private EditText mRcvDeptView;
        private EditText mSndDeptView;

        private EditText mRcverView;
        private TextView mRcvTimeView;

        private EditText mSnderView;
        private TextView mSndTimeView;

        private TextView mStatusView;

        private ImageView mbtnCapture;
        private ImageView mbtnRcv;
        private ImageView mbtnSnd;

        private ImageView getRecevierAddr;
        private ImageView getSenderAddr;

        private Button btnCommit;

        public static ExpressEditFragment3 newInstance() {
            ExpressEditFragment3 fragment = new ExpressEditFragment3();
            return fragment;
        }

        public ExpressEditFragment3() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_express_sheet,
                    container, false);
            mIDView = (TextView) rootView.findViewById(R.id.CreateExpressId);
            mRcvNameView = (EditText) rootView.findViewById(R.id.CreateExpressRcvName);
            mRcvTelCodeView = (EditText) rootView.findViewById(R.id.CreateExpressRcvTel);
            mRcvAddrView = (EditText) rootView.findViewById(R.id.CreateExpressRcvAddr);
            mRcvDeptView = (EditText) rootView.findViewById(R.id.CreateRcvExpressDept);
            mSndDeptView = (EditText) rootView.findViewById(R.id.CreateSndExpressDept);


            mSndNameView = (EditText) rootView.findViewById(R.id.CreateExpressSndName);
            mSndTelCodeView = (EditText) rootView.findViewById(R.id.CreateExpressSndTel);
            mSndAddrView = (EditText) rootView.findViewById(R.id.CreateExpressSndAddr);

            mRcvTimeView = (TextView) rootView.findViewById(R.id.CreateExpressAccTime);
            mSndTimeView = (TextView) rootView.findViewById(R.id.CreateExpressDlvTime);

            getRecevierAddr = (ImageView) rootView.findViewById(R.id.find_receiver_addr);
            getSenderAddr = (ImageView) rootView.findViewById(R.id.find_sender_addr);

            getRecevierAddr.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), RegionListActivity.class);
                            getActivity().startActivityForResult(intent, REQUEST_FIND_RECEIVER_ADDR);
                        }
                    }
            );

            getSenderAddr.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), RegionListActivity.class);
                            getActivity().startActivityForResult(intent, REQUEST_FIND_SENDER_ADDR);
                        }
                    }
            );

            mbtnCapture = (ImageView) rootView.findViewById(R.id.create_action_ex_capture_icon);
            mbtnCapture.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((StartExpressActivity) getActivity()).StartCapture();
                        }
                    });
            mbtnRcv = (ImageView) rootView.findViewById(R.id.create_action_ex_rcv_icon);
            mbtnRcv.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((StartExpressActivity) getActivity()).GetCustomer(REQUEST_RCV);
                        }
                    });
            mbtnSnd = (ImageView) rootView.findViewById(R.id.create_action_ex_snd_icon);
            mbtnSnd.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((StartExpressActivity) getActivity()).GetCustomer(REQUEST_SND);
                        }
                    });

            // 提交快件的按钮监听
            btnCommit = (Button) rootView.findViewById(R.id.create_btn_commit);
            btnCommit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ExpressSheet nowES = ((StartExpressActivity) getActivity()).getData();
                            CustomerInfo receiver = new CustomerInfo();
                            if(mRcvNameView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入收件人姓名！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                receiver.setName(mRcvNameView.getText().toString());
                            }

                            if(mRcvTelCodeView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入收件人电话！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                receiver.setTelCode(mRcvTelCodeView.getText().toString());
                            }

                            if(mRcvAddrView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入收件人行政区！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                receiver.setAddress(mRcvAddrView.getText().toString());
                            }

                            if(mRcvDeptView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入收件人详细地址！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                receiver.setDepartment(mRcvDeptView.getText().toString());
                            }

                            CustomerInfo sender = new CustomerInfo();

                            if(mSndNameView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入发件人姓名！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                sender.setName(mSndNameView.getText().toString());
                            }

                            if(mSndTelCodeView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入发件人电话！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                sender.setTelCode(mSndTelCodeView.getText().toString());
                            }

                            if(mSndAddrView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入发件人行政区！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                sender.setAddress(mSndAddrView.getText().toString());
                            }

                            if(mSndDeptView.getText().toString().equals("")){
                                Toast.makeText(getContext(), "请输入发件人详细地址！", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                receiver.setDepartment(mSndDeptView.getText().toString());
                            }

                            nowES.setRecever(receiver);
                            nowES.setSender(sender);

                            Log.d("!!!!", nowES.toString());

                            ((StartExpressActivity) getActivity()).setData(nowES);
                            ((StartExpressActivity) getActivity()).Save();
                        }
                    }
            );
            mRcvTimeView.setText((DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date())));
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
                case ExpressSheet.STATUS.STATUS_TRANSPORT:
                    stText = "运送途中";
                    break;
                case ExpressSheet.STATUS.STATUS_DELIVERIED:
                    stText = "已交付";
                    break;
            }
            displayBtn(es);
        }

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

        void displayRcv(ExpressSheet es){
            if(es.getRecever() != null){
                mRcvNameView.setText(es.getRecever().getName());
                mRcvTelCodeView.setText(es.getRecever().getTelCode());
                mRcvNameView.setTag(es.getRecever());
                mRcvAddrView.setText(es.getRecever().getAddress());
            }
            else{
                mRcvNameView.setText(null);
                mRcvTelCodeView.setText(null);
                mRcvNameView.setTag(null);
                mRcvAddrView.setText(null);
            }
        }

        void displaySnd(ExpressSheet es){
            if(es.getSender() != null){
                mSndNameView.setText(es.getSender().getName());
                mSndTelCodeView.setText(es.getSender().getTelCode());
                mSndNameView.setTag(es.getSender());
                mSndAddrView.setText(es.getSender().getAddress());
            }
            else{
                mSndNameView.setText(null);
                mSndTelCodeView.setText(null);
                mSndNameView.setTag(null);
                mSndAddrView.setText(null);

            }
        }
    }

    public static class ExpressEditFragment2 extends Fragment {

        private EditText mExpSheetType;
        private EditText mExpSheetFees;
        private EditText mExpSheetWeight;
        private EditText mExpressSheetStatus;
        private EditText mExpressSheetIsuFees;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
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
            mExpressSheetStatus = (EditText) rootView.findViewById(R.id.ExpressSheetStatus);
            mExpSheetFees  = (EditText) rootView.findViewById(R.id.ExpressSheetTransFee);
            mExpSheetType = (EditText) rootView.findViewById(R.id.ExpressSheetType);
            mExpSheetWeight = (EditText) rootView.findViewById(R.id.ExpressSheetWeught);
            mExpressSheetIsuFees = (EditText) rootView.findViewById(R.id.ExpressSheetIsuFee);

//			TextView textView = (TextView) rootView
//					.findViewById(R.id.section_label);
//			textView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


}
