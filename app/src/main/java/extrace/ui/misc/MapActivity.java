package extrace.ui.misc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.TraceLocation;
import com.baidu.trace.model.TransportMode;

import java.util.ArrayList;
import java.util.List;

import extrace.ui.main.R;

public class MapActivity extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button start;
    private Button end;
    long startTime;
    long endTime;
    private List<LatLng> trackPoints = new ArrayList<>();
    private OnTraceListener mTraceListener = null;
    private LBSTraceClient mTraceClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 该方法要在 setContentView之前
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        setContentView(R.layout.map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        start = (Button) findViewById(R.id.button3);
        end = (Button) findViewById(R.id.button4);

        yingYanInit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    public void yingYanInit() {
        // 请求标识
        int tag = 1;
        // 轨迹服务ID
        long serviceId = 220293;
        // entityName
        String entityName = "myCar1";
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        Trace mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());

        //定位请求参数类
        LocRequest locRequest = new LocRequest(serviceId);
        OnEntityListener entityListener = new OnEntityListener() {
            @Override
            public void onReceiveLocation(TraceLocation location) {
                //将回调的当前位置location显示在地图MapView上，地图显示位置不清楚的可以篇头阅读百度文章(二)
                //这里位置点的返回间隔时间为Handler.postDelayed的延时时间
            }
        };
        //时时定位设备当前位置，定位信息不会存储在轨迹服务端，即不会形成轨迹信息,只用于在MapView显示当前位置
        mTraceClient.queryRealTimeLoc(locRequest, entityListener);//这里只会一次定位,多次定位使Handler.postDelayed(Runnable, interval)实现;

        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        // 定位周期(单位:秒)
        int gatherInterval = 2;
        // 打包回传周期(单位:秒)
        int packInterval = 8;
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        // 初始化轨迹服务监听器
        mTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {

            }

            // 开启服务回调
            @Override
            public void onStartTraceCallback(int status, String message) {
                Log.d("@@@@@@@@", message);
                startGather();
            }
            // 停止服务回调
            @Override
            public void onStopTraceCallback(int status, String message) {
                Log.d("@@@@@@@@@@", message);
            }
            // 开启采集回调
            @Override
            public void onStartGatherCallback(int status, String message) {
                Log.d("@@@@@@@@@@@@@@@@", message);
            }
            // 停止采集回调
            @Override
            public void onStopGatherCallback(int status, String message) {
                Log.d("@@@@@@@@@@@@@", message);
            }
            // 推送回调
            @Override
            public void onPushCallback(byte messageNo, PushMessage message) {}

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 开启服务
                mTraceClient.startTrace(mTrace, mTraceListener);
                // 开启采集

                startTime = System.currentTimeMillis() / 1000;
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止采集
                mTraceClient.stopGather(mTraceListener);
                endTime = System.currentTimeMillis() / 1000;
                // 创建历史轨迹请求实例
                HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag, serviceId, entityName);
                ProcessOption processOption = new ProcessOption();//纠偏选项
                processOption.setRadiusThreshold(50);//精度过滤
                processOption.setTransportMode(TransportMode.walking);//交通方式，默认为驾车
                processOption.setNeedDenoise(true);//去噪处理，默认为false，不处理
                processOption.setNeedVacuate(true);//设置抽稀，仅在查询历史轨迹时有效，默认需要false
                processOption.setNeedMapMatch(true);//绑路处理，将点移到路径上，默认不需要false
                historyTrackRequest.setProcessOption(processOption);
                // 设置开始时间
                historyTrackRequest.setStartTime(startTime);
                // 设置结束时间
                historyTrackRequest.setEndTime(endTime);

                // 初始化轨迹监听器
                OnTrackListener mTrackListener = new OnTrackListener() {
                    // 历史轨迹回调
                    @Override
                    public void onHistoryTrackCallback(HistoryTrackResponse response) {
                        List<TrackPoint> points = response.getTrackPoints();//获取轨迹点
                        for (TrackPoint trackPoint : points) {
                            //将轨迹点转化为地图画图层的LatLng类
                            trackPoints.add(new LatLng(trackPoint.getLocation().getLatitude(), trackPoint.getLocation().getLongitude()));
                            Log.d("@@@@", points.toString());
                        }

                        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(trackPoints);

                        //在地图上画出线条图层，mPolyline：线条图层
                        Overlay mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        mPolyline.setZIndex(3);
                    }
                };

                // 查询历史轨迹
                mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
            }
        });



    }

    public void startGather(){
        // 开启采集
        mTraceClient.startGather(mTraceListener);
    }
}
