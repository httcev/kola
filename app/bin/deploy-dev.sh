#!/bin/sh
rm -rf plugins/* platforms/*
ionic cordova platform add android
ionic cordova run android
