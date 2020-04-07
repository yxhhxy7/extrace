package extrace.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import extrace.loader.LoginAndRegisterLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, IDataAdapter<String> {
    String APPKEY = "2eac8d3687ec2";
    String APPSECRET = "eebb2d575b29525b75128519ab1af066";
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText telephone;
    private EditText captcha;
    private CheckBox agree;
    private Button getCaptcha;
    private Button register;
    private String status = "-1";

    private int i = 30;//计时器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        initView();
        MobSDK.init(this, APPKEY, APPSECRET);
        MobSDK.submitPolicyGrantResult(true, null);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                Message message = new Message();
                message.arg1 = i;
                message.arg2 = i1;
                message.obj = o;
                handler.sendMessage(message);
            }
            //注册回调监听接口
        };
        SMSSDK.registerEventHandler(eventHandler);
        Log.d("!!!!!", "!!!!!");


    }

    public void initView(){
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        telephone = (EditText) findViewById(R.id.register_telephone);
        captcha = (EditText) findViewById(R.id.register_captcha);
        agree = (CheckBox) findViewById(R.id.register_checkBox);
        getCaptcha = (Button) findViewById(R.id.register_captcha_button);
        register = (Button) findViewById(R.id.btn_register);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == -9) {
                getCaptcha.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                getCaptcha.setText("获取验证码");
                getCaptcha.setClickable(true);
                i = 30;
            } else {
                int i = msg.arg1;
                int i1 = msg.arg2;
                Object o = msg.obj;
                if (i1 == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (i == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        Toast.makeText(RegisterActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
                        registerToServer();

                    } else if (i == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                        Toast.makeText(RegisterActivity.this, "正在获取验证码", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };



    @Override
    public void onClick(View view) {
        String phoneNum = telephone.getText().toString();
        switch (view.getId()){
            case R.id.register_captcha_button:
                Log.d("&&&&&&&&", "phoneNum");
                // 1. 判断手机号是不是11位并且看格式是否合理
                if (!judgePhoneNums(phoneNum)){
                    return;
                }
                // 2. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86",phoneNum);
                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                getCaptcha.setClickable(false);
                getCaptcha.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for ( ; i  > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0 ){
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;

            case R.id.btn_register:
                Log.d("&&&&&&&&", "phoneNum");
                if(username.getText().toString().equals("")){
                    Toast.makeText(this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if (telephone.getText().toString().equals("")){
                    Toast.makeText(this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (captcha.getText().toString().equals("")){
                    Toast.makeText(this,"验证码不能为空！",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(password.getText().toString().equals("")){
                    Toast.makeText(this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(this,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!agree.isChecked()){
                    Toast.makeText(this,"请同意隐私条款！",Toast.LENGTH_SHORT).show();
                    return;
                }

                registerToServer();
                //将收到的验证码和手机号提交再次核对
                SMSSDK.submitVerificationCode("86", phoneNum, captcha
                        .getText().toString());
                break;
        }
    }

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        //反注册回调监听接口
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    public void registerToServer(){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(username.getText().toString());
        userInfo.setTelCode(telephone.getText().toString());
        userInfo.setPWD(password.getText().toString());

        Log.d("^^^^^^^", userInfo.toString());
        LoginAndRegisterLoader myLoader = new LoginAndRegisterLoader(this, userInfo,  this);
        myLoader.register(userInfo);
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public void setData(String data) {
        this.status = data;

    }

    @Override
    public void notifyDataSetChanged() {
        if(status.equals("1")){
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        }else if(status.equals("2")){
            Toast.makeText(this, "手机号已被注册！", Toast.LENGTH_SHORT).show();
        }
    }
}
