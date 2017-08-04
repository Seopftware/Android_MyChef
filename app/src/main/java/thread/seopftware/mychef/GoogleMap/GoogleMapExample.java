package thread.seopftware.mychef.GoogleMap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import thread.seopftware.mychef.GoogleMap_Module.DirectionFinder;
import thread.seopftware.mychef.GoogleMap_Module.DirectionFinderListener;
import thread.seopftware.mychef.GoogleMap_Module.Route;
import thread.seopftware.mychef.R;

public class GoogleMapExample extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private TextView tv_CustomerName;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private LocationManager locationManager;

    double Current_latitude, Current_longtitude;
    String Customer_Name, Customer_Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_example);

        Intent intent = getIntent();
        Customer_Name = intent.getStringExtra("Customer_Name");
        Customer_Location = intent.getStringExtra("Customer_Location");
        Log.d("인텐트 받은 값", Customer_Name+Customer_Location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        tv_CustomerName= (TextView) findViewById(R.id.tv_CustomerName);

        etDestination.setText(Customer_Location); // 목적지
        tv_CustomerName.setText("  ("+Customer_Name+"께 가는길)");

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hcmus = new LatLng(37.566535,126.977969);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 12)); // 숫자가 커질수록 zoom이 확대됨

        chkGpsService(); // GPS 설정 여부 물어보기

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(GoogleMapExample.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleMapExample.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }

                // Check the network provided is enabled
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10000, new LocationListener() { // 최소 업데이트 변경 거리 및 시간
                        @Override
                        public void onLocationChanged(Location location) {
                            Current_latitude = location.getLatitude();
                            Current_longtitude = location.getLongitude();

                            LatLng latLng = new LatLng(Current_latitude, Current_longtitude); // Instantiate the class, Geocoder

                            Log.d("NETWORK상 현재 위치", String.valueOf(latLng));

                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
                            try {

                                List<Address> addressList = geocoder.getFromLocation(Current_latitude, Current_longtitude, 1);
                                String str = addressList.get(0).getAdminArea()+" "+addressList.get(0).getLocality() +" "+addressList.get(0).getThoroughfare() +" "+ addressList.get(0).getFeatureName();
                                str += "";
//                                mMap.addMarker(new MarkerOptions().position(latLng).title(str)); // 현재 나의 위치에 마커 추가
                                etOrigin.setText(str);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() { // 최소 업데이트 변경 거리 및 시간
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longtitude = location.getLongitude();

                            LatLng latLng = new LatLng(latitude, longtitude); // Instantiate the class, Geocoder
                            Log.d("GPS상 현재 위치", String.valueOf(latLng));

                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(latitude, longtitude, 1);
                                String str = addressList.get(0).getLocality() + ", ";
                                str += addressList.get(0).getCountryName();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(str));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });

                }
                return false;
            }
        });

        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.", "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) { // 폴리라인 없애기. 이 함수가 사용되어지지 않으면 폴리라인은 맵에서 없어지지 않는다.
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text); // 도착지까지 걸리는 시간 표시
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text); // 도착지까지의 거리 표시

            originMarkers.add(mMap.addMarker(new MarkerOptions() // 시작 지역에 마커 추가
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title("현재 나의 위치") // 마커 클릭 시 정보창에 표시되는 문자열
                    .snippet("내 위치") // 제목 아래에 표시되는 추가 텍스트
                    .position(route.startLocation))); // 위치 (Latlng 값) - 필수값
            destinationMarkers.add(mMap.addMarker(new MarkerOptions() // 목적지 지역에 마커 추가
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title("김인섭 고객님 출장장소")
                    .snippet("출장 장소 이름")
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) // 폴리라인의 각 포인터 지점 추가하기
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions)); // 폴리 라인 옵션 추가
        }
    }

    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setIcon(R.drawable.ic_menu_send);
            gsDialog.setMessage("GPS 사용 동의 후 위치 서비스 사용이 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("설정안함", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create();

            AlertDialog alertDialog = gsDialog.create();
            alertDialog.show();

            return false;

        } else {
            return false;
        }
    }
}