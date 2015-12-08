#!/bin/sh
ENV=staging gulp
ionic build --release android
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /home/tittel/work/httc/httc-keystore/httc.keystore platforms/android/build/outputs/apk/android-release-unsigned.apk httc \
&& zipalign -f -v 4 platforms/android/build/outputs/apk/android-release-unsigned.apk kola-staging.apk
