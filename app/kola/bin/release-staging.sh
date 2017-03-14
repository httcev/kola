#!/bin/sh
rm -rf plugins/* platforms/*
ENV=staging gulp
cordova platform add android
ionic build android --release
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /home/tittel/share/work/httc/httc-keystore/httc.keystore platforms/android/build/outputs/apk/android-release-unsigned.apk httc \
&& zipalign -f -v 4 platforms/android/build/outputs/apk/android-release-unsigned.apk kola-staging.apk
