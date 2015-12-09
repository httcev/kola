#!/bin/sh
rm -r plugins/* platforms/*
ENV=demo gulp
cordova platform add android
ionic build --release android
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /home/tittel/work/httc/httc-keystore/httc.keystore platforms/android/build/outputs/apk/android-release-unsigned.apk httc \
&& zipalign -f -v 4 platforms/android/build/outputs/apk/android-release-unsigned.apk kola-demo.apk
