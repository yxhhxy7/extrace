package extrace.ui.misc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import extrace.loader.QueryRouteLoader;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.RouteItem;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class MapActivity extends Activity implements IDataAdapter<List<TransPackage>> {

    public static final int REQUEST_CAPTURE = 100;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ListView myListView;
    private List<LatLng> trackPoints = new ArrayList<>();
    private OnTraceListener mTraceListener = null;
    private LBSTraceClient mTraceClient = null;
    private Intent mIntent;
    private String sheetId;
    private ArrayList<TransPackage> myRoute;
    ArrayList<RouteItem> myList = new ArrayList<>();


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

        myListView = (ListView) findViewById(R.id.route_listView);

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

        // yingYanInit();

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_CANCELED) return ;
        if (data.hasExtra("BarCode")) {
            sheetId = data.getStringExtra("BarCode");
            init();

        }
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

                //定位位置回调
                /**
                 * MyLocationData 定位数据类，地图上的定位位置需要经纬度、精度、方向这几个参数，所以这里我们把这个几个参数传给地图
                 * 如果不需要要精度圈，推荐builder.accuracy(0);
                 * mCurrentDirection 是通过手机方向传感器获取的方向；也可以设置option.setNeedDeviceDirect(true)获取location.getDirection()，
                 但是这不会时时更新位置的方向，因为周期性请求定位有时间间隔。
                 * location.getLatitude()和location.getLongitude()经纬度，如果你只需要在地图上显示某个固定的位置，完全可以写入固定的值，
                 如纬度36.958454，经度114.137588，这样就不要要同过定位sdk来获取位置了
                 */
                /*MyLocationData locData = new MyLocationData.Builder().accuracy((float) location.getRadius())
                        .direction((float) location.getRadius()).
                        latitude(location.getLatitude()).longitude(location.getLongitude()).build();

                mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了*/

                /**
                 *当首次定位时，记得要放大地图，便于观察具体的位置
                 * LatLng是缩放的中心点，这里注意一定要和上面设置给地图的经纬度一致；
                 * MapStatus.Builder 地图状态构造器
                 */
                /*if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    //设置缩放中心点；缩放比例；
                    builder.target(ll);
                    //给地图设置状态
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }*/
            }
        };
        //时时定位设备当前位置，定位信息不会存储在轨迹服务端，即不会形成轨迹信息,只用于在MapView显示当前位置
        mTraceClient.queryRealTimeLoc(locRequest, entityListener);//这里只会一次定位,多次定位使Handler.postDelayed(Runnable, interval)实现;

        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        // 定位周期(单位:秒)
        int gatherInterval = 2;
        // 打包回传周期(单位:秒)
        int packInterval = 4;
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

        /*start.setOnClickListener(new View.OnClickListener() {
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
        });*/



    }

    public void init(){
        QueryRouteLoader myLoader = new QueryRouteLoader(this, this);
        myLoader.QueryRoute(sheetId);
    }

    public void displayData(){
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(trackPoints);

        //在地图上画出线条图层，mPolyline：线条图层
        Overlay mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
    }

    public void addressData(){
        ArrayList<PackageRoute> route = new ArrayList<>();
        for(TransPackage tp : myRoute){
            RouteItem ri = new RouteItem();
            ri.setTime(tp.getCreateTime());
            ri.setFrom(tp.getSourceNode());
            ri.setTo(tp.getTargetNode());
            Log.d("**********ri", ri.toString());
            myList.add(ri);
            for(PackageRoute pr: tp.getRoute()){
                route.add(pr);
            }
        }

        Collections.sort(route, new Comparator<PackageRoute>() {
            @Override
            public int compare(PackageRoute packageRoute, PackageRoute t1) {
                return packageRoute.getTm().compareTo(t1.getTm());
            }
        });

        for(PackageRoute pr:route){
            Log.d("********after address", pr.toString());
        }

        for(PackageRoute pr:route){
            trackPoints.add(new LatLng(pr.getX(), pr.getY()));
        }

        setListAdapter();
        displayData();
    }

    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    public void setListAdapter(){
        Log.d("*******ada", myList.toString());
        RouteDisplayAdapter myAdapter = new RouteDisplayAdapter(this, R.layout.route_list_item, myList);
        myListView.setAdapter(myAdapter);
    }

    public void startGather(){
        // 开启采集
        mTraceClient.startGather(mTraceListener);
    }

    @Override
    public List<TransPackage> getData() {
        return null;
    }

    @Override
    public void setData(List<TransPackage> data) {
        myRoute = (ArrayList<TransPackage>) data;
        addressData();
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
