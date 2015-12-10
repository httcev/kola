#!/bin/sh
rm -r plugins/* platforms/*
gulp
cordova platform add android
ionic run android
