package kr.hee.kwnoti;

/** 인자 전달 등에서 사용되는 String 값을 보기 편하게 만듦 */
public interface KEY {
    // FCM 디바이스 확인용 토큰
    String PREFERENCE_DEVICE_TOKEN = "Device Token";

    // 어플리케이션의 최초 실행 여부
    // 최초 실행 = true
    String FIRST_USE = "FirstUse";

    // 유캠퍼스 접속용 쿠키셋
    String COOKIE_SET = "Cookies";

    // 인텐트 전달
    String SUBJECT              = "Subject";
    String SUBJECT_LOAD_TYPE    = "SubjectListType";
    String SUBJECT_PLAN         = "강의계획서";
    String SUBJECT_INFO         = "공지사항";
    String SUBJECT_UTIL         = "강의자료실";
    String SUBJECT_STUDENT      = "학생자료실";
    String SUBJECT_ASSIGNMENT   = "과제 제출";
    String SUBJECT_QNA          = "강의 Q&A";

    String BROWSER_DATA         = "BrowserOpenData";
    String BROWSER_FROM_UCAMPUS = "BrowserFromUCampus";
    String BROWSER_FROM_LECTURE_NOTE = "BrowserFromLectureNote";
    String BROWSER_URL          = "BrowserURL";
    String BROWSER_TITLE        = "BrowserTitle";
    String BROWSER_INCLUDE_URL  = "BrowserIncludeLectureUrl";

    // 학생증 사진 파일 이름
    String STUDENT_IMAGE        = "stuImage.jpg";
    // 학식 데이터의 유효기간
    String FOOD_DATE            = "foodDate";
}