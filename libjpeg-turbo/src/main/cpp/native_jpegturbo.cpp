//
// Created by stoneslc on 2020/4/5.
//

#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <malloc.h>
#include "include/turbojpeg.h"




#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "jpeg-turbo", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "jpeg-turbo", format, ##__VA_ARGS__)

unsigned long jpeg_size = 640 * 480;
unsigned char *jpeg_buff = (unsigned char*)malloc(640 * 480);
unsigned char *pcheck;
unsigned char* frameBuffer = NULL;



int yuv2Jpeg(unsigned char* yuv_buffer,int width,int height,int sub_sample ,unsigned char** jpeg_buffer,unsigned long *jpeg_size,int quality){
    tjhandle handle = NULL;
    int flags = 0;
    int need_size = 0;
    int padding = 4;
    int ret = 0;


    int yuv_size = width * height * 3 / 2;

    handle = tjInitCompress();

    need_size = tjBufSizeYUV2(width,padding,height,sub_sample);

    if(need_size != yuv_size){
        LOGE("we detect yuv size: %d,but give: %d !",need_size,yuv_size);
        return -1;
    }

    ret = tjCompressFromYUV(handle,yuv_buffer,width,padding,height,sub_sample,jpeg_buffer,jpeg_size,quality,flags);
    if(ret < 0){
        LOGE("compress to jpeg failed %s\n",tjGetErrorStr());

    }
    tjDestroy(handle);
    return  ret;
}


size_t writeFile(const char* path ,uint8_t *buff,int size){
    FILE *file = fopen(path,"wb");
    if(file == NULL){
        return -1;
    }
    size_t  ret = fwrite(buff,1,size,file);
    fclose(file);
    return ret;
}



extern "C" JNIEXPORT void JNICALL
Java_com_lc_jpeg_JpegTurbo_init(JNIEnv* env,jobject thiz,jobject buffer){

    void* address = env->GetDirectBufferAddress(buffer);
    frameBuffer = static_cast<unsigned char *>(address);
}
extern "C" JNIEXPORT void JNICALL
Java_com_lc_jpeg_JpegTurbo_yuvJpeg(JNIEnv* env,jobject thiz,jbyteArray yuv,jint width,jint height) {
    unsigned char* srcBuff =  (unsigned char*)env->GetByteArrayElements(yuv,JNI_FALSE);
    pcheck = jpeg_buff;
    int ret = yuv2Jpeg(srcBuff,width,height,TJSAMP_420, &jpeg_buff,&jpeg_size,75);
    LOGE("ret %d",ret);
    env->ReleaseByteArrayElements(yuv, reinterpret_cast<jbyte *>(srcBuff), 0);
    //const char* file = env->GetStringUTFChars(path,JNI_FALSE);
    //writeFile(file,jpeg_buff,jpeg_size);
    //env->ReleaseStringUTFChars(path,file);




    jclass c = env->GetObjectClass(thiz);
    jmethodID onFrame = env->GetMethodID(c,"onFrame", "([B)V");
    jbyteArray data = env->NewByteArray(jpeg_size);
    env->SetByteArrayRegion(data, 0,jpeg_size, reinterpret_cast<jbyte *>(jpeg_buff));
    env->CallVoidMethod(thiz,onFrame,data);
    env->DeleteLocalRef(data);

    if(jpeg_buff != pcheck){
        free(jpeg_buff);
        jpeg_buff = pcheck;
    }

}







