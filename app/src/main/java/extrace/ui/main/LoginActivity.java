package extrace.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.LoginAndRegisterLoader;
import extrace.misc.model.UserInfo;
import extrace.net.CodeUtils;
import extrace.net.IDataAdapter;
import extrace.net.LoginDataAdapter;

public class LoginActivity extends /*AppCompatActivity*/ Activity implements LoginDataAdapter<String> {

    private Integer status = -1;
    private Button loginBtn;
    private TextView registerBtn;
    private EditText username;
    private EditText password;
    private EditText code;
    private ImageView codeView;
    private CheckBox rememberAcc;
    private CheckBox rememberPwd;
    private CodeUtils codeUtils = CodeUtils.getInstance();
    private UserInfo user = new UserInfo();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        editor = sp.edit();

        loginBtn = (Button) findViewById(R.id.btn_login);
        registerBtn = (TextView) findViewById(R.id.btn_register);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        code = (EditText) findViewById(R.id.et_code);
        codeView = (ImageView) findViewById(R.id.et_code_view);
        rememberAcc = (CheckBox) findViewById(R.id.remember_acc);
        rememberPwd = (CheckBox) findViewById(R.id.remember_pwd);

        init();

    }

    public void init(){
        // 显示之前要保存的账号或者密码信息
        if(sp.getString("rememberAcc",null) != null){
            String Acc = sp.getString("rememberAcc",null);
            if(Acc.equals("1")){
                rememberAcc.setChecked(true);
                username.setText(sp.getString("telCode", null));
            }
        }

        if(sp.getString("rememberPwd",null) != null){
            String Acc = sp.getString("rememberPwd",null);
            if(Acc.equals("1")){
                rememberPwd.setChecked(true);
                password.setText(sp.getString("PWD", null));
            }
        }

        refreshCode();

        // 登录的按钮监听
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

        // 注册验证码
        codeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshCode();
            }
        });

        // 注册按钮监听
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void login(){
        String telephone;
        String pwd;
        String cde;
        Log.d("0000000000", "ceshi1");
        if(username.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入手机号！", Toast.LENGTH_LONG).show();
            return;
        }else{
            user.setTelCode(username.getText().toString());
            telephone = username.getText().toString();
        }

        if(password.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(), "请输入密码！", Toast.LENGTH_LONG).show();
            return;
        }else{
            user.setPWD(password.getText().toString());
            pwd = password.getText().toString();
        }

        if(code.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入验证码！", Toast.LENGTH_LONG).show();
            return;
        }else{
            cde = code.getText().toString().toLowerCase();
        }

        if(!cde.equals(codeUtils.getCode())){
            Toast.makeText(getApplicationContext(), "验证码错误！请重新输入！", Toast.LENGTH_LONG).show();
            refreshCode();
            return;
        }

        LoginAndRegisterLoader myLoader = new LoginAndRegisterLoader(this, user,  this);
        myLoader.login(telephone, pwd);

    }

    public void refreshCode(){
        Bitmap bitmap = codeUtils.createBitmap();
        codeView.setImageBitmap(bitmap);
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public void setData(Object data) {
        status = Integer.parseInt((String) data);
    }


    @Override
    public void notifyDataSetChanged() {

        if(rememberAcc.isChecked()){
            editor.putString("telCode", username.getText().toString());
            editor.putString("rememberAcc", "1");
        }else{
            editor.putString("rememberAcc", "0");
        }

        if(rememberPwd.isChecked()){
            editor.putString("PWD", password.getText().toString());
            editor.putString("rememberPwd", "1");
        }else{
            editor.putString("rememberPwd", "0");
        }

        editor.putString("receivePackageID", user.getReceivePackageID());
        editor.putString("delivePackageID", user.getDelivePackageID());
        editor.putString("transPackageID", user.geTransPackageID());
        editor.putString("UID", Integer.toString(user.getUID()));

        editor.commit();

        if(status.equals(0)){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else if(status.equals(1)){
            Toast.makeText(getApplicationContext(), "密码错误请重新输入!", Toast.LENGTH_LONG).show();
            refreshCode();
        }else if(status.equals(2)){
            Toast.makeText(getApplicationContext(), "用户不存在!", Toast.LENGTH_LONG).show();
            refreshCode();
        }
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        user = userInfo;
        Log.d("++++++++++++++++", user.toString());
    }
}
