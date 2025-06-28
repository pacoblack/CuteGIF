#include <jni.h>
#include <string>
#include <cstring>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 加密密钥 - 实际项目中应该从安全源获取
const char XOR_KEY = 0xAB; // 0-255之间的任意值

// 安全擦除内存
void secureErase(void* ptr, size_t size) {
    if (ptr) {
        memset(ptr, 0x00, size);  // 用零覆盖
        memset(ptr, 0xFF, size);  // 用1覆盖
        memset(ptr, 0xAA, size);  // 用10101010模式覆盖
        memset(ptr, 0x00, size);  // 最终归零
    }
}

// XOR加密/解密函数
void xorEncryptDecrypt(char* data, size_t size, char key) {
    for (size_t i = 0; i < size; ++i) {
        data[i] = data[i] ^ key;
    }
}

// 获取加密后的AppID（实际项目中应该从安全存储获取）
const unsigned char* getEncryptedAppId(size_t* outSize) {
    // 这里是加密后的AppID字节数组
    // 原始值: "wx1234567890abcdef" (18个字符)
    static const unsigned char encryptedAppId[] = {
            0xDC, 0xD3, 0x9A, 0x99, 0x98, 0x9F, 0x9E, 0x9D,
            0x9C, 0x93, 0x92, 0x9B, 0xCA, 0xC9, 0xC8, 0xCF,
            0xCE, 0xCD, 0x00  // 以null结尾
    };

    *outSize = sizeof(encryptedAppId) - 1; // 减去null终止符
    return encryptedAppId;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_cv_pic_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_cv_pic_nativelib_NativeLib_getWxAppIdFromJNI(JNIEnv *env, jobject thiz) {
    size_t encryptedSize = 0;
    const unsigned char* encryptedData = getEncryptedAppId(&encryptedSize);

    if (!encryptedData || encryptedSize == 0) {
        LOGE("Failed to get encrypted AppID");
        return env->NewStringUTF("");
    }

    // 分配内存用于解密
    char* decryptedBuffer = new (std::nothrow) char[encryptedSize + 1];
    if (!decryptedBuffer) {
        LOGE("Memory allocation failed");
        return env->NewStringUTF("");
    }

    // 复制加密数据到缓冲区
    memcpy(decryptedBuffer, encryptedData, encryptedSize);
    decryptedBuffer[encryptedSize] = '\0'; // 确保null终止

    // 解密数据
    xorEncryptDecrypt(decryptedBuffer, encryptedSize, XOR_KEY);

    LOGI("Decrypted AppID: %s", decryptedBuffer);

    // 创建Java字符串
    jstring result = env->NewStringUTF(decryptedBuffer);

    // 安全擦除解密缓冲区
    secureErase(decryptedBuffer, encryptedSize);
    delete[] decryptedBuffer;

    return result;
}

// 辅助函数：在JNI中加密字符串（用于生成加密数据）
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_cv_pic_nativelib_NativeLib_encryptString(JNIEnv *env, jobject thiz, jstring input) {
    const char *inputStr = env->GetStringUTFChars(input, nullptr);
    if (!inputStr) {
        return nullptr;
    }

    size_t len = strlen(inputStr);
    jbyteArray result = env->NewByteArray(len);

    // 复制数据到新缓冲区
    char* buffer = new char[len + 1];
    strcpy(buffer, inputStr);

    // 加密数据
    xorEncryptDecrypt(buffer, len, XOR_KEY);

    // 设置字节数组
    env->SetByteArrayRegion(result, 0, len, reinterpret_cast<jbyte*>(buffer));

    // 清理
    secureErase(buffer, len);
    delete[] buffer;
    env->ReleaseStringUTFChars(input, inputStr);

    return result;
}