#include <jni.h>
#include <string>
#include <android/log.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
//#include "../../../../../../../Library/Android/sdk/ndk-bundle/platforms/android-19/arch-arm/usr/include/linux/in.h"

std::string hoge(std::string s) {
    std::string ret = "";
    for(int i = 0; i < s.size(); i++) {
        ret += s[i] ^ (i+1);
    }
    return ret;
}

std::string fuga(std::string s) {
    std::string ret = "";
    for(int i = 0; i < s.size(); i++) {
        ret += (s[i] ^ (i+1)) - 2;
    }
    return ret;
}

extern "C"
jstring
Java_totem_androidquiz_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
void
Java_totem_androidquiz_MainActivity_logWrite(JNIEnv* env, jobject) {
    std::string str = "gnbc~2pmz:fi,Q{x aL%fI$j}E}p)yb";
    __android_log_print(ANDROID_LOG_DEBUG, "flag",
                        "%s",
                        hoge(str).c_str());
}

extern "C"
void
Java_totem_androidquiz_MainActivity_sendPacket(JNIEnv * env, jobject) {
    std::string str = "il`mxoso?|(m{d<ep!fu#`}yqt-ub";
    int sockfd;
    struct sockaddr_in server;

    __android_log_print(ANDROID_LOG_DEBUG, "flag",
                        "%s",
                        fuga(str).c_str());

    if((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "socket", "could not get sockfd");
        return;
    }


    server.sin_family = AF_INET;
    server.sin_addr.s_addr = inet_addr("192.168.0.1");
    server.sin_port = htons(12345);

    std::string decrypted =  fuga(str);
    for(int i = 0; i < decrypted.size(); i++) {
        sendto(sockfd, &decrypted[i], 1, MSG_DONTWAIT, (struct sockaddr *)&server, sizeof(server));
        __android_log_print(ANDROID_LOG_DEBUG, "socket", "sending %c", decrypted[i]);
    }

    close(sockfd);
    return;
}

