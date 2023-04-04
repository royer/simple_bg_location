#!/bin/bash

# launch emulator
$ANDROID_HOME/emulator/emulator @Pixel_5_API_28 &

#wait for emulator to start
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'