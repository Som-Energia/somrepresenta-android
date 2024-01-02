# somrepresenta-android

An experimental Android wrapper for somrepresenta-oficinavirtual

## Minimal instructions for Pythonists (written by Pythonists so if you are a Androist/Javaist, please, improve)

```bash
# TODO: Environment install

./gradlew uninstallAll # This uninstall existing version of the app, needed before reinstalling
./gradlew installDebug # This builds and installs a new version of the API
./gradlew assembleRelease # This builds a release APK (does not install, requires signature)
adb shell logcat | grep epresenta # This shows the log

```


