#!/bin/sh
rm -rf plugins/* platforms/*
cordova platform add android
ionic cordova build --release android
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /home/tittel/share/work/httc/httc-keystore/httc.keystore platforms/android/app/build/outputs/apk/release/app-release-unsigned.apk httc \
&& zipalign -f -v 4 platforms/android/app/build/outputs/apk/release/app-release-unsigned.apk kola-prod.apk
