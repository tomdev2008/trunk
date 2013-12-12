
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Aisound

LOCAL_LDLIBS := -L$(LOCAL_PATH)/Lib -lAiSound5 -llog

LOCAL_SRC_FILES := AisoundJni.c

TARGET_CFLAGS += -g

include $(BUILD_SHARED_LIBRARY)
