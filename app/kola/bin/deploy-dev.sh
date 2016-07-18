#!/bin/sh
rm -rf plugins/* platforms/*
gulp
cordova platform add android
ionic run android
