#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>


using namespace cv;
using namespace std;

//=======================================================================================================================================
// 얼굴 검출 함수들 있음 - Register_chef5.java , Detector_camera.java
//=======================================================================================================================================


extern "C" {


//======================================================================================================================
// 이미지 리사이즈 함수
//======================================================================================================================

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}
//======================================================================================================================



//======================================================================================================================
// 얼굴 인식 및 눈 인식 학습 파일 불러오는 곳 - Register_chef5.java
JNIEXPORT jlong JNICALL
Java_thread_seopftware_mychef_Register_Register_1chef5_loadCascade(JNIEnv *env,
                                                                    jclass type,
                                                                    jstring cascadeFileName) {

    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);

    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName, nativeFileNameString);

    return ret;
}
//======================================================================================================================



//======================================================================================================================
// 얼굴 인식할 이미지 파일을 읽어오는 함수 - Register_chef5.java
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Register_Register_1chef5_loadImage(JNIEnv *env,
                                                                 jobject instance,
                                                                 jstring imageFileName,
                                                                 jlong addrImage) {
    Mat &img_input = *(Mat *) addrImage;
    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName, 0);
    String path = nativeFileNameString;

    img_input = imread(path, IMREAD_COLOR); // path에서 이미지 파일을 읽어온 다음 그 값을 img_input에 저장 (이미지 주소, 옵션)
}
//======================================================================================================================

//======================================================================================================================
// 이미지 파일에서 얼굴 검출을 도와줄 함수 - Register_chef5.java
JNIEXPORT jlong JNICALL
Java_thread_seopftware_mychef_Register_Register_1chef5_detect_1album(JNIEnv *env,
                                                                     jclass type,
                                                                     jlong cascadeClassifier_face,
                                                                     jlong cascadeClassifier_eye,
                                                                     jlong matAddrInput,
                                                                     jlong matAddrResult) {

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));

    long num = faces.size();
//        __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
//                            (char *) "face %d found ", faces.size());


    // 인식되는 얼굴에 원을 그려주는 부분
    for (int i = 0; i < faces.size(); i++) {

        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center(real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height / 2);
        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(255, 0, 255), 30, 8, 0);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        Mat faceROI = img_gray(face_area);
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
                                                                        0 | CASCADE_SCALE_IMAGE,
                                                                        Size(30, 30));

        for (size_t j = 0; j < eyes.size(); j++) {
            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
            circle(img_result, eye_center, radius, Scalar(255, 0, 0), 30, 8, 0);
        } // 내부 for문 끝
    } // 외부 for문 끝

    return num; // 인식된 얼굴의 갯수

}
//======================================================================================================================



//======================================================================================================================
// 파일 읽어들이는 함수 - Detector_camera.java
JNIEXPORT jlong JNICALL
Java_thread_seopftware_mychef_Register_Detector_1camera_loadCascade_1camera(JNIEnv *env,
                                                                            jclass type,
                                                                            jstring cascadeFileName) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);

    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName, nativeFileNameString);

    return ret;
}


//======================================================================================================================
// 카메라에서 얼굴 검출을 도와줄 함수 - Detector_camera.java ( 카메라에서 얼굴 검출 함수 )
JNIEXPORT jlong JNICALL
Java_thread_seopftware_mychef_Register_Detector_1camera_detect_1camera
                                                               (JNIEnv *env,
                                                               jclass type,
                                                               jlong cascadeClassifier_face,
                                                               jlong cascadeClassifier_eye,
                                                               jlong matAddrInput,
                                                               jlong matAddrResult) {

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));

    long num = faces.size();
//        __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
//                            (char *) "face %d found ", faces.size());


    // 인식되는 얼굴에 원을 그려주는 부분
    for (int i = 0; i < faces.size(); i++) {

        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center(real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height / 2);
        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(255, 0, 255), 30, 8, 0);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        Mat faceROI = img_gray(face_area);
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
                                                                        0 | CASCADE_SCALE_IMAGE,
                                                                        Size(30, 30));

        for (size_t j = 0; j < eyes.size(); j++) {
            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
            circle(img_result, eye_center, radius, Scalar(255, 0, 0), 30, 8, 0);
        } // 내부 for문 끝
    } // 외부 for문 끝

    return num; // 인식된 얼굴의 갯수
}

//======================================================================================================================

}