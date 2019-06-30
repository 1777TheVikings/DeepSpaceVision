# DeepSpaceVision

This is Team 1777's offseason vision processing code for Destination: Deep Space. The code is written in Java and is intended to be run on a [Jetson Nano](https://developer.nvidia.com/embedded/jetson-nano-developer-kit). This project is heavily integrated with Visual Studio Code for ease of use.

Some cool features:

- [TCP server](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/src/main/java/DeepSpaceVision/TcpServer.java) to send data to the RoboRIO
    - Much lower latency than NetworkTables
    - Runs in a separate thread to keep blocking calls from stalling the main processing
- Proper build/deploy system using Gradle ([build.gradle](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/build.gradle))
    - Builds a fat .jar file that contains the OpenCV .jar
    - Deploys to a Jetson Nano at `tegra-ubuntu.local` over SSH, stopping and starting a [custom service](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/src/main/resources/vision-code.service) along the way
- Unit testing of the processing pipeline
    - Ensures changes to the pipeline don't break the detection or cause significant inaccuracy

# Setup

## On your development computer (Windows 10)

1. Install Java JDK 11.

    a. Download and run the [Java 11 JDK installer](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html) for your system. OpenJDK should also work for Linux users. (Note: in Java 11, the JRE is included in the JDK installer.)

    b. Find where the installer put the JDK. There should be a folder called `jdk-11.X.X` in `C:\\Program Files\\Java\\` if you used the 64-bit installer, or in `C:\\Program Files (x86)\\Java` if you used the 32-bit installer. Either way, remember what the exact name of the JDK folder is (e.g. `jdk-11.0.3`).

    c. Open Control Panel and enter "System and Security" -> "System" -> "Advanced system settings". In the "Advanced" tab, click "Environment Variables..."

    d. Click on "Path" in "System variables" and click "Edit..."

    e. Click "New". In the new field, type `C:\\Program Files\\Java\\<FOLDER_NAME_FROM_STEP_B>\\bin`.

    f. Click "OK" to exit the Path editing window. Under "System variables", click "New...". Set the variable name to `JAVA_HOME` and the variable value to `C:\\Program Files\\Java\\<FOLDER_NAME_FROM_STEP_B>`. Click "OK" to save it.

2. Install OpenCV 4.1.0.

    a. If you don' want to deal with compiling from source, you can download the [Windows installer](https://opencv.org/releases/) instead. A custom build isn't necessary for this use case.

    b. After the installer extracts, move the `opencv` folder to your user root folder (probably `C:\\Users\\<YOUR_USERNAME_HERE>`).

    c. Open Control Panel and enter "System and Security" -> "System" -> "Advanced system settings". In the "Advanced" tab, click "Environment Variables..."

    d. Click on "Path" in "System variables" and click "Edit..."

    e. Click "New". In the new field, type `C:\\Users\\<YOUR_USERNAME_HERE>\\opencv\\build\\java\\x64`. If you use a 32-bit install of Windows 10, replace `x64` at the end with `x86`.

    f. Restart your computer.

3. Install [Visual Studio Code](https://code.visualstudio.com/). You can actually use whatever Java IDE you want, but this project is already well integrated with Code.

    a. Install the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) to make use of all of that nice IDE integration.

4. Install [Bonjour Print Services for Windows](https://support.apple.com/kb/dl999). It isn't strictly necessary, but it'll make life a lot easier.

## On your deployment target (Jetson Nano)

This assumes that you have already done the basic setup for your Nano. If you haven't follow [NVIDIA's guide](https://developer.nvidia.com/embedded/learn/get-started-jetson-nano-devkit) to get the bare minimum setup done and [this other guide](https://jkjung-avt.github.io/setting-up-nano/) to get it ready to build stuff. It is strongly recommended to use an SD card with at least 32 GB of space; a 16 GB one is enough to install Ubuntu, but you won't have enough space to make a swap file or download the OpenCV source files.

1. Install [OpenJDK 11](https://packages.ubuntu.com/bionic/openjdk-11-jdk): `sudo apt-get install openjdk-11-jdk openjdk-11-jre`

2. Build and install OpenCV 4.1.0.

    a. Download [this script](https://github.com/AastaNV/JEP/blob/master/script/install_opencv4.0.0_Nano.sh): `wget https://raw.githubusercontent.com/AastaNV/JEP/master/script/install_opencv4.0.0_Nano.sh`

    b. Edit the downloaded file and replace **every** `4.0.0` with `4.1.0`.

    c. Make your Nano enter zoom-zoom mode: `sudo nvpmodel -m 0 && sudo jetson_clocks`. Don't do this if your power supply can't handle 10 W.

    d. Run the script: `./install_opencv4.0.0_Nano.sh`

3. Put the important library somewhere that the JVM can find it: `sudo cp opencv-4.1.0/build/lib/libopencv-java410.so /usr/lib/`

## Stuff to change in this repository

- In [build.gradle](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/build.gradle), change the info in `remotes.jetson` to match your Nano's hostname and user login. You will also need to modify the contents of `task deploy` to make the path correct for your system.

- In [vision-code.service](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/src/main/resources/vision-code.service), change `WorkingDirectory` and `ExecStart` to use the correct path for your system.
