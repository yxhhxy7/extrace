package extrace.ui.domain;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import extrace.misc.model.PackageRoute;
import extrace.net.IDataAdapter;
import extrace.net.PositionAdapter;


public class MyService extends Service {

    private static final String TAG = "MyService";
    private LocalBinder mBinder = new LocalBinder();
    private static OnTraceListener mTraceListener = null;
    private static LBSTraceClient mTraceClient = null;
    private static Trace mTrace;
    private static ArrayList<PackageRoute> myRoute = new ArrayList<>();
    private static PositionAdapter<ArrayList<PackageRoute>> myAdapter;
    // 请求标识
    private static int tag = 1;
    // 轨迹服务ID
    private static long serviceId = 220293;
    // entityName
    private static String entityName = "myCar1";

    private  static long startTime;
    private  static long endTime;

    // 轨迹服务
    protected static Trace trace = null;

    // 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
    /*public static final long serviceId = 220293;


    // 轨迹服务类型
    //0 : 不建立socket长连接，
    //1 : 建立socket长连接但不上传位置数据，
    //2 : 建立socket长连接并上传位置数据）
    private int traceType = 2;

    // 轨迹服务客户端
    public static LBSTraceClient client = null;

    // Entity监听器
    public static OnEntityListener entityListener = null;

    // 开启轨迹服务监听器
    protected OnStartTraceListener startTraceListener = null;

    // 停止轨迹服务监听器
    protected static OnStopTraceListener stopTraceListener = null;

    // 采集周期（单位 : 秒）
    private int gatherInterval = 10;

    // 设置打包周期(单位 : 秒)
    private int packInterval = 20;

    protected static boolean isTraceStart = false;

    // 手机IMEI号设置为唯一轨迹标记号,只要该值唯一,就可以作为轨迹的标识号,使用相同的标识将导致轨迹混乱
    private String imei;*/


    public IBinder onBind(Intent arg0) {
        Log.d("*******bind", "成功");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*if(intent != null && intent.getExtras() != null){
            imei= intent.getStringExtra("imei");
        }*/
        Log.d("*******StartCommand", "成功");
        yingYanInit();
        startTrace();
        return super.onStartCommand(intent, flags, startId);
    }

    //被销毁时反注册广播接收器
    public void onDestroy() {
        super.onDestroy();
        stopTrace();
    }

    public void yingYanInit() {
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
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

    /**
     * 初始化
     */
    /*private void init() {
        // 初始化轨迹服务客户端
        client = new LBSTraceClient(this);

        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);

        // 初始化轨迹服务
        trace = new Trace(this, serviceId, imei, traceType);

// 采集周期,上传周期
        client.setInterval(gatherInterval, packInterval);

        // 设置http请求协议类型0:http,1:https
        client.setProtocolType(0);

        // 初始化监听器
        initListener();

        // 启动轨迹上传
        startTrace();
    }*/
    // 开启轨迹服务
    private void startTrace() {
        // 通过轨迹服务客户端client开启轨迹服务
        // client.startTrace(trace, startTraceListener);
        // 开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
        // 开启采集

        startTime = System.currentTimeMillis() / 1000;
    }

    // 停止轨迹服务
    public static void stopTrace() {
        /*// 通过轨迹服务客户端client停止轨迹服务
        Log.d(TAG, "stopTrace(), isTraceStart : " + isTraceStart);

        if(isTraceStart){
            client.stopTrace(trace, stopTraceListener);
        }*/
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
                        // trackPoints.add(new LatLng(trackPoint.getLocation().getLatitude(), trackPoint.getLocation().getLongitude()));
                        Log.d("*******route", points.toString());
                        Log.d("*****time", trackPoint.getCreateTime());
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try{
                            date = sdf.parse(trackPoint.getCreateTime());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        PackageRoute position = new PackageRoute();
                        position.setX(trackPoint.getLocation().getLatitude());
                        position.setY(trackPoint.getLocation().getLongitude());
                        position.setTm(date);
                        myRoute.add(position);
                    }

                    myAdapter.setPosition(myRoute);
                    /*OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(trackPoints);

                    //在地图上画出线条图层，mPolyline：线条图层
                    Overlay mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                    mPolyline.setZIndex(3);*/

                }
            };

            // 查询历史轨迹
            mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);

    }

    // 初始化监听器
   /* private void initListener() {

        initOnEntityListener();

        // 初始化开启轨迹服务监听器
        initOnStartTraceListener();

        // 初始化停止轨迹服务监听器
        initOnStopTraceListener();
    }*/


    /**
     * 初始化OnStartTraceListener
     */
    /*private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {

            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                Log.d(TAG, "开启轨迹回调接口 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
                if (0 == arg0 || 10006 == arg0) {
                    isTraceStart = true;
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            public void onTracePushCallback(byte arg0, String arg1) {
                Log.d(TAG, "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]");
            }
        };
    }

    // 初始化OnStopTraceListener
    private void initOnStopTraceListener() {
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                Log.d(TAG, "停止轨迹服务成功");
                isTraceStart = false;
                stopSelf();
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                Log.d(TAG, "停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
            }
        };
    }

    // 初始化OnEntityListener
    private void initOnEntityListener() {

        entityListener = new OnEntityListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {method stub
                Looper.prepare();
                Log.d(TAG, "entity请求失败回调接口消息 : " + arg0);
                Toast.makeText(getApplicationContext(), "entity请求失败回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 添加entity回调接口
            @Override
            public void onAddEntityCallback(String arg0) {
                Looper.prepare();
                Log.d(TAG, "添加entity回调接口消息 : " + arg0);
                Toast.makeText(getApplicationContext(), "添加entity回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                Log.d(TAG, "onQueryEntityListCallback : " + message);
            }

            @Override
            public void onReceiveLocation(TraceLocation location) {

            }
        };
    }*/

    public void startGather(){
        // 开启采集
        mTraceClient.startGather(mTraceListener);
    }



    public class LocalBinder extends Binder {
        public MyService getservices(){
            return MyService.this;
        }

        public void stop(){
            stopTrace();
        }

        public void setAdapter(PositionAdapter<ArrayList<PackageRoute>> adpt){
            myAdapter = adpt;
        }

    }

}

