package extrace.ui.misc;

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

import java.util.ArrayList;
import java.util.Locale;

import extrace.loader.TransPackageLoader;
import extrace.loader.ZhuanYunLoader;
import extrace.misc.model.TransPackage;
import extrace.misc.model.TransPackages;
import extrace.net.NotifyAdapter;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class ZhuanYunActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<TransPackage>, NotifyAdapter<Boolean>,ExpressListFragment.OnFragmentInteractionListener {

//	public static final int INTENT_NEW = 1;
//	public static final int INTENT_EDIT = 2;

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_ADD_TRANS = 101;
    public static final String TAG = "ZhuanYun";

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Button submitBtn;
    private Button addTpBtn;


    private Intent mIntent;
    private ZhuanYunListFragment baseFragment;
    private String packageId;
    private MenuItem action_menu_item;
    private ArrayList<TransPackage> myTpList = new ArrayList<>();

    private ZhuanYunLoader myLoader;
    private TransPackage myTp;
    private TransPackages myTps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zhuan_yun);

        mIntent = getIntent();
        if (mIntent.hasExtra("Action")) {
            if(mIntent.getStringExtra("Action").equals("None")){
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
    public TransPackage getData() {
        return null;
    }

    @Override
    public void setData(TransPackage data) {
        myTp = data;
    }


    @Override
    public void notifyDataSetChanged() {
        myTpList.add(myTp);
        baseFragment.RefreshList(myTpList);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        Log.d(TAG, "123");
        if(requestCode == REQUEST_CAPTURE){
            if (data.hasExtra("BarCode")) {
                packageId = data.getStringExtra("BarCode");
                Log.d(TAG, packageId);
                init();
            }
        } else {
            if(requestCode == REQUEST_ADD_TRANS){
                if (data.hasExtra("BarCode")) {
                    packageId = data.getStringExtra("BarCode");
                    QueryTp();
                }
            }
        }

    }


    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    private void StartAddTrans(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_ADD_TRANS);
    }

    public void QueryTp(){
        TransPackageLoader myLoader = new TransPackageLoader(this, this);
        myLoader.Load(packageId);
    }

    public void init(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        submitBtn = (Button) findViewById(R.id.submit_zhuan_yun_btn);
        addTpBtn = (Button) findViewById(R.id.add_trans_package);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.zhuan_yun_pager);
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

                ZhuanYun();
            }
        });

        addTpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartAddTrans();
            }
        });

        QueryTp();
    }

    public void ZhuanYun(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(ZhuanYunActivity.this);
        //获取AlertDialog对象
        dialog.setTitle("警告");//设置标题
        dialog.setMessage("确认转运这些包裹吗？");//设置信息具体内容

        dialog.setCancelable(false);//设置是否可取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override//设置ok的事件
            public void onClick(DialogInterface dialogInterface, int i) {
                //在此处写入ok的逻辑
                confirmZhuanYun();
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

    public void confirmZhuanYun(){
         myLoader = new ZhuanYunLoader(this, this);
         myTps = new TransPackages();
        ((ExTraceApplication)this.getApplication()).refresh();
         myTps.setTelCode(((ExTraceApplication)this.getApplication()).getLoginUser().getTelCode());
         ArrayList<String> res = new ArrayList<>();
         for(TransPackage tp : myTpList){
             res.add(tp.getID());
         }
         myTps.setTranspackages(res);
         myLoader.ZhuanYun(myTps);

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void notifyResult(Boolean b) {
        if(b == true){
            Toast.makeText(this, "接受转运任务成功！！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "接受转运任务失败！！", Toast.LENGTH_SHORT).show();
        }
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
                    baseFragment = ZhuanYunListFragment.newInstance(packageId);
                    return baseFragment;

            }
            return ZhuanYunListFragment.newInstance(packageId);
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
                    return "要转运的包裹".toUpperCase(l);

            }
            return null;
        }
    }




}
