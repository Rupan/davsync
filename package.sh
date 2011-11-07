#!/bin/bash

# This script should be run from the root of an Android project

KEYSTORE='../android.keystore'

if [ ! -f "${KEYSTORE}" ]
then
  keytool -genkey -v -alias release -keyalg RSA -keysize 4096 -validity 10000 -keystore ${KEYSTORE}
fi

if [ ! -f "local.properties" ]
then
  android update project --name davsync --target android-9 --path $PWD --subprojects
fi

# rebuild JNI shared objects, if applicable
if [ -d "jni" ]
then
  rm -rf obj libs/armeabi*/*.so
  ndk-build
  if [ $? -ne 0 ]
  then
    echo "Native build failed, bailing"
    exit 1
  fi
fi

# rebuild APK
ant clean
ant release

if [ "${1}" == "push" ]
then
  # push to device and wait for status messages
  adb install -r ./bin/davsync-release.apk
  adb logcat
fi
