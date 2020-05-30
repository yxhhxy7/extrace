package extrace.ui.domain;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Instant;

import extrace.loader.ExpressDeliveLoader;
import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.R;
import extrace.ui.misc.DaBaoActivity;
import zxing.util.CaptureActivity;

public class ExpressDeliveActivity extends AppCompatActivity implements IDataAdapter<Boolean> {

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
    private Button mbtnReady;

    private Intent mIntent;

    private ExpressSheet es;

    private CustomerInfo receiver;
    private CustomerInfo sender;

    private ExpressDeliveLoader mLoader;

    private Boolean mBoolean=true;

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_RCV = 101;
    public static final int REQUEST_SND = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_delive);
        mIDView = findViewById(R.id.expressId);
        mRcvNameView = findViewById(R.id.expressRcvName);
        mRcvTelCodeView =findViewById(R.id.expressRcvTel);
        mRcvAddrView = findViewById(R.id.expressRcvAddr);
        //mRcvDptView = (TextView) rootView.findViewById(R.id.expressRcvDpt);
        //mRcvRegionView = (TextView) rootView.findViewById(R.id.expressRcvRegion);

        mRcvDptView = findViewById(R.id.RcvExpressDept);
        mSndDptView = findViewById(R.id.SndExpressDept);
        mSndNameView = findViewById(R.id.expressSndName);
        mSndTelCodeView = findViewById(R.id.expressSndTel);
        mSndAddrView =findViewById(R.id.expressSndAddr);
        //mSndDptView = (TextView) rootView.findViewById(R.id.expressSndDpt);
        //mSndRegionView = (TextView) rootView.findViewById(R.id.expressSndRegion);

        mRcvTimeView = findViewById(R.id.expressAccTime);
        mSndTimeView = findViewById(R.id.expressDlvTime);

        mStatusView = findViewById(R.id.expressStatus);

        mbtnDelive=findViewById(R.id.btnDelivry);

        mIntent=getIntent();

        if (mIntent.hasExtra("Action")) {


             if(mIntent.getStringExtra("Action").equals("Delive")){
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

        receiver=es.getRecever();
        sender=es.getSender();
        mIDView.setText(es.getID());
        mRcvNameView.setText(receiver.getName()+"");
        mRcvTelCodeView.setText(receiver.getTelCode()+"");
        //mRcvRegionView.setText(receiver.getRegionCode()+"");
        mRcvAddrView.setText(receiver.getAddress());
        mRcvDptView.setText(receiver.getDepartment());
        mSndNameView.setText(sender.getName()+"");
        mSndTelCodeView.setText(sender.getTelCode()+"");
       // mSndRegionView.setText(sender.getRegionString()+"");
        mSndAddrView.setText(sender.getAddress());
        mSndDptView.setText(sender.getDepartment());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(es.getAccepteTime());

        mRcvTimeView.setText(date);

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
                stText = "已送达";
                break;
        }
        mStatusView.setText(stText);
        //mStatusView.setText(es.getStatus()+"");
        //mLoader=new ExpressDeliveLoader(this,this);


        mbtnDelive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(ExpressDeliveActivity.this);
                //获取AlertDialog对象
                dialog.setTitle("警告");//设置标题
                dialog.setMessage("确认送达吗？");//设置信息具体内容

                dialog.setCancelable(false);//设置是否可取消
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override//设置ok的事件
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //在此处写入ok的逻辑
                        completeDispatch();
                        String jj= JsonUtils.toJson(es, true);
                        Log.d("派送任务的es",jj);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override//设置取消事件
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //在此写入取消的事件
                    }

                });
                dialog.show();
                //mLoader=new ExpressLoader(IDataAdapter<boolean>,A);
                //mLoader=new ExpressDeliveLoader(this,this);

                //Toast.makeText(getApplicationContext(), "您已完成派送!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    public void readyDispatch(){

        mLoader=new ExpressDeliveLoader(this,this);
        mLoader.ReadyDispatch(es.getID());
    }
    */
    public void completeDispatch(){

        mLoader=new ExpressDeliveLoader(this,this);
        mLoader.Dispatch(es.getID());
    }

    @Override
    public Boolean getData() {
        return null;
    }

    @Override
    public void setData(Boolean data) {
        mBoolean=data;
    }

    @Override
    public void notifyDataSetChanged() {

            if(mBoolean==true){
                Toast.makeText(getApplicationContext(), "您已完成派送!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "运单未完成派送 请检查运单状态并重试!", Toast.LENGTH_LONG).show();
            }


    }



}
