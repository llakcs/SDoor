LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
APP_PROJECT_PATH := $(call my-dir)/../
APP_MODULES:= detection_based_tracker devicecontrol
test-root-dir:= $(APP_PROJECT_PATH)jni/
APP_STL := gnustl_static
APP_CPPFLAGS := -frtti -fexceptions
APP_ABI := armeabi armeabi-v7a
APP_PLATFORM := android-19

