package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UCampusMainActivity extends Activity {
    // 리사이클러 뷰 친구들
    RecyclerView recyclerView;
    UCampusMainAdapter adapter;

    // 로딩 다이얼로그
    ProgressDialog progressDialog;

    // 로그인을 위한 데이터와 Retrofit 인터페이스
    private String stuId;
    private String stuPwd;
    Interface request;

    // 로그인 스레드
    LoginThread loginThread;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucampus_main);
        setTitle(R.string.ucampus_title);

        SharedPreferences pref = UTILS.checkUserData(this);
        if (pref == null)   finish();
        else {
            initView();
            init(pref);
            progressDialog.show();
        }
    }

    /** 액티비티 데이터 초기화 및 데이터 불러오기 메소드 */
    void init(SharedPreferences pref) {
        // 유저 데이터 불러오기
        stuId   = pref.getString(getString(R.string.key_studentID), "");
        stuPwd  = pref.getString(getString(R.string.key_studentUCampusPassword), "");

        // 로그인 시도
        request = UTILS.makeRequestClientForUCampus(this, Interface.LOGIN_URL);
        loginThread = new LoginThread();
        loginThread.start();
        // 유캠퍼스 접속 시도
        new GetUcampusThread().start();
    }

    /** 리사이클러 뷰 초기화 및 다이얼로그 생성 메소드 */
    void initView() {
        // 리사이클러 뷰 설정
        recyclerView = (RecyclerView)findViewById(R.id.ucampus_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2열 그리드
        recyclerView.setAdapter(adapter = new UCampusMainAdapter(this));
        // 다이얼로그 생성
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
    }

    /** 유캠퍼스 로그인을 위한 Thread 객체 */
    class LoginThread extends Thread {
        @Override public void run() {
            // 로그인 요청 ===========================================================================
            Call<ResponseBody> call = request.getCookie("2", "http%3A%2F%2Finfo.kw.ac.kr%2F", "11",
                    "", "KOREAN", stuId, stuPwd);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml = new String(response.body().bytes(), "EUC-kR");
                if (responseHtml.contains("비밀번호가 맞지 않습니다") || responseHtml.contains("비밀번호를 입력하세요"))
                    throw new IOException();
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "로그인에 실패했습니다.");
                    }
                });
                e.printStackTrace();
                return;
            }

            // 유캠퍼스 로드 요청 =====================================================================
            call = request.getUcampus();
            try {
                call.execute();
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    /** 유캠퍼스 로그인 이후 메인 화면을 얻어오기 위한 Thread 객체 */
    class GetUcampusThread extends Thread {
        @Override public void run() {
            // 로그인 스레드가 끝나야 실행
            try {
                loginThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 유캠퍼스 데이터 로드 요청 ===============================================================
            Call<ResponseBody> call = request.getUcampusCore(/*"", "univ", "N000003", "U2016209697220013", "2016", "2", "01", "2014722028UA", "", "11", ""*/);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml;
                if (response.body() == null)
                    responseHtml = "";
                else responseHtml = response.body().string();
                // 로그인 이후 나온 데이터를 파싱하여 RecyclerView 데이터로 넣어줌
                setViewData(responseHtml);
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
            finally {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }

        void setViewData(String html) throws IOException {
            Document doc = Jsoup.parse(html);
            if (doc.text().equals(""))
                throw new IOException();

            // 수강 과목 및 데이터 추출
            Elements elements = doc.select("table.main_box").last().select("td.list_txt");

            UCampusMainData data = null;
            Element element = elements.first();
            for (int i = 0; i < elements.size();) {
                data = new UCampusMainData();
                // 과목명
                String name = element.text();
                name = name.replace("[학부]", "");
                data.subjName = name;
                element = elements.get(++i);
                // 강의실
                data.subjPlace = element.text();
                element = elements.get(++i);
                // 강의번호
                String[] subjectData = element.html().split("\'");
                data.subjId = subjectData[1];
                data.subjYear = subjectData[3];
                data.subjTerm = subjectData[5];
                data.subjClass = subjectData[7];
                // 신규 과제 혹은 공지사항 여부 확인
                if (subjectData.length > 9) {
                    String newSomething = subjectData[16];
                    // 신규 공지사항
                    if (newSomething.contains("btn_n.gif")) data.newNotice = true;
                    // 신규 과제
                    else                                    data.newAssignment = true;
                }

                // 데이터 추가
                adapter.addData(data);
                // 다음 공지 유무 확인 및 파싱 종료
                if (element != elements.last()) element = elements.get(++i);
                else                            break;
            }

            // 데이터 로드가 모두 끝나면 어댑터에 알리고 다이얼로그를 없앰
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}