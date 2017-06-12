#include <jni.h>
#include <string>
#include <android/log.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

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
Java_com_totem_analyze_1app_1quiz_MainActivity_getPath(
        JNIEnv* env,
        jobject /* this */) {
    std::string path = "iuuq;..mhfiu/enutq/nsf.tqmne`.mhfiu/enutq/nsf289777/qof";
    std::string str = "";
    for(int i = 0; i < path.size(); i++) {
        str += (path[i] ^ 0x01);
    }
    return env->NewStringUTF(str.c_str());
}

extern "C"
jstring
Java_com_totem_analyze_1app_1quiz_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject ) {
    std::string hoge = "fmen{=i5z0jn!Vpi1r[8sVl0deag_oh5g|";
    std::string str = "";
    for(int i = 0; i < hoge.size(); i++) {
        str += hoge[i] ^ i*i%16;
    }
    return env->NewStringUTF(str.c_str());
}


extern "C"
void
Java_com_totem_analyze_1app_1quiz_MainActivity_logWrite(JNIEnv*, jobject) {
    std::string str = "gnbc~2pmz:fi,Q{x aL%fI$j}E}p)yb";
    __android_log_print(ANDROID_LOG_DEBUG, "flag",
                        "%s",
                        hoge(str).c_str());
}

extern "C"
void
Java_com_totem_analyze_1app_1quiz_MainActivity_sendPacket(JNIEnv *, jobject) {
    std::string str = "il`mxoso?|(m{d<ep!fu#`}yqt-ub";
    int sockfd;
    struct sockaddr_in server;

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
    }

    close(sockfd);
    return;
}

extern "C"
jstring
Java_com_totem_analyze_1app_1quiz_MainActivity_cryptStr(JNIEnv * env, jobject, jstring s, jboolean b) {
    std::string str = env->GetStringUTFChars(s, 0);
    if(b) {
        str += "True";
    } else {
        str += "False";
    }
    for(int i = 0; i < str.length(); i++) {
        str[i] = (str[i] + (char)(i % 3)) ^ (char) 0x01;
    }
    return env->NewStringUTF(str.c_str());
}