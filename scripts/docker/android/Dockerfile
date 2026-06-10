FROM archlinux:base

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    JAVA_HOME=/usr/lib/jvm/java-17-openjdk \
    PATH=/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/usr/lib/jvm/java-17-openjdk/bin:$PATH

RUN sed -i '/^\[options\]/a DisableSandbox' /etc/pacman.conf

RUN pacman -Syu --noconfirm \
        base-devel \
        curl \
        git \
        jdk17-openjdk \
        unzip \
    && pacman -Scc --noconfirm

RUN mkdir -p /opt/android-sdk/cmdline-tools \
    && curl -fsSL https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -o /tmp/android-commandline-tools.zip \
    && unzip -q /tmp/android-commandline-tools.zip -d /opt/android-sdk/cmdline-tools \
    && mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest \
    && rm /tmp/android-commandline-tools.zip

RUN yes | sdkmanager --licenses >/dev/null \
    && sdkmanager \
        "platform-tools" \
        "platforms;android-36" \
        "build-tools;36.0.0"
