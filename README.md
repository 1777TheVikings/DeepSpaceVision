# DeepSpaceVision

This is Team 1777's offseason vision processing code for Destination: Deep Space. The code is written in Java and is intended to be run on a [Jetson Nano](https://developer.nvidia.com/embedded/jetson-nano-developer-kit). This project is heavily integrated with Visual Studio Code for ease of use.

Some cool features:

- Proper build/deploy system using Gradle ([build.gradle](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/build.gradle))
    - Builds a fat .jar file that contains the OpenCV .jar
    - Deploys to `jetson-nano.local` over SSH, stopping and starting a [custom service](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/src/main/resources/vision-code.service) along the way
- Unit testing of the processing pipeline
    - Ensures changes to the pipeline don't break the detection or cause significant inaccuracy

# Setup

Required software/libraries on development computer:

- [Visual Studio Code](https://code.visualstudio.com/) is the IDE of choice for team 1777 and provides strong integration with Gradle and unit testing in this project. The [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) is strongly recommended as well.
- Java [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [JRE](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) for version 8
- OpenCV 3.4.6 (Windows installer can be downloaded [here](https://opencv.org/releases/), a custom build isn't strictly necessary for development)
- [Bonjour Print Services for Windows](https://support.apple.com/kb/dl999) is recommended if developing on a Windows machine, but can be avoided by giving your Jetson a static IP address

Required software/libraries on Jetson Nano:

- OpenJDK [JRE](https://packages.ubuntu.com/bionic/openjdk-8-jre) for version 8
- OpenCV 3.4.6 (do not use the pre-installed OpenCV, compile using [these instructions](https://jkjung-avt.github.io/opencv-on-nano/) instead)

Before deploying to your Jetson, change `jetson-nano.local` in [build.gradle](https://github.com/1777TheVikings/DeepSpaceVision/blob/master/build.gradle) to reflect your Jetson's actual name. Alternatively, set a static IP address on your Jetson and use that instead for extra reliability.
