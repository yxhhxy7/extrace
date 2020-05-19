package extrace.ui.domain;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ExpressReciveActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet>{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_recive);
        mIdView=findViewById(R.id.expressId);
        mSenderNameView=findViewById(R.id.expressSndName);
        mSenderTelView=findViewById(R.id.expressSndTel);
        mSenderDeptView=findViewById(R.id.SndExpressDept);
        mSendTimeView=findViewById(R.id.SndTime);
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

        mIdView.setText(es.getID());
        mSenderNameView.setText(es.getSender().getName());
        mSenderTelView.setText(es.getSender().getTelCode());
        mSenderDeptView.setText(es.getSender().getDepartment());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String date=sdf.format(es.getAccepteTime());
        // mSendTimeView.setText(date);
        mStatusView.setText(es.getStatus()+"");

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receive();
            }
        });
    }



    void Save(){
        /*mLoader = new ExpressLoader(this, this);
        mLoader.Save(mItem);*/
        if(mExpressSheetType.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入快件类型！", Toast.LENGTH_LONG).show();

            return ;
        } else {
            es.setType(Integer.parseInt(mExpressSheetType.getText().toString()));
        }

        if(mExpressSheetWeight.toString().equals("")){
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



        String res = JsonUtils.toJson(es);
        Log.d("!!!!!", res);
        res = res.trim();
        mLoader = new ExpressLoader(this, this);
        mLoader.New(res);
        // Toast.makeText(getApplicationContext(), "已成功创建快递单!", Toast.LENGTH_SHORT).show();

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


