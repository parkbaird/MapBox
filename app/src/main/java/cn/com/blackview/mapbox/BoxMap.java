//package cn.com.blackview.azdome.map;
//
//import android.animation.ObjectAnimator;
//import android.animation.TypeEvaluator;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.dueeeke.videoplayer.player.IjkVideoView;
//import com.mapbox.geojson.Feature;
//import com.mapbox.geojson.FeatureCollection;
//import com.mapbox.geojson.LineString;
//import com.mapbox.geojson.Point;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.maps.UiSettings;
//import com.mapbox.mapboxsdk.style.layers.LineLayer;
//import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
//import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
//import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import cn.com.blackview.azdome.R;
//import cn.com.blackview.azdome.utils.DateUtil;
//import cn.com.library.gps.LDGPSInvoke;
//import cn.com.library.helper.RxHelper;
//import cn.com.library.utils.LogHelper;
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.observers.DisposableObserver;
//
//public class BoxMap {
//
//    private Context context;
//    private String VideoPath;
//    private MapboxMap mapboxMap;
//    private boolean isTimelapse = false;
//    private List<Point> mGeoPoints;
//    private Disposable disposable;
//    private IjkVideoView videoView;
//    private GeoJsonSource geoJsonSource;
//
//    private ValueAnimator animator;
//    private LatLng currentPosition = new LatLng(22.65063, 114.02910166666666);
//
//    public BoxMap(String videoPath) {
//        VideoPath = videoPath;
//    }
//
//    public void setBoxMap(Context context, MapView mMapView, IjkVideoView videoView) {
//        this.videoView = videoView;
//        this.context = context;
//
//        // GPS 轨迹数组 大于 视频时长判断为缩时录影 x2 避免某些视频异常
//        if (LDGPSInvoke.getInstance().videoInfo(VideoPath) > Integer.parseInt(DateUtil.timeParse(videoView.getDuration())) * 2) {
//            isTimelapse = true;
//        }
//
//        mMapView.getMapAsync(mapboxMap -> {
//            this.mapboxMap = mapboxMap;
//            // 地图UI显示
//            initUiSetting();
//
//            geoJsonSource = new GeoJsonSource("source-id",
//                    Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
//                            currentPosition.getLatitude())));
//
//            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
//
//                mGeoPoints = new ArrayList<>();
//                Observable.interval(1, TimeUnit.MILLISECONDS).take(LDGPSInvoke.getInstance().videoInfo(VideoPath))
//                        .compose(RxHelper.rxSchedulerHelper())
//                        .subscribe(new DisposableObserver<Long>() {
//                            @Override
//                            public void onNext(Long aLong) {
//                                LDGPSInvoke.getInstance().GpsInfoinvoke(Integer.parseInt(String.valueOf(aLong)), gpsInfo -> {
//                                    if (gpsInfo.getLatitude() != 0.0 && gpsInfo.getLongitude() != 0.0) {
////                                                LogHelper.d("ltnq GPS", "getLatitude : " + gpsInfo.getLatitude() + " : "
////                                                        + "getLongitude : " + gpsInfo.getLongitude());
//                                        mGeoPoints.add(Point.fromLngLat(gpsInfo.getLongitude(), gpsInfo.getLatitude()));
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                if (mGeoPoints.size() > 0) {
//                                    LatLng latLng = new LatLng(mGeoPoints.get(0).latitude(), mGeoPoints.get(0).longitude());
//                                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                                            .target(new LatLng(latLng.getLatitude(), latLng.getLongitude()))
//                                            .zoom(17)//放大尺度 从0开始，0即最大比例尺，最大未知，17左右即为街道层级
//                                            .bearing(180)//地图旋转，但并不是每次点击都旋转180度，而是相对于正方向180度，即如果已经为相对正方向180度了，就不会进行旋转
//                                            .tilt(30)//地图倾斜角度，同上，相对于初始状态（平面）成30度
//                                            .build();//创建CameraPosition对象
//
//                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);
//
//                                    style.addImage(("marker_icon"), BitmapFactory.decodeResource(
//                                            context.getResources(), R.mipmap.pink_dot));
//
//                                    style.addSource(geoJsonSource);
//
//                                    style.addSource(new GeoJsonSource("line-source",
//                                            FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
//                                                    LineString.fromLngLats(mGeoPoints)
//                                            )})));
//
//                                    style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
//                                            PropertyFactory.lineWidth(5f),
//                                            PropertyFactory.lineColor(Color.parseColor("#239B6E"))
//                                    ));
//
//                                    style.addLayer(new SymbolLayer("layer-id", "source-id")
//                                            .withProperties(
//                                                    PropertyFactory.iconImage("marker_icon"),
//                                                    PropertyFactory.iconIgnorePlacement(true),
//                                                    PropertyFactory.iconAllowOverlap(true)
//                                            ));
//
//                                    mapboxMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(mGeoPoints.get(0).latitude(), mGeoPoints.get(0).longitude())));
//
//                                    mapboxMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(mGeoPoints.get(mGeoPoints.size() - 1).latitude(), mGeoPoints.get(mGeoPoints.size() - 1).longitude())));
//
//                                    setBoxMap();
//                                } else {
//                                    mMapView.setVisibility(View.GONE);
//                                }
//                            }
//                        });
//            });
//
//        });
//    }
//
//    public void setBoxMap() {
//        if (!isTimelapse) {
//            LDGPSInvoke.getInstance().videoInfo(VideoPath);
//            Log.d("ltnq ----- 时长 ----- ", String.valueOf(LDGPSInvoke.getInstance().videoInfo(VideoPath)));
//            timer(1000);
//        }
//    }
//
//    public void timer(long milliseconds) {
//        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        try {
//                            LDGPSInvoke.getInstance().GpsInfoinvoke(Integer.parseInt(DateUtil.timeParse(videoView.getCurrentPosition())), gpsInfo -> {
//                                if (gpsInfo.getLatitude() != 0.0 && gpsInfo.getLongitude() != 0.0) {
//                                    LogHelper.d("ltnq GPS", "getLatitude : " + gpsInfo.getLatitude() + " : "
//                                            + "getLongitude : " + gpsInfo.getLongitude());
//
//                                    if (animator != null && animator.isStarted()) {
//                                        currentPosition = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
//                                        animator.cancel();
//                                    }
//
//                                    animator = ObjectAnimator
//                                            .ofObject(latLngEvaluator, currentPosition, new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude()))
//                                            .setDuration(2000);
//                                    Log.d("ltnq", String.valueOf(new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude())));
//                                    animator.addUpdateListener(animatorUpdateListener);
//                                    animator.start();
//
//                                    currentPosition = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
//                                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
//                                }
//                            });
//
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
//            new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
//                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
//                }
//            };
//
//    /**
//     * Method is used to interpolate the SymbolLayer icon animation.
//     */
//    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {
//
//        private final LatLng latLng = new LatLng();
//
//        @Override
//        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
//            latLng.setLatitude(startValue.getLatitude()
//                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
//            latLng.setLongitude(startValue.getLongitude()
//                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
//            return latLng;
//        }
//    };
//
//    private void initUiSetting() {
//        UiSettings uiSettings = mapboxMap.getUiSettings();
//        uiSettings.setCompassEnabled(false);                // 隐藏指南针
//        uiSettings.setLogoEnabled(false);                   // 隐藏logo
//        uiSettings.setTiltGesturesEnabled(true);            // 设置是否可以调整地图倾斜角
//        uiSettings.setRotateGesturesEnabled(true);          // 设置是否可以旋转地图
//        uiSettings.setAttributionEnabled(false);            // 设置是否显示那个提示按钮
//    }
//
//    public void onDestroy() {
//        // 退出时销毁定位
//        // 关闭定位图层
//        if (disposable != null && disposable.isDisposed()) {
//            disposable.dispose();
//        }
//        VideoPath = null;
//    }
//}
