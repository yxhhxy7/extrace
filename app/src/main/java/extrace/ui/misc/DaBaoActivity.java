package extrace.ui.misc;

import java.util.ArrayList;
import java.util.Locale;

import extrace.loader.DaBaoLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.TransPackage;
import extrace.misc.model.UserInfo;
import extrace.net.NotifyAdapter;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.main.MainActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class DaBaoActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<ExpressSheet>, NotifyAdapter<Boolean>,ExpressListFragment.OnFragmentInteractionListener {

//	public static final int INTENT_NEW = 1;
//	public static final int INTENT_EDIT = 2;

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_ADD_ES = 101;
    public static final int REQUEST_SOURCE_NODE = 103;
    public static final int REQUEST_TARGET_NODE = 104;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Button submitBtn;
    private Button addEsBtn;

    private ExpressSheet mItem = new ExpressSheet();

    private Intent mIntent;
    private DaBaoListFragment baseFragment;
    private TransPackage myPackage;
    private String packageId;
    private String esId;
    private MenuItem action_menu_item;
    private boolean new_es = false;	//新建
    private ArrayList<ExpressSheet> myEsList = new ArrayList<>();
    private TextView packageIdView;

    private ExpressSheet myEs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_da_bao);

        mIntent = getIntent();
        myPackage = (TransPackage) mIntent.getSerializableExtra("BarCode");
        Log.d("*******myPackage", myPackage.toString());

        //packageId = "111";

        packageId = myPackage.getID();
        packageIdView = findViewById(R.id.packageId);
        packageIdView.setText(" "+ packageId);
        init();
        /*if (mIntent.hasExtra("Action")) {
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
        }*/


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
        return null;
    }

    @Override
    public void setData(ExpressSheet data) {
        myEs = data;
        Log.d("#####", data.toString());
    }


    @Override
    public void notifyDataSetChanged() {
        if(myEsList.contains(myEs)){
            Toast.makeText(this, "此快件已在包裹中", Toast.LENGTH_SHORT).show();
            return ;
        }
        myEsList.add(myEs);
        /*
        ExpressSheet es=new ExpressSheet();
        CustomerInfo cu1=new CustomerInfo();
        cu1.setName("苏勋");
        cu1.setTelCode("111");
        cu1.setDepartment("sadsd");
        cu1.setAddress("sfs");
        CustomerInfo cu2=new CustomerInfo();
        cu2.setName("苏勋");
        cu2.setTelCode("111");
        cu2.setDepartment("sadsd");
        cu2.setAddress("sfs");
        es.setID("111");
        es.setRecever(cu1);
        es.setSender(cu2);
        es.setStatus(1);
        myEsList.add(es);

         */
        baseFragment.RefreshList(myEsList);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        if(requestCode == REQUEST_CAPTURE){
            if (data.hasExtra("BarCode")) {
                packageId = data.getStringExtra("BarCode");
            }
        } else if(requestCode == REQUEST_ADD_ES){
                if (data.hasExtra("BarCode")) {
                    esId = data.getStringExtra("BarCode");
                    ExpressLoader myLoader = new ExpressLoader(this, this);
                    myLoader.Load(esId);
                }
        }

    }

    /*void MenuDisplay(int status){
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
    }*/


    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    private void StartAddEs(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ES);
    }


    public void init(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        submitBtn = (Button) findViewById(R.id.submit_da_bao_btn);
        addEsBtn = (Button) findViewById(R.id.add_express_sheet);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.da_bao_pager);
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

                DaBao();
            }
        });

        addEsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartAddEs();
            }
        });
    }


    public void DaBao(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(DaBaoActivity.this);
        //获取AlertDialog对象
        dialog.setTitle("警告");//设置标题
        dialog.setMessage("确认打包吗？");//设置信息具体内容

        dialog.setCancelable(false);//设置是否可取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override//设置ok的事件
            public void onClick(DialogInterface dialogInterface, int i) {
                //在此处写入ok的逻辑
                confirmDaBao();
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

    public void confirmDaBao(){
        DaBaoLoader dbl = new DaBaoLoader(this, this);
        Log.d("*******myPackage", myPackage.toString());
        dbl.DaBao(myEsList, myPackage);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void notifyResult(Boolean b) {
        Log.d("*******myPackage", myPackage.toString());
        if(b == true){
            Toast.makeText(this, "打包成功！！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "打包失败！！", Toast.LENGTH_SHORT).show();
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
                    baseFragment = DaBaoListFragment.newInstance(packageId);
                    return baseFragment;

            }
            return DaBaoListFragment.newInstance(packageId);
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
                    return "需要打包的快件信息".toUpperCase(l);
                case 1:
                    return "节点信息".toUpperCase(l);
            }
            return null;
        }
    }



}
