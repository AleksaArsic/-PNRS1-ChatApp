//
// Created by Cisra on 6/4/2018.
//

/*
 * Androi.mk omogucava prevodjenje C/C++ biblioteke u .so datoteku
 * Kada pozovemo ndk-build izgradjuje se native kod i biblioteka .so
 * koju kompajler smesta u libs folder
 */

#include "arsic_aleksa_chatapplication_MyNDK.h"
#include "string.h"

#define KEY "This is the key"
/*
 * jobject represents the types of classes that are not supported by JNI
 * like Object in java implicitly extends all classes
 */

 /*
  * JNIEnv *env represents the link with virtual machine
  * Valid only in method call
  * Consists of very big number of VM methods including some other methods
  */
JNIEXPORT jstring JNICALL Java_arsic_aleksa_chatapplication_MyNDK_encryptDecryptMessage
    (JNIEnv * env, jobject jo, jstring message){

    // Converts Java String to modified UTF-8 char array
    const char *nativeString = env->GetStringUTFChars(message, NULL);
    int message_len = strlen(nativeString);

    char messageTemp[message_len+1];
    int key_len = strlen(KEY);

    memset(messageTemp, 0, message_len);

    int i = 0;

    for(i = 0; i < message_len; i++){
        if (0 <= nativeString[i] && nativeString[i] < 0x7d){
            messageTemp[i] = nativeString[i] ^ KEY[i % key_len];

            if(messageTemp[i] < 32) messageTemp[i] = messageTemp[i] ^ KEY[i % key_len];

        }
    }

    // Informs the VM that the native code no longer needs access to utf
    // and releases the memory allocated for modified UTF-8 char array
    env->ReleaseStringUTFChars(message, nativeString);
    messageTemp[message_len] = 0;

    // Creates Java String
    return env->NewStringUTF(messageTemp);
}
