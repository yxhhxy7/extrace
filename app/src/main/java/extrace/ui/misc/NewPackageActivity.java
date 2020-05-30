package extrace.ui.misc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import extrace.loader.TransPackageLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.domain.ExpressReciveActivity;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class NewPackageActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<TransPackage>, ExpressListFragment.OnFragmentInteractionListener {

    public static final int REQUEST_SOURCE_NODE = 100;
    public static final int REQUEST_TARGET_NODE = 101;
    public static final int REQUEST_CAPTURE = 102;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Button submitBtn;



    private Intent mIntent;
    private NodeFragment baseFragment;
    private String packageId = null;
    private MenuItem action_menu_item;
    private TransPackage myPackage = new TransPackage();
    private TextView packageView;
    private TransPackageLoader myLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_package);

        /*mIntent = getIntent();
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
        }*/




        init();

        StartCapture();



    }




/*
    private void tiaozhuan(){

        myPackage.setID(packageId);
        Toast.makeText(this, packageId, Toast.LENGTH_SHORT).show();
        //Log.d("ppppp",packageId);
        Intent intent = new Intent();
        intent.putExtra("BarCode",myPackage);
        Log.d("*******myPackage", myPackage.toString());
        intent.setClass(this, DaBaoActivity.class);
        startActivity(intent);


    }

 */

    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
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
        myPackage.setID(data.getID());
        myPackage.setCreateTime(data.getCreateTime());
        myPackage.setStatus(data.getStatus());
        packageId = myPackage.getID();
        Log.d("$$$$$", data.toString());
        if(myLoader.f){
            myPackage.setSourceNode(data.getSourceNode());
            myPackage.setTargetNode(data.getTargetNode());

        }
    }


    @Override
    public void notifyDataSetChanged() {
        if(myLoader.f){

            //Toast.makeText(this, "包裹已存在  请添加快件！！", Toast.LENGTH_SHORT).show();
            myPackage.setID(packageId);
            //Toast.makeText(this, myPackage.getSourceNode(), Toast.LENGTH_SHORT).show();
            baseFragment.sourceNode.setText(myPackage.getSourceNode());
            baseFragment.targetNode.setText(myPackage.getTargetNode());
            Log.d("*******myPackage2", myPackage.toString());

            if(myPackage.getStatus()==0){

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewPackageActivity.this,"包裹已存在  无法新建",Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

                AlertDialog.Builder dialog=new AlertDialog.Builder(NewPackageActivity.this);
                //获取AlertDialog对象
                dialog.setTitle("警告");//设置标题
                dialog.setMessage("包裹已存在 是否继续添加快件？");//设置信息具体内容

                dialog.setCancelable(false);//设置是否可取消
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override//设置ok的事件
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //在此处写入ok的逻辑



                        Intent intent = new Intent();
                        intent.putExtra("BarCode",myPackage);
                        Log.d("*******myPackage", myPackage.toString());
                        intent.setClass(NewPackageActivity.this, DaBaoActivity.class);
                        startActivity(intent);

                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override//设置取消事件
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //在此写入取消的事件
                    }

                });
                dialog.show();
                /*
                Toast.makeText(this, "包裹已存在  请添加快件！！", Toast.LENGTH_SHORT).show();
                myPackage.setID(packageId);
                //Toast.makeText(this, myPackage.getSourceNode(), Toast.LENGTH_SHORT).show();
                baseFragment.sourceNode.setText(myPackage.getSourceNode());
                baseFragment.targetNode.setText(myPackage.getTargetNode());
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewPackageActivity.this,"包裹已存在  无法新建",Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
                Intent intent = new Intent();
                intent.putExtra("BarCode",myPackage);
                Log.d("*******myPackage", myPackage.toString());
                intent.setClass(this, DaBaoActivity.class);
                startActivity(intent);

                 */
            }
            else{
                Toast.makeText(this, "包裹无法添加快件！！", Toast.LENGTH_SHORT).show();
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewPackageActivity.this,"包裹正在装运中",Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        }else{
            if(packageId != null){
                Toast.makeText(this, "包裹新建成功！！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("BarCode",myPackage);
                Log.d("*******myPackage", myPackage.toString());
                intent.setClass(this, DaBaoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "包裹新建失败！！", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        if (data.hasExtra("BarCode")) {

            packageId = data.getStringExtra("BarCode");
            packageView.setText(packageId);
            Log.d("ChaiBaoActivity", packageId);
            // init();
        }else if(requestCode == REQUEST_SOURCE_NODE){
            if(data.hasExtra("NodeInfo")){
                TransNode tn = (TransNode)data.getSerializableExtra("NodeInfo");
                baseFragment.sourceNode.setText(tn.getNodeName());
                myPackage.setSourceNode(tn.getID());
            }
        } else if(requestCode == REQUEST_TARGET_NODE){
            TransNode tn = (TransNode)data.getSerializableExtra("NodeInfo");
            baseFragment.targetNode.setText(tn.getNodeName());
            myPackage.setTargetNode(tn.getID());
        }

        myLoader = new TransPackageLoader(this,this);
        myLoader.getTransPackage(packageId);

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

    public void GetNode(int intent_code) {
        Intent intent = new Intent();
        intent.setClass(this, NodeListActivity.class);
        intent.putExtra("Action","New");
        startActivityForResult(intent, intent_code);
    }

    public void NewPackage(){



            if(baseFragment.sourceNode.getText().toString().equals("")){
                Toast.makeText(this, "请输入源结点信息！", Toast.LENGTH_SHORT).show();
                return ;
            }

            if(baseFragment.targetNode.getText().toString().equals("")){
                Toast.makeText(this, "请输入目标结点信息！", Toast.LENGTH_SHORT).show();
                return ;
            }
            myLoader = new TransPackageLoader(this,this);
            myLoader.NewPackage(packageId);


    }

    public void init(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        submitBtn = (Button) findViewById(R.id.new_package_btn);

        packageView  = (TextView) findViewById(R.id.NewPackageId);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.new_package_pager);
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
                NewPackage();
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
                    baseFragment = NodeFragment.newInstance();
                    return baseFragment;

            }
            return NodeFragment.newInstance();
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
                    return "包裹信息".toUpperCase(l);

            }
            return null;
        }
    }

    public static class NodeFragment extends Fragment {

        private TextView sourceNode;
        private TextView targetNode;
        private ImageView querySn;
        private ImageView queryTn;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static NodeFragment newInstance() {
            NodeFragment fragment = new NodeFragment();
//			Bundle args = new Bundle();
//			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//			fragment.setArguments(args);
            return fragment;
        }

        public NodeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_node,
                    container, false);
            sourceNode = (TextView) rootView.findViewById(R.id.sourceNode);
            sourceNode.setEnabled(false);
            targetNode = (TextView) rootView.findViewById(R.id.targetNode);
            targetNode.setEnabled(false);
            querySn = (ImageView) rootView.findViewById(R.id.query_source_node);
            queryTn = (ImageView) rootView.findViewById(R.id.query_target_node);

            querySn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NewPackageActivity)getActivity()).GetNode(REQUEST_SOURCE_NODE);
                }
            });

            queryTn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NewPackageActivity)getActivity()).GetNode(REQUEST_TARGET_NODE);
                }
            });

            return rootView;
        }
    }



}
