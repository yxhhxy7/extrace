package extrace.ui.domain;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;
import extrace.ui.misc.RegionListActivity;
import zxing.util.CaptureActivity;

public class ExpressReciveActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet>, AdapterView.OnItemClickListener {

    private TextView mIdView;
    private TextView mSenderNameView;
    private TextView mSenderTelView;
    private TextView mSenderDeptView;
    private TextView mSendTimeView;
    private TextView mStatusView;
    private EditText mExpressSheetType;
    private EditText mExpressSheetWeight;
    private EditText mExpressSheetTransFee;
    private EditText mExpressSheetIsuFee;
    private Button btnReceive;

    private Intent mIntent;
    private ExpressSheet es;
    private ExpressSheet expressSheet;

    private Boolean bo=false;

    private ExpressLoader mLoader;

    private ListPopupWindow listPopupWindow;
    private String[] products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_recive);
        mIdView=findViewById(R.id.expressId);
        mSenderNameView=findViewById(R.id.expressSndName);
        mSenderTelView=findViewById(R.id.expressSndTel);
        mSenderDeptView=findViewById(R.id.SndExpressDept);
        //mSendTimeView=findViewById(R.id.SndTime);
        mExpressSheetType=findViewById(R.id.ExpressSheetType);
        mExpressSheetTransFee=findViewById(R.id.ExpressSheetTransFee);
        mExpressSheetIsuFee=findViewById(R.id.ExpressSheetIsuFee);
        mExpressSheetWeight=findViewById(R.id.ExpressSheetWeught);
        mStatusView=findViewById(R.id.ExpressSheetStatus);
        btnReceive=findViewById(R.id.btnReceive);

        mIntent=getIntent();

        if (mIntent.hasExtra("Action")) {


            if(mIntent.getStringExtra("Action").equals("Receive")){
                if (mIntent.hasExtra("ExpressSheet")) {
                    es = (ExpressSheet) mIntent.getSerializableExtra("ExpressSheet");
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
        String[] products={"Camera", "Laptop", "Watch","Smartphone",
                "Television"};
        listPopupWindow = new ListPopupWindow(
                ExpressReciveActivity.this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                R.layout.espresssheettype_list, products));
        listPopupWindow.setAnchorView(mExpressSheetType);
        listPopupWindow.setWidth(600);
        listPopupWindow.setHeight(800);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(ExpressReciveActivity.this);
        mExpressSheetType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });


        /*
        mExpressSheetType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (mExpressSheetType.getWidth() - mExpressSheetType
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        mExpressSheetType.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.pullup1), null);
                        showListPopulWindow();
                        return true;
                    }
                }
                return false;
            }
        });
        */


        mIdView.setText(es.getID());
        /*
        mSenderNameView.setText(es.getSender().getName());
        mSenderTelView.setText(es.getSender().getTelCode());
        mSenderDeptView.setText(es.getSender().getDepartment());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(es.getAccepteTime());
        mSendTimeView.setText(date);
        mStatusView.setText(es.getStatus()+"");
        */
        if(es.getSender().getName()!=null){
            mSenderNameView.setText(es.getSender().getName());
        }else{
            mSenderNameView.setText(" ");
        }
        if(es.getSender().getDepartment()!=null){
            mSenderDeptView.setText(es.getSender().getDepartment());
        }else{
            mSenderDeptView.setText(" ");
        }
        if(es.getSender().getTelCode()!=null){
            mSenderTelView.setText(es.getSender().getTelCode());
        }else{
            mSenderTelView.setText(" ");
        }

        if(es.getStatus()!=null){
            String stText = "";
            switch (es.getStatus()) {
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
            mStatusView.setText(stText);
            //mStatusView.setText(es.getStatus()+"");
        }else{
            mStatusView.setText(" ");
        }
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //receive();

                Save();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        String[] products={"Camera", "Laptop", "Watch","Smartphone",
                "Television"};
        mExpressSheetType.setText(products[position]);
        Log.d("woshiposition",products[position]);
        listPopupWindow.dismiss();
    }

    /*
    private void showListPopulWindow(){
        String[] list = { "item1", "item2", "item3", "item4" };
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));
        listPopupWindow.setAnchorView(mExpressSheetType);
        listPopupWindow.setModal(true);
        listPopupWindow.show();
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mExpressSheetType.setText(list[i]);

            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mExpressSheetType.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.pulldown1), null);
            }
        });
    }
    */
    void Save(){
        /*mLoader = new ExpressLoader(this, this);
        mLoader.Save(mItem);*/
        if(mExpressSheetType.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件类型！", Toast.LENGTH_LONG).show();

            return ;
        } else {
           // es.setType(Integer.parseInt(mExpressSheetType.getText().toString()));
        }

        if(mExpressSheetWeight.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件重量！", Toast.LENGTH_LONG).show();
            return ;
        } else {
           es.setWeight(Float.parseFloat(mExpressSheetWeight.getText().toString()));
        }

        if(mExpressSheetTransFee.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件费用！", Toast.LENGTH_LONG).show();
            return ;
        } else {
            es.setTranFee(Float.parseFloat(mExpressSheetTransFee.getText().toString()));
        }



        if(mExpressSheetIsuFee.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件保险金！", Toast.LENGTH_LONG).show();
            return ;
        } else{
            es.setInsuFee(Float.parseFloat(mExpressSheetIsuFee.getText().toString()));
        }


        /*
        String res = JsonUtils.toJson(es);
        Log.d("!!!!!", res);
        res = res.trim();
        mLoader = new ExpressLoader(this, this);
        mLoader.New(res);
        // Toast.makeText(getApplicationContext(), "已成功创建快递单!", Toast.LENGTH_SHORT).show();
        */
    }





    void receive(){
        mLoader=new ExpressLoader(this,this);
        mLoader.Receive(es.getID());
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
            Toast.makeText(getApplicationContext(),"快件已揽收完成",Toast.LENGTH_LONG).show();
        }
    }
}


