package extrace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import extrace.ui.domain.ExpressDeliveActivity;
import extrace.ui.domain.ExpressDispatchActivity;
import extrace.ui.domain.ExpressEditActivity;
import extrace.ui.domain.ExpressQueryActivity;
import extrace.ui.domain.StartExpressActivity;
import extrace.ui.misc.ChaiBaoActivity;
import extrace.ui.misc.CustomerListActivity;
import extrace.ui.misc.DaBaoActivity;
import extrace.ui.misc.MapActivity;
import extrace.ui.misc.NewPackageActivity;
import extrace.ui.misc.ZhuanYunActivity;

public class MainFragment  extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //fragment_main中的操作激发
        rootView.findViewById(R.id.action_ex_receive_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartReceiveExpress();
                    }
                });
        rootView.findViewById(R.id.action_ex_receive).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartReceiveExpress();
                    }
                });
        rootView.findViewById(R.id.action_ex_transfer_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartDispatchExpress();
                    }
                });
        rootView.findViewById(R.id.action_ex_transfer).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartDispatchExpress();
                        //StartDeliveExpress();
                    }
                });


        //包裹拆包和打包在这里
        rootView.findViewById(R.id.chai_bao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChaiBao();
            }
        });

        rootView.findViewById(R.id.da_bao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaBao();
            }
        });

        // 快递转运
        rootView.findViewById(R.id.zhuan_yun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZhuanYun();
            }
        });

        // 客户管理和快件查询
        rootView.findViewById(R.id.action_cu_mng_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartCustomerList();
                    }
                });
        rootView.findViewById(R.id.action_cu_mng).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartCustomerList();
                    }
                });

        rootView.findViewById(R.id.action_ex_qur_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartQueryExpress();
                    }
                });
        rootView.findViewById(R.id.action_ex_qur).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartQueryExpress();
                    }
                });

        // 路径查询
        rootView.findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Action","None");
                intent.setClass(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    void StartReceiveExpress()
    {
        Intent intent = new Intent();
        intent.putExtra("Action","New");
        intent.setClass(this.getActivity(), StartExpressActivity.class);
        startActivityForResult(intent, 0);
    }

    void StartDispatchExpress()
    {
        Intent intent = new Intent();
        intent.putExtra("Action","Query");
        intent.setClass(this.getActivity(), ExpressDispatchActivity.class);
        startActivityForResult(intent, 0);
    }

    void StartQueryExpress()
    {
        Intent intent = new Intent();
        intent.putExtra("Action","Query");
        intent.setClass(this.getActivity(), ExpressQueryActivity.class);
        startActivityForResult(intent, 0);
    }

    void StartCustomerList()
    {
        Intent intent = new Intent();
        intent.putExtra("Action","None");
        intent.setClass(this.getActivity(), CustomerListActivity.class);
        startActivityForResult(intent, 0);
    }

    void ChaiBao(){
        Intent intent = new Intent();
        intent.putExtra("Action","None");
        intent.setClass(this.getActivity(), ChaiBaoActivity.class);
        startActivityForResult(intent, 0);
    }

    void DaBao(){
        Intent intent = new Intent();
        // intent.putExtra("Action","None");
        intent.setClass(this.getActivity(), NewPackageActivity.class);
        // startActivityForResult(intent, 0);
        startActivity(intent);
    }

    void StartDeliveExpress()
    {
        Intent intent = new Intent();
        intent.putExtra("Action","Query");
        intent.setClass(this.getActivity(), ExpressDeliveActivity.class);
        startActivity(intent);
    }


    void ZhuanYun(){
        Intent intent = new Intent();
        intent.putExtra("Action","None");
        intent.setClass(this.getActivity(), ZhuanYunActivity.class);
        startActivityForResult(intent, 0);
    }

}
