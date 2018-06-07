LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := encryptDecryptData
LOCAL_SRC_FILES := encryptDecryptData.cpp
include $(BUILD_SHARED_LIBRARY)
