#include <jni.h>
#include <opencv2/opencv.hpp> // imread 함수 포함하고 있음

//#include <iostream> // cout 함수를 이용해 콘솔에 출력하기 위해 사용
#include <android/asset_manager_jni.h>
#include <android/log.h>


// <opencv2/opencv.hpp> 안의 모든 구조체 함수는 cv namespace로 선언되어 있음
// (만약 이걸 빼먹으면 OpenCV의 각함수와 데이터 구조체를 사용할 때마다 'cv::' 특정 자를사용해야 함.
using namespace cv;
using namespace std;

extern "C" {

//=======================================================================================================================================
// LOADIMAGE => 말 그대로 이미지를 불러들이는 함수
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_loadImage(JNIEnv *env,
                                                                   jobject,
                                                                   jstring imageFileName,
                                                                   jlong addrImage) {

    Mat &img_input = *(Mat *) addrImage;
    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName, 0);

    String path = nativeFileNameString;

    img_input = imread(path, IMREAD_COLOR); // path에서 이미지 파일을 읽어온 다음 그 값을 img_input에 저장 (이미지 주소, 옵션)
}


//=======================================================================================================================================
// 1. GRAY
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing1(JNIEnv *env,
                                                                         jobject,
                                                                         jlong addrInputImage,
                                                                         jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, COLOR_BGR2GRAY);


}

//=======================================================================================================================================
// 2. HSV
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing2(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, COLOR_BGR2HSV);


}

//=======================================================================================================================================
// 3. detailEnhance
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing3(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);

    float sigma_s=10;
    float sigma_r=0.15f;

    detailEnhance(img_input, img_output, sigma_s, sigma_r);

}

//=======================================================================================================================================
// 4. Luv
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing4(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, COLOR_BGR2Luv);


}

//=======================================================================================================================================
// 5. Stylization
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing5(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    float sigma_s=60;
    float sigma_r=0.45f;

    stylization(img_input, img_output, sigma_s, sigma_r);


}


//=======================================================================================================================================
// 6. AUTUMN
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing6(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, COLORMAP_AUTUMN);


}

//=======================================================================================================================================
// 7. WINTER
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing7(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);

    int flags=1;
    float sigma_s=60;
    float sigma_r=0.45f;

    edgePreservingFilter(img_input, img_output,  flags, sigma_s, sigma_r);


}


//=======================================================================================================================================
// 8. COMIC
//=======================================================================================================================================
JNIEXPORT void JNICALL
Java_thread_seopftware_mychef_Chatting_Chat_1SendPicture_imageprocessing8(JNIEnv *env,
                                                                          jobject,
                                                                          jlong addrInputImage,
                                                                          jlong addrOutputImage) {

    // Mat : 데이터 (영상 이나 이미지)가 저장되는 공간 (Matrix 의 앞 글자만 따옴)


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, CV_RGB2GRAY);
    blur(img_output, img_output, Size(5, 5));
    Canny(img_output, img_output, 50, 150, 5);


}

}


