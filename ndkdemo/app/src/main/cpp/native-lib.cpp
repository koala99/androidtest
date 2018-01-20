#include <jni.h>
#include <string>
#include <android/log.h>
#include <string.h>

using namespace std;

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "ndk", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "ndk", __VA_ARGS__)


extern "C"
JNIEXPORT jstring
JNICALL
Java_com_android_ndkdemo_NativeUtils_stringFromJNI(
        JNIEnv *env,
        jobject) {
    string hello = "Hello from C++";
    hello = hello + "sss";
    LOGD("this is a demo");
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_android_ndkdemo_NativeUtils_getString(
        JNIEnv *env, jclass) {

    return env->NewStringUTF("Hello JNI!");

}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_android_ndkdemo_NativeUtils_changeString(
        JNIEnv *env, jclass, jstring str) {

    const char *char1 = env->GetStringUTFChars(str, false);
    string str2(char1);
    str2 += "1222";
    env->ReleaseStringUTFChars(str, char1);
    return env->NewStringUTF(str2.c_str());
}

extern "C"
JNIEXPORT jobject
JNICALL
Java_com_android_ndkdemo_NativeUtils_changeStringList(
        JNIEnv *env, jclass, jobject strlist) {
    //静态方法 GetStaticMethodID
    jclass cls_arraylist = env->GetObjectClass(strlist);
    jmethodID arraylist_get = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
    jmethodID arraylist_size = env->GetMethodID(cls_arraylist, "size", "()I");
    jmethodID arrayList_add = env->GetMethodID(cls_arraylist, "add", "(Ljava/lang/Object;)Z");
    const char *cc = "hello";
    jstring jrstr;
    jrstr = env->NewStringUTF(cc);
//    env->CallObjectMethod(strlist, arrayList_add, jrstr);
    env->CallBooleanMethod(strlist, arrayList_add, jrstr);

    jint len = env->CallIntMethod(strlist, arraylist_size);
    char buf[64];
    sprintf(buf, "%d", len);
    jstring str = env->NewStringUTF(buf);
    LOGD(env->GetStringUTFChars(str, false));
    return strlist;
}
