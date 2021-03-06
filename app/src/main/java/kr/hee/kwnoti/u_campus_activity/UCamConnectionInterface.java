package kr.hee.kwnoti.u_campus_activity;

import androidx.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/** 최초 로그인 시 사용하는 인터페이스
 * POST 로그인을 사용하며 Response 에서 쿠키값만 가져오는 용도로 사용한다 */
public interface UCamConnectionInterface {
    /* ====================================== 유캠퍼스 로그인 ====================================== */
    String LOGIN_URL = "http://info.kw.ac.kr/";
    @FormUrlEncoded @POST("webnote/login/login_proc.php")
    Call<ResponseBody> getCookie(
            @Field("login_type")            String login_type,
            @Field("redirect_url")          String redirect_url,
            @Field("gubun_code")            String gubun_code,  // 사용자 유형(학생은 11)
            @Field("layout_opt") @Nullable  String layout_opt,  // ""
            @Field("p_language")            String language,
            @Field("member_no")             String id,          // 학번
            @Field("password")              String pwd          // 비밀번호
    );

    /* ==================================== 유캠퍼스 데이터 로드 ==================================== */
    String UCAMPUS_URL = "http://info2.kw.ac.kr/";
    // 원칙적으로는 host에 /servlet/~ 하는 것이 맞으나 그지같은 유캠퍼스 호스팅때문에 어쩔 수 없이 길게 우회
    @GET("http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
    Call<ResponseBody> getUcampus();

    /* ========================== 유캠퍼스 코어 데이터(학생의 실제 정보) 로드 ========================== */
    //@FormUrlEncoded
    @POST("http://info2.kw.ac.kr/servlet/controller.homepage.KwuMainServlet?p_process=openStu&")
    Call<ResponseBody> getUcampusCore(/*
            @Field("p_process") @Nullable       String process,
            @Field("p_gate")            String gate,
            @Field("p_grcode")          String gradeCode,
            @Field("p_subj")            String subject,
            @Field("p_year")            String year,
            @Field("p_subjseq")         String sequence,
            @Field("p_class")           String classNum,
            @Field("p_userid")          String id,
            @Field("p_page") @Nullable  String page,
            @Field("gubun_code")        String gubunCode,
            @Field("p_tutor_name") @Nullable    String tutorName*/
    );

    /* =========================== 강의 별 데이터(공지사항, 자료실 등) 로드 ============================ */
    @FormUrlEncoded @POST
    Call<ResponseBody> getList(
            @Url String url,
            @Field("p_process") @Nullable   String process,
            @Field("p_grcode")              String gradeCode,
            @Field("p_subj")                String subjectNum,
            @Field("p_year")                String year,
            @Field("p_subjseq")             String sequence,
            @Field("p_class")               String classNum,
            @Field("p_pageno")              int pageNum);

    /* ======================================= 게시글 로드 ======================================== */
    @FormUrlEncoded @POST
    Call<ResponseBody> getContent(
            @Url String url,
            @Field("p_process")     String process,
            @Field("p_bdseq")       String sequence);

    /* ===================================== 강의 계획서 로드 ====================================== */
    @GET
    Call<ResponseBody> getLectureNote(
            @Url String url/*,
            @Query("bunban_no")     String classNumber,
            @Query("hakgi")         char semester,
            @Query("open_grade")    char grade,
            @Query("open_gwamok_no")String subjectNumber,
            @Query("open_major_code")String majorCode,
            @Query("this_year")     String thisYear*/
    );




    /* ===================================== 강의 계획서 검색 ====================================== */
    @GET("webnote/lecture/h_lecture.php?layout_opt=N")
    Call<ResponseBody> getLectureListMain();

    @FormUrlEncoded
    @POST("webnote/lecture/h_lecture.php?layout_opt=N&mode=view&user_opt=&skin_opt=&show_hakbu=&sugang_opt=all&x=29&y=18&")
    Call<ResponseBody> getLectureList(
            @Field(value = "hh", encoded = true)        String lectureName,     // 강좌명
            @Query(value = "prof_name", encoded = true) String profName,        // 교수이름
            @Query(value = "fsel1", encoded = true)     String commonSubject,   // 공통과목
            @Query(value = "this_year", encoded = true) String year,            // 검색년도
            @Query(value = "hakgi", encoded = true)     String semester,        // 검색학기
            @Query(value = "fsel2", encoded = true)     String major,           // 학과/전공
            @Query(value = "fsel4", encoded = true)     String major2           // ??
    );
}