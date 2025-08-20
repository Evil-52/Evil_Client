# Evil Client Lite (Fabric 1.21)

Single-mod that adds **ToggleSprint**, **Zoom (hold C)**, **Keystrokes HUD**, and **FPS HUD**. No hacks.

## Build locally (Windows-friendly)
1. Install **JDK 21**.
2. Open a terminal in this folder.
3. Run:
   ```
   gradlew.bat build
   ```
4. The mod JAR will be at:
   `build/libs/evilclientlite-0.1.0.jar`

## Use without local build (GitHub Actions)
1. Create a **new GitHub repo** and upload these files.
2. In the repo, go to **Actions** → enable workflows (first run might take a minute).
3. Add this workflow file at `.github/workflows/build.yml`:
   ```yaml
   name: Build EvilClientLite
   on: [push, workflow_dispatch]
   jobs:
     build:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v4
         - uses: actions/setup-java@v4
           with:
             distribution: temurin
             java-version: '21'
         - uses: gradle/gradle-build-action@v3
           with:
             gradle-version: wrapper
         - run: chmod +x ./gradlew
         - run: ./gradlew build
         - uses: actions/upload-artifact@v4
           with:
             name: evilclientlite-jar
             path: build/libs/*.jar
   ```
4. Go to **Actions → Build EvilClientLite → Artifacts** and **download the JAR**.

## Install
- Copy the JAR to `.minecraft/mods/` (Fabric 1.21 + Fabric API required).

## Controls
- ToggleSprint: **G** (press to toggle)
- Zoom: **Hold C**
- Keystrokes: auto on (W/A/S/D/SPACE/SHIFT)
