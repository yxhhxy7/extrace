package extrace.ui.misc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import extrace.loader.ChaiBaoLoader;
import extrace.net.JsonUtils;
import extrace.ui.domain.ExpressListFragment;
import zxing.util.CaptureActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;
import zxing.util.ViewfinderView;

public class ChaiBaoActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<Boolean>, ExpressListFragment.OnFragmentInteractionListener {

//	public static final int INTENT_NEW = 1;
//	public static final int INTENT_EDIT = 2;

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_QUERY_EXPRESS = 101;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Button submitBtn;
    private Button confirmExprSheetBtn;

    private ExpressSheet mItem = new ExpressSheet();

    private Intent mIntent;
    private ChaiBaoListFragment baseFragment;
    private CustomerInfo sndCustomer = new CustomerInfo();
    private CustomerInfo rcvCustomer = new CustomerInfo();
    private String packageId;
    private MenuItem action_menu_item;
    private boolean new_es = false;	//新建

    private ChaiBaoLoader mChaiBaoLoader;
    private Boolean status = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chai_bao);

        mIntent = getIntent();
        if (mIntent.hasExtra("Action")) {
            if(mIntent.getStringExtra("Action").equals("None")){
                StartCapture(REQUEST_CAPTURE);
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
    public Boolean getData() {
        return true;
    }

    @Override
    public void setData(Boolean data) {
        status = data;
    }


    @Override
    public void notifyDataSetChanged() {
        if (status == true){
            Toast.makeText(this, "拆包成功！！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "包裹已拆包！！", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        if (data.hasExtra("BarCode")) {
            packageId = data.getStringExtra("BarCode");
            Log.d("ChaiBaoActivity", packageId);
            init();
        }
        if(requestCode == REQUEST_QUERY_EXPRESS){
            String id = data.getStringExtra("BarCode");
            baseFragment.changeStatus(id);
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


    private void StartCapture(int request){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, request);
    }

    public void ChaiBao(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(ChaiBaoActivity.this);
        //获取AlertDialog对象
        dialog.setTitle("警告");//设置标题
        dialog.setMessage("确认拆包吗？");//设置信息具体内容

        dialog.setCancelable(false);//设置是否可取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override//设置ok的事件
            public void onClick(DialogInterface dialogInterface, int i) {
                //在此处写入ok的逻辑
                ConfirmChaiBao();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override//设置取消事件
            public void onClick(DialogInterface dialogInterface, int i) {
                //在此写入取消的事件
            }

        });
        dialog.show();

    }

    public void ConfirmChaiBao(){
        mChaiBaoLoader = new ChaiBaoLoader(this,this);
        mChaiBaoLoader.ChaiBao(packageId);
    }

    public void init(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        submitBtn = (Button) findViewById(R.id.submit_chai_bao_btn);
        confirmExprSheetBtn = (Button) findViewById(R.id.confirm_express_sheet);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.chai_bao_pager);
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(baseFragment.checkStatus()){
                    ChaiBao();
                } else {
                    Toast.makeText(getApplicationContext(), "还有快件未确认！!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirmExprSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartCapture(REQUEST_QUERY_EXPRESS);
            }
        });
    }

    @Override
    public void onFragmentInteraction(String id) {

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
                    baseFragment = ChaiBaoListFragment.newInstance(packageId);
                    return baseFragment;

            }
            return ChaiBaoListFragment.newInstance(packageId);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "包裹内的快件信息".toUpperCase(l);

            }
            return null;
        }
    }





}
