package kr.hee.kwnoti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import kr.hee.kwnoti.calendar_activity.CalendarActivity;
import kr.hee.kwnoti.info_activity.InfoActivity;
import kr.hee.kwnoti.settings_activity.SettingsActivity;
import kr.hee.kwnoti.student_card_activity.StudentCardActivity;
import kr.hee.kwnoti.tel_activity.TelActivity;
import kr.hee.kwnoti.u_campus_activity.UCampusMainActivity;

public class MainActivity extends ActivityBase implements View.OnClickListener {
    TextView    btn_card,       // 학생증
                btn_calendar,   // 학사일정
                btn_tel,        // 교내 전화번호
                btn_info,       // 학교 공지사항
                btn_food,       // 금주의 학식
                btn_uCampus,    // 유캠퍼스 공지사항
                btn_wifi,       // 올레와이파이 신청
                btn_settings;   // 설정
//    boolean     weatherActive;  // 실시간 날씨 사용 여부

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstUse();            // 최초 실행 여부 검사
//        weatherActive = checkUseWeatherService(); // 날씨 서비스를 켰는지 여부 확인
        initView();                 // 뷰 초기화
    }

    /** 최초 실행인지 확인하는 메소드 */
    void checkFirstUse() {
        SharedPreferences firstUse = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this);
        // 최초실행일 경우 최초실행 화면을 표시
        if (firstUse.getBoolean(KEY.FIRST_USE, true)) {
            // FCM 공지사항 알림 수신 설정
            new Thread(new Runnable() {
                @Override public void run() {
                    FirebaseMessaging.getInstance().subscribeToTopic("notice");
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            UTILS.showToast(MainActivity.this, getString(R.string.toast_first_use));
                        }
                    });
                    Log.d("MainActivity", "First run, FCM Enabled.");
                }
            }).start();

            //startActivity(new Intent(this, FirstActivity.class)); TODO
            //finish();

            // 최초 실행 종료
            SharedPreferences.Editor editor = firstUse.edit();
            editor.putBoolean(KEY.FIRST_USE, false).apply();
        }
    }

    /** 학교의 날씨를 받아올지 설정을 가져오는 메소드
     * @return  날씨 설정 여부 */
//    boolean checkUseWeatherService() {
//        SharedPreferences useWeather = PreferenceManager.
//                getDefaultSharedPreferences(MainActivity.this);
//        return useWeather.getBoolean(getString(R.string.key_weatherActive), true);
//    }

    /** 뷰 초기화 메소드 */
    void initView() {
        setContentView(R.layout.activity_main);

        btn_card    = (TextView)findViewById(R.id.main_btn_idCard);
        btn_calendar= (TextView)findViewById(R.id.main_btn_calendar);
        btn_tel     = (TextView)findViewById(R.id.main_btn_tel);
        btn_info    = (TextView)findViewById(R.id.main_btn_info);
//        btn_food    = (TextView)findViewById(R.id.main_btn_food);
        btn_uCampus = (TextView)findViewById(R.id.main_btn_uInfo);
//        btn_wifi    = (TextView)findViewById(R.id.main_btn_wifi);
        btn_settings= (TextView)findViewById(R.id.main_btn_settings);

        btn_card.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        btn_tel.setOnClickListener(this);
        btn_info.setOnClickListener(this);
//        btn_food.setOnClickListener(this);
        btn_uCampus.setOnClickListener(this);
//        btn_wifi.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.main_btn_idCard :
                startActivity(new Intent(this, StudentCardActivity.class)); break;
            case R.id.main_btn_calendar :
                startActivity(new Intent(this, CalendarActivity.class)); break;
            case R.id.main_btn_tel :
                startActivity(new Intent(this, TelActivity.class)); break;
            case R.id.main_btn_info :
                startActivity(new Intent(this, InfoActivity.class)); break;
//            case R.id.main_btn_food :
//                startActivity(new Intent(this, FoodActivity.class)); break;
            case R.id.main_btn_uInfo :
                startActivity(new Intent(this, UCampusMainActivity.class)); break;
//            case R.id.main_btn_wifi :
//                startActivity(new Intent(this,WifiActivity.class)); break;
            case R.id.main_btn_settings :
                startActivity(new Intent(this, SettingsActivity.class)); break;
        }
    }
}
