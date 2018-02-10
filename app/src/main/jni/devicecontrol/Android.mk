LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE :=STATIC

LOCAL_MODULE := devicecontrol

LOCAL_SRC_FILES :=devicecontrol.cpp

LOCAL_LDLIBS +=  -lm -llog

include $(BUILD_SHARED_LIBRARY)