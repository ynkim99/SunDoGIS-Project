package com.example.gis_project_app;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity
        extends AppCompatActivity
        implements OnMapReadyCallback
        // 위치 정보를 얻기 위한 인터페이스들
        , LocationListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        // 센서정보를 얻기 위한 인터페이스
        , SensorEventListener
{
    /** #################
     * 서버 주소 : 포트번호까지의 주소를 입력
     *           (ex: http://111.22.33.44:1234/qwer/asdf -> "http://111.22.33.44:1234/")
     *           본인의 ip주소를 입력
     * 전송 주기 설정 : 밀리초 단위로 입력
     ################# */
    private static final String URL= "http://172.30.1.73:8080";
    private static final int CYCLE = 5000;

    // 지도객체
    private GoogleMap mMap;
    // 지도 ui 객체
    private UiSettings mUi;

    // 위도 경도 선언
    private double mLat, mLng;

    //  로케이션 리퀘스트와 구글 API 선언
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    // onConnected() 메소드를 사용할 수 있을때 사용하는 변수
    private FusedLocationProviderClient providerClient;

    // 버튼 선언
    private Button startBtn;
    private Button pauseBtn;
    private Button stopBtn;

    // Http 통신을 위한 변수
    private RequestQueue queue;

    // 인터벌을 위한 변수
    private Timer timerCall;
    TimerTask timerTask;

    // dialog 변수
    private String carNum;
    private View viewDialog;

    // 데시벨 측정을 위한 변수
    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private String filePath = "";
    private int amplitude;

    // RPM 측정을 위한 변수
    // SensorManager 선언
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] previousAcceleration = {0, 0, 0}; // 이전 가속도 값
    private long previousTimeMillis = 0; // 이전 시간(ms)
    private double rpm = 0.0; // RPM 값

    // CSV 파일명
    private static String GPS_CSV = "app_data_gps.csv";
    private static String NOISE_CSV = "app_data_noise.csv";
    private static String RPM_CSV = "app_data_rpm.csv";
    // CSV 파일 경로
    private String csvFilePath;
    // CSV 파일 객체
    CsvFile csvFile;

    // polyline등록을 위한 변수들
    private Marker mCurrentMarker;
    private Marker mStartMarker;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private LatLng startLatLng = new LatLng(0, 0);
    private LatLng endLatLng = new LatLng(0, 0);
    private List<Polyline> polylines;

    // 알림 관련 변수
    private static final String NOTIFICATION_CHANNEL_ID = "measurement_channel";
    private static final int NOTIFICATION_ID = 1;

    // 인트로 화면을 위한 변수
    private IntroDialog introDialog;

    // 뒤로가기 제어를 위한 변수
    private OnBackPressedCallback backPressedCallback;



    //// 액티비티가 실행되고 처음 시작될 때 호출

    /**
     * Bundle savedInstanceState : 이전에 저장된 상태, 액티비티가 이전에 종료 되었을때 상태정보 복원
     */
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 부모클래스에서 정의된 초기화 작업을 수행
        super.onCreate(savedInstanceState);

        // 현재 액티비티의 화면에 어떤 레이아웃을 표시할지 설정
        setContentView(R.layout.activity_main);

        // 인트로 화면 띄우기
        if(introDialog == null){
            introDialog = new IntroDialog(R.layout.dialog_intro);
            introDialog.show(getSupportFragmentManager(),null);
        }

        //  버튼들, 구글API, 타이머, csv, volley 등 전역변수 초기값 부여
        setInitialValue();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // R.id.mapView 이라는 프래그먼트에 Google Maps API 지도를 로드
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager() // 현재 활성화된 프래그먼트 매니저를 얻음
                .findFragmentById(R.id.mapView); // XML 레이아웃에서 정의한 지도 프래그먼트의 ID가 R.id.map인 프래그먼트를 찾음
        mapFragment.getMapAsync(this); // 비동기적으로 Google Maps API 지도를 로드



        //// 엑세스 권한 요청
        /**
         * registerForActivityResult()
         *      : ActivityResultLauncher객체 초기화, String[] 타입의 권한목록 요청함
         * new ActivityResultContracts.RequestMultiplePermissions()
         *      : 사용자에게 권한을 요청
         * isGranted -> {...}
         *      : isGranted를 매개변수로 하는 람다함수
         * isGranted : java.util.Map<String, Boolean> 타입의 변수
         *      .values() : 맵의 값(부여 여부)를 컬렉션으로 가져옴
         *          .stream() : 컬렉션을 스트링으로 변환
         *              .allMatch( : 모든 권한이 부여되었는지 검사
         *                  permission -> permission.booleanValue() == true) : 모든 권한이 부여되면 true를 반환
         */
        ActivityResultLauncher<String[]> requestPermissionLauncher
                = registerForActivityResult( // 권한 요청 결과 처리 시작
                new ActivityResultContracts.RequestMultiplePermissions()
                , isGranted -> { // java.util.Map<String, Boolean>
                    // Map형태로 되어있는 변수에서 stream을 통해 모두 권한이 부여됐는지 체크
                    if (isGranted.values().stream().allMatch(permission -> permission.booleanValue() == true)) {
                        mGoogleApiClient.connect();
                    } else {
                        Toast.makeText(this, "권한 거부..", Toast.LENGTH_SHORT).show();
                    }
                });

        //// ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 권한 확인
        /**
         * checkSelfPermission(this, permission) : 현재 앱에서 특정 권한을 가지고 있는지 확인
         * PackageManager.PERMISSION_GRANTED : 권한이 부여되었을 때 상수 값
         * if -> 권한이 부여되지 않았을 때 (ACCESS_FINE_LOCATION의 값과 일치하지 않는 경우)
         * else -> 권한이 부여되었을 때
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("권한 확인", "onCreate: ACCESS_FINE_LOCATION 권한 요청");
                // ACCESS_FINE_LOCATION 권한 요청
                /**
                 * requestPermissionLauncher : 안드로이드 권한 요청 API
                 * launch() : 권한 요청을 시작
                 * 사용자에게 권한 요청 대화상자가 표시되며, 사용자가 승인하거나 거부할수 있음
                 */
                requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("권한 확인", "onCreate: ACCESS_COARSE_LOCATION 권한요청");
                // ACCESS_COARSE_LOCATION 권한 요청
                /**
                 * requestPermissionLauncher : 안드로이드 권한 요청 API
                 * launch() : 권한 요청을 시작
                 * 사용자에게 권한 요청 대화상자가 표시되며, 사용자가 승인하거나 거부할수 있음
                 */
                requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        } else {
            // 위치 제공자 준비하기
            mGoogleApiClient.connect();
        }

        // 녹음 권한 런타임 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // RECORD_AUDIO 권한 요청
            /**
             * requestPermissionLauncher : 안드로이드 권한 요청 API
             * launch() : 권한 요청을 시작
             * 사용자에게 권한 요청 대화상자가 표시되며, 사용자가 승인하거나 거부할수 있음
             */
            requestPermissionLauncher.launch(new String[]{Manifest.permission.RECORD_AUDIO});
        }


        // 버튼 이벤트 등록
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // timerTask가 취소되지 않은 상태면 취소하고 시작
                cancelTimerTask();

                if (stopBtn.isEnabled()) {
                    // stopBtn이 활성화 된 상황 : start 버튼을 누른 후 일시정지하고 있는 상태
                    // dialog를 생성하지 않고 Timer를 실행한다
                    setStartTimer();
                } else {
                    // stopBtn이 비활성화 된 상황 : start 버튼을 처음 누르는 상태

                    // dialog 상자 생성 시작
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("차량 번호");
                    // View.inflate 로 dialog 레이아웃을 뷰로 구현
                    viewDialog = (View) View.inflate(MainActivity.this, R.layout.dialog_car_num, null);
                    // setView 함수로 뷰를 dialog 변수에 전달
                    dialog.setView(viewDialog);

                    // 차랑정보 입력 dialog 버튼
                    dialog.setPositiveButton("입력",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // dialog로 차량번호 정보 얻기
                                    EditText carNumBox = (EditText) viewDialog.findViewById(R.id.car_num);
                                    carNum = String.valueOf(carNumBox.getText());
                                    
                                    // 기존에 있던 선과 마커 제거
                                    removePolylinesAndMarkers();
                                    // mStartMarker 생성
                                    drawStartMarker();
                                    // Timer 객체 실행
                                    setStartTimer();
                                }
                            });
                    dialog.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 차량번호를 입력하지 않으면 아무런 일도 일어나지 않음
                                }
                            });
                    dialog.show();
                }

            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // timerTask 중지
                cancelTimerTask();

                // 녹음 종료
                stopRecord();
                // RPM 측정 종료
                stopRPMMeasurement();
                // 종료 알림
                showNotification("측정이 일시정지 되었습니다.");

                // 버튼 활성화/비활성화
                startBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.INVISIBLE);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // timerTask 중지
                cancelTimerTask();

                // 녹음 종료
                stopRecord();
                // RPM 측정 종료
                stopRPMMeasurement();
                // 종료 알림
                showNotification("측정이 종료되었습니다.");

                // 종료 요청 보내기
                sendStopRequest();

                // 버튼 활성화/비활성화
                stopBtn.setEnabled(false);
                startBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.INVISIBLE);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }


    // 지도 객체를 이용할 수 있는 상황이 될 때
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Log.d("onMapReady", "onMapReady: 지도 설정");
        // 컨트롤러 올리기
        mUi = mMap.getUiSettings();

        // 나침반 추가
        mUi.setCompassEnabled(true);
        // 확대축소 컨트롤 추가
        mUi.setZoomControlsEnabled(true);

        // 주기적인 위치 업데이트 시작
        createLocationRequest();

        // 인트로 화면 내리기
        if (introDialog.isAdded()) {
            // 지도가 완전히 로딩이 될때 인트로 화면을 띄우기 위해 postDelayed를 사용
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    introDialog.dismiss();
                }
            },2000);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("onLocationChanged", "onLocationChanged: 실행");
        // 위치가 변경된 경우
        // 현재 좌표 값을 가지고 이동
        moveMap(location.getLatitude(), location.getLongitude());
    }

    // 위치 제공자를 사용할 수 있는 상황일 때
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected", "onConnected: 실행 시작");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && providerClient != null) {
            providerClient.getLastLocation()
                    .addOnSuccessListener(
                            this,
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        moveMap(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            });
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // 위치 제공자를 사용할 수 없을 때
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 사용할 수 있는 위치 제공자가 없을 때
    }

    // 가속도 센서 이벤트 처리
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 가속도 값 얻기
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // 가속도 변화량 계산
            float deltaX = x - previousAcceleration[0];
            float deltaY = y - previousAcceleration[1];
            float deltaZ = z - previousAcceleration[2];

            // 이전 값을 업데이트
            previousAcceleration[0] = x;
            previousAcceleration[1] = y;
            previousAcceleration[2] = z;

            // RPM 값을 얻는 메서드 호출
            // getRpm();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 사용자 정의 메서드
     */
    ////////////////////////////////////////////////////////////////////////////////////////////

    // 1-1. 초기값 부여하는 메서드
    @SuppressLint("ResourceAsColor")
    private void setInitialValue() {
        // 버튼 초기화
        startBtn = (Button) findViewById(R.id.startBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        Log.d("setInitialValue", "setInitialValue: 버튼 초기화 완료");

        // 구글 API 초기화
        providerClient = LocationServices.getFusedLocationProviderClient(this);
        // onCreate 메소드가 실행되면 구글 API 객체를 생성하고 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API) // LocationServices.API 추가
                .addConnectionCallbacks(this) // GoogleApiClient.ConnectionCallbacks 객체 등록
                .addOnConnectionFailedListener(this).build(); // GoogleApiClient.OnConnectionFailedListener 객체 등록

        Log.d("setInitialValue", "setInitialValue: 구글 API 초기화 완료");

        // 타이머 초기화
        timerCall = new Timer();

        Log.d("setInitialValue", "setInitialValue: 타이머 초기화 완료");

        // csv를 저장할 내부 저장소 경로
        csvFilePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        // csv 객체 초기화
        csvFile = new CsvFile(csvFilePath);
        Log.d("csvFilePath", "내부 저장소 경로 : " + csvFilePath);

        Log.d("setInitialValue", "setInitialValue: csv 객체 초기화 완료");

        // volley 요청변수 초기화
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        Log.d("setInitialValue", "setInitialValue: volley 초기화 완료");

        // 가속도(RPM) 센서 초기화
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        Log.d("setInitialValue", "setInitialValue: 가속도 센서 초기화 완료");

        // polyline 리스트 초기화
        polylines = new ArrayList<>();
        Log.d("setInitialValue", "setInitialValue: Polyline 리스트 초기화 완료");

        // 뒤로가기 콜백 변수 초기화
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("종료하시겠습니까?");
                builder.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Log.d("handleOnBackPressed", "handleOnBackPressed: 뒤로가기 버튼 눌림");
            }
        };
        Log.d("setInitialValue", "setInitialValue: 뒤로가기 콜백 변수 초기화 완료");

    }

    //------------------------------------------------------------------------------------------

    // 2-1. 지도 중앙좌표를 이동하는 메소드
    private void moveMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        // 중심 좌표 생성
        CameraPosition positon = CameraPosition.builder()
                .target(latLng)
                .zoom(16f)
                .build();
        // 지도 중심 이동
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positon));
    }

    //------------------------------------------------------------------------------------------

    // 2-2. 좌표 얻는 메서드
    private void getLastLocation() {
        Log.d("getLastLocation", "getLastLocation: startLatLng:" + startLatLng + ", endLatLng:" + endLatLng);
        startLatLng = new LatLng(mLat, mLng);
        // 위도 경도 정보 얻기
        mLat = mCurrentLocation.getLatitude();
        mLng = mCurrentLocation.getLongitude();
        endLatLng = new LatLng(mLat, mLng);
        Log.d("test", "latitude:" + mCurrentLocation.getLatitude());
        Log.d("test", "longitude:" + mCurrentLocation.getLongitude());

        // 지도 중심 이동하기
        moveMap(mLat, mLng);

        // 선 그리기
        drawPath();
        // 마커 그리기
        drawCurrentMarker();
    }

    //------------------------------------------------------------------------------------------

    // 3-1. JSON데이터 서버에 전송하는 메서드
    public void sendJsonData(String url) {
        // JSON 데이터 전송
        try {

            // 현재 일자, 시간
            LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul"));
            LocalTime time = LocalTime.now(ZoneId.of("Asia/Seoul"));

            // csv 파일 생성
            List<String[]> csvGps = csvFile.readAllCsvData(GPS_CSV);
            csvGps.add(new String[]{carNum, "" + date, "" + time, "" + mLat, "" + mLng});
            csvFile.writeData(GPS_CSV, csvGps);
            List<String[]> csvNoise = csvFile.readAllCsvData(NOISE_CSV);
            csvNoise.add(new String[]{carNum, "" + date, "" + time, "" + amplitude});
            csvFile.writeData(NOISE_CSV, csvNoise);
            List<String[]> csvRpm = csvFile.readAllCsvData(RPM_CSV);
            csvRpm.add(new String[]{carNum, "" + date, "" + time, "" + rpm});
            csvFile.writeData(RPM_CSV, csvRpm);

            //Toast.makeText(this, "현재 위치 : " + mLat + ", " + mLng + "현재 소음 : " + amplitude, Toast.LENGTH_SHORT).show();

            // JSON 객체 생성
            final JSONObject object = new JSONObject();

            // gps 정보 전송
            final JSONObject gps = new JSONObject();
            gps.put("carNum", carNum);
            gps.put("date", date);
            gps.put("time", time);
            gps.put("lat", mLat);
            gps.put("lon", mLng);

            // 소음 정보 전송
            final JSONObject noise = new JSONObject();
            noise.put("carNum", carNum);
            noise.put("date", date);
            noise.put("time", time);
            noise.put("noise", amplitude);

            // 진동 정보 전송
            final JSONObject frequency = new JSONObject();
            frequency.put("carNum", carNum);
            frequency.put("date", date);
            frequency.put("time", time);
            frequency.put("frequency", rpm);

            // 전송할 json 객체에 저장
            object.put("gps", gps);
            object.put("noise", noise);
            object.put("frequency", frequency);

            Log.d("sendJsonData", "sendJsonData: " + object);

            // 전송 준비
            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("test", "onResponse: ");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("test", "onErrorResponse: " + error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            // 전송
            queue.add(jsonRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //------------------------------------------------------------------------------------------

    // 3-2. 종료 요청을 서버에 보내는 메서드
    private void sendStopRequest() {
        // 전송 준비
        ///// 문자열 요청
        StringRequest stopRequest = new StringRequest(
                Request.Method.POST,
                URL + "/api/sensor/stop",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("stop", "onResponse: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("stop", "onErrorResponse: " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "text/plain");
                return headers;
            }
        };
        // 전송
        queue.add(stopRequest);
    }

    //------------------------------------------------------------------------------------------

    // 4-1. timerTask를 시작하는 메서드
    private void setStartTimer() {
        // 녹음 시작
        startRecord();
        // RPM 측정 시작
        startRPMMeasurement();
        // 측정 중 알림
        showNotification("측정 중...");

        // 반복할 코드
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 위도경도 정보 얻기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getLastLocation();
                    }
                });
                // 데시벨 정보 얻기
                getDecibel();
                // 진동 RPM 얻기
                getRpm();
                // Json 데이터 보내기
                sendJsonData(URL + "/api/sensor/save");
            }
        };

        // 기록 시작 (5초로 설정)
        timerCall.schedule(timerTask, 0, CYCLE);

        // 버튼 활성화/비활성화
        stopBtn.setEnabled(true);
        startBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    //------------------------------------------------------------------------------------------

    // 4-2. timerTask를 멈추는 메서드
    private void cancelTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    //------------------------------------------------------------------------------------------

    // 5-1. 데시벨을 얻기위해 녹음을 시작하는 메서드
    @SuppressLint("RestrictedApi")
    private void startRecord() {
        recorder = new MediaRecorder();
        // 외부 저장소 내 캐시저장소에 저장하기
        String basePath = getExternalCacheDir().getAbsolutePath().toString();

        if (recorder != null) {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 외부에서 들어오는 소리를 녹음
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 출력 파일 포맷을 설정
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 오디오 인코더를 설정
            filePath = basePath + "/myRecord.3gp";
            recorder.setOutputFile(filePath); // 출력 파일 이름을 설정
        }

        try {
            if (recorder != null) {
                recorder.prepare(); // 초기화를 완료
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            return;
        }

        if (recorder != null) {
            recorder.start(); // 녹음기를 시작
            getDecibel(); // 데시벨 측정
        }
    }

    //------------------------------------------------------------------------------------------

    // 5-2. 녹음을 중지하는 메서드
    private void stopRecord() {
        isRecording = false;
        if (recorder != null) {
            recorder.stop(); // 녹음기 중지
            recorder.release(); // 리소스 확보
            recorder = null;
        }
    }

    //------------------------------------------------------------------------------------------

    // 5-3. 데시벨을 측정하는 메서드
    private void getDecibel() {
        if (recorder != null) {
            isRecording = true;
            amplitude = recorder.getMaxAmplitude();
            amplitude = (int) (20 * Math.log10(amplitude)); // 진폭 to 데시벨
            //Toast.makeText(getApplicationContext(), "현재 소음 : " + amplitude, Toast.LENGTH_SHORT).show();

            // 데시벨 값에 따른 작업을 수행
            if (amplitude < 0) {
                // 데시벨이 음수값이면 0으로 설정
                amplitude = 0;
            }

            Log.d("getDecibel", "getDecibel: " + amplitude);
        }
    }

    //------------------------------------------------------------------------------------------

    // 6-1. RPM 값을 얻는 메서드
    private void getRpm() {
        // 현재 시간(ms) 얻기
        long currentTimeMillis = System.currentTimeMillis();

        // 이전 시간과의 차이(ms) 계산
        long timeDiffMillis = currentTimeMillis - previousTimeMillis;

        // 가속도 변화량 계산
        float deltaX = previousAcceleration[0];
        float deltaY = previousAcceleration[1];
        float deltaZ = previousAcceleration[2];

        // 가속도 변화량의 크기 계산 (벡터의 크기)
        double accelerationMagnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // RPM 계산 (임의의 스케일 팩터 사용)
        if (timeDiffMillis > 0) {
            rpm = (accelerationMagnitude / timeDiffMillis) * 1000000.0; // RPM으로 변환
        }

        // RPM 값 로그 출력
        Log.d("getRPM", "RPM: " + rpm);

        // 이전 값을 업데이트
        previousTimeMillis = currentTimeMillis;
    }

    //------------------------------------------------------------------------------------------

    // 6-2. RPM 측정을 시작하는 함수
    private void startRPMMeasurement() {
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    //------------------------------------------------------------------------------------------

    // 6-3. RPM 측정을 중지하는 함수
    private void stopRPMMeasurement() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    //------------------------------------------------------------------------------------------

    // 7-1. 주기적인 위치 업데이트 처리를 위한 메서드
    private void createLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("createLocationRequest", "createLocationRequest: 업데이트 처리 시작");
            // 위치 설정 변경
            mLocationRequest = new LocationRequest();
            // 위치 업데이트 간격
            mLocationRequest.setInterval(10000);
            // 가장 빠른 업데이트 간격
            mLocationRequest.setFastestInterval(5000);
            // 우선순위 -> 가장 정확한 위치
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // 현재 위치 설정 받기
            LocationSettingsRequest.Builder builder
                    = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            // 설정이 잘 됐는지 확인
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.d("LocationSettingRequest", "onSuccess: 위치 설정 성공");
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LocationSettingRequest", "onFailure: 위치 설정 실패");
                    if(e instanceof ResolvableApiException){
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(
                                    MainActivity.this,
                                    0x1);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });

            // 주기적인 위치 업데이트를 위한 LocationCallback 설정
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        for (Location location : locationResult.getLocations()) {
                            if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
                                // 위치 업데이트 처리 및 보정 로직
                                mCurrentLocation = location;
                                // 지도 중심 이동하기
                                moveMap(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                }
            };

            providerClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper());
        }

    }

    //------------------------------------------------------------------------------------------

    // 8-1. polyline 그리기
    private void drawPath(){
        Log.d("drawPath", "drawPath: 선그리기 시도중 startLatLng:" + startLatLng + ", endLatLng:" + endLatLng);
        PolylineOptions options = new PolylineOptions()
                .add(startLatLng)
                .add(endLatLng)
                .width(15)
                .color(0xFF0054A7)
                .geodesic(true);
        Polyline polyline = mMap.addPolyline(options);
        polylines.add(polyline);
        Log.d("drawPath", "drawPath: polylines.size() : " + polylines.size());

    }

    // 8-2. mCurrentMarker 그리기
    private void drawCurrentMarker(){
        // 마커 옵션
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions.position(endLatLng);
        markerOptions.title("MyLocation");
        Log.d("drawCurrentMarker", "drawCurrentMarker: 마커 위치 = " + endLatLng);

        // 마커 1개만 놔두기 위해서 기존 마커 지우기
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }

        // 마커 표시
        mCurrentMarker = mMap.addMarker(markerOptions);
    }

    //------------------------------------------------------------------------------------------

    // 8-3. mStartMarker 그리기
    private void drawStartMarker(){
        // 위도 경도 정보 얻기
        mLat = mCurrentLocation.getLatitude();
        mLng = mCurrentLocation.getLongitude();

        // 시작지점 초기화
        startLatLng = new LatLng(mLat, mLng);

        // 마커 옵션
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions.position(startLatLng);
        markerOptions.title("MyLocation");
        Log.d("drawStartMarker", "drawStartMarker: 마커 위치 = " + startLatLng);

        // 마커 1개만 놔두기 위해서 기존 마커 지우기
        if (mStartMarker != null) {
            mStartMarker.remove();
        }
        mStartMarker = mMap.addMarker(markerOptions);
    }

    //------------------------------------------------------------------------------------------

    // 8-4. Polyline,Marker 지우기
    private void removePolylinesAndMarkers(){
        if(mStartMarker != null){
            mStartMarker.remove();
        }
        if(mCurrentMarker != null){
            mCurrentMarker.remove();
        }
        for(Polyline polyline : polylines){
            polyline.remove();
        }
    }
    //------------------------------------------------------------------------------------------

    // 9-1. 알림 관련 메서드
    private void showNotification(String message) {
        // 알림 매니저 생성
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android Oreo 이상에서는 알림 채널을 생성해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Measurement Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription("Measurement Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // 알림 빌더 생성
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notice) // 아이콘 설정
                        .setContentTitle("용인시 청소차 관제 시스템") // 제목 설정
                        .setContentText(message) // 메시지 설정
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        // 알림 표시
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 사용자 정의 함수 끝
     */
    ////////////////////////////////////////////////////////////////////////////////////////////


}