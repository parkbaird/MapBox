package cn.com.blackview.mapbox;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SAVED_STATE_CAMERA = "saved_state_camera";
    private static final String SAVED_STATE_RENDER = "saved_state_render";
    private static final String SAVED_STATE_LOCATION = "saved_state_location";
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private MapboxMap mapboxMap;
    private Location lastLocation;

    @CameraMode.Mode
    private int cameraMode = CameraMode.TRACKING;

    @RenderMode.Mode
    private int renderMode = RenderMode.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        if (savedInstanceState != null) {
            cameraMode = savedInstanceState.getInt(SAVED_STATE_CAMERA);
            renderMode = savedInstanceState.getInt(SAVED_STATE_RENDER);
            lastLocation = savedInstanceState.getParcelable(SAVED_STATE_LOCATION);
        }

        mapView.onCreate(savedInstanceState);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

                        // Retrieve and customize the Maps SDK's LocationComponent
                        locationComponent = mapboxMap.getLocationComponent();
                        locationComponent.activateLocationComponent(
                                LocationComponentActivationOptions
                                        .builder(MainActivity.this, style)
                                        .useDefaultLocationEngine(true)
                                        .locationEngineRequest(new LocationEngineRequest.Builder(750)
                                                .setFastestInterval(750)
                                                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                                .build())
                                        .build());

                        locationComponent.setLocationComponentEnabled(true);
                        locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                            @Override
                            public void onLocationComponentClick() {
                                Toast.makeText(MainActivity.this, "222", Toast.LENGTH_LONG).show();
                            }
                        });
                        locationComponent.addOnCameraTrackingChangedListener(new OnCameraTrackingChangedListener() {
                            @Override
                            public void onCameraTrackingDismissed() {
                                Log.d("ltnq onCameraTrackingDismissed", "none");
                            }

                            @Override
                            public void onCameraTrackingChanged(int currentMode) {
                                Log.d("ltnq onCameraTrackingChanged", String.valueOf(currentMode));
                            }
                        });
                        locationComponent.setCameraMode(cameraMode);
                        setRendererMode(renderMode);
                        locationComponent.forceLocationUpdate(lastLocation);
                    });
                }
            });
        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(MainActivity.this,
                            "111", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                                mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

                                    // Retrieve and customize the Maps SDK's LocationComponent
                                    locationComponent = mapboxMap.getLocationComponent();
                                    locationComponent.activateLocationComponent(
                                            LocationComponentActivationOptions
                                                    .builder(MainActivity.this, style)
                                                    .useDefaultLocationEngine(true)
                                                    .locationEngineRequest(new LocationEngineRequest.Builder(750)
                                                            .setFastestInterval(750)
                                                            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                                            .build())
                                                    .build());

                                    locationComponent.setLocationComponentEnabled(true);
                                    locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                                        @Override
                                        public void onLocationComponentClick() {
                                            Toast.makeText(MainActivity.this, "222", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    locationComponent.addOnCameraTrackingChangedListener(new OnCameraTrackingChangedListener() {
                                        @Override
                                        public void onCameraTrackingDismissed() {
                                            Log.d("ltnq onCameraTrackingDismissed", "none");
                                        }

                                        @Override
                                        public void onCameraTrackingChanged(int currentMode) {
                                            Log.d("ltnq onCameraTrackingChanged", String.valueOf(currentMode));
                                        }
                                    });
                                    locationComponent.setCameraMode(cameraMode);
                                    setRendererMode(renderMode);
                                    locationComponent.forceLocationUpdate(lastLocation);
                                });
                            }
                        });
                    } else {
                        finish();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_CAMERA, cameraMode);
        outState.putInt(SAVED_STATE_RENDER, renderMode);
        if (locationComponent != null) {
            outState.putParcelable(SAVED_STATE_LOCATION, locationComponent.getLastKnownLocation());
        }
    }

    private void setRendererMode(@RenderMode.Mode int mode) {
        renderMode = mode;
        locationComponent.setRenderMode(mode);
        Log.d("ltnq setRendererMode", String.valueOf(mode));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}