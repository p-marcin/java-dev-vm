ARG UBUNTU_VERSION=24.04
FROM alpine:latest AS intellij-idea
ARG INTELLIJ_IDEA_VERSION
ARG INTELLIJ_IDEA_EDITION
RUN wget -q -O "intellij-idea.tar.gz" "https://download.jetbrains.com/idea/ideaI${INTELLIJ_IDEA_EDITION}-${INTELLIJ_IDEA_VERSION}.tar.gz" \
    && mkdir "/intellij-idea" \
    && tar --extract --gzip --no-same-owner --directory "/intellij-idea" --strip-components=1 --file="intellij-idea.tar.gz"

FROM alpine:latest AS kafka
ARG KAFKA_VERSION
ARG SCALA_VERSION
RUN wget -q -O "kafka.tar.gz" "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" \
    && mkdir "/kafka" \
    && tar --extract --gzip --no-same-owner --exclude="windows" --directory "/kafka" --strip-components=1 --file="kafka.tar.gz" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/config" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/libs"

FROM alpine:latest AS keystore-explorer
ARG KEYSTORE_EXPLORER_VERSION
RUN wget -q -O "kse.zip" "https://github.com/kaikramer/keystore-explorer/releases/download/v${KEYSTORE_EXPLORER_VERSION}/kse-${KEYSTORE_EXPLORER_VERSION//./}.zip" \
    && mkdir "/keystore-explorer" \
    && unzip -q "kse.zip" -x "*.exe" -d "/tmp" \
    && mv "/tmp/kse-${KEYSTORE_EXPLORER_VERSION//./}"/* "/keystore-explorer"

FROM alpine:latest AS kitty
RUN wget -q -O - "https://sw.kovidgoyal.net/kitty/installer.sh" | sh "/dev/stdin" dest="/tmp" launch="n" > /dev/null \
    && mkdir "/kitty" \
    && mv "/tmp/kitty.app"/* "/kitty"

FROM alpine:latest AS postman
ARG POSTMAN_VERSION
RUN wget -q -O "postman.tar.gz" "https://dl.pstmn.io/download/${POSTMAN_VERSION}/linux64" \
    && mkdir "/postman" \
	&& tar --extract --gzip --no-same-owner --directory="/postman" --strip-components=2 --file="postman.tar.gz" "Postman/app"

FROM alpine:latest AS sdkman
ARG JAVA_VERSION
ARG MAVEN_VERSION
ARG SPRING_BOOT_VERSION
RUN apk -q update \
    && apk -q add --no-cache bash ca-certificates curl unzip zip \
    && export SDKMAN_DIR="/sdkman" \
    && wget -q -O - "https://get.sdkman.io" | bash > /dev/null \
    && sed --in-place "/sdkman_auto_answer=/c sdkman_auto_answer=true" "${SDKMAN_DIR}/etc/config" \
    && sed --in-place "/sdkman_auto_env=/c sdkman_auto_env=true" "${SDKMAN_DIR}/etc/config" \
    && sed --in-place "/sdkman_colour_enable=/c sdkman_colour_enable=false" "${SDKMAN_DIR}/etc/config" \
    && sed --in-place "/sdkman_curl_connect_timeout=/c sdkman_curl_connect_timeout=15" "${SDKMAN_DIR}/etc/config" \
    && sed --in-place "/sdkman_curl_max_time=/c sdkman_curl_max_time=30" "${SDKMAN_DIR}/etc/config" \
    && sed --in-place "/sdkman_selfupdate_feature=/c sdkman_selfupdate_feature=false" "${SDKMAN_DIR}/etc/config" \
    && bash -c ". \"${SDKMAN_DIR}/bin/sdkman-init.sh\" \
    && sdk install \"java\" \"${JAVA_VERSION}\" > /dev/null \
    && sdk install \"maven\" \"${MAVEN_VERSION}\" > /dev/null \
    && sdk install \"springboot\" \"${SPRING_BOOT_VERSION}\" > /dev/null"

FROM scratch AS dev-tools
COPY --from=kafka /kafka /dev-tools/kafka/
COPY --from=keystore-explorer /keystore-explorer /dev-tools/keystore-explorer/
COPY --from=kitty /kitty /dev-tools/kitty/
COPY --from=postman /postman /dev-tools/postman/
COPY --from=sdkman /sdkman /dev-tools/sdkman/

FROM alpine:latest AS cloud-tools
ARG CTOP_VERSION
ARG KUBECTL_VERSION
ARG KUBECTL_KREW_VERSION
ARG K3D_VERSION
ARG HELM_VERSION
ARG K9S_VERSION
RUN apk -q update \
	&& apk -q add --no-cache git \
	&& mkdir --parents "/cloud-tools/bash_completion.d" "/cloud-tools/.krew" \
    # Ctop
    && wget -q -O "/cloud-tools/ctop" "https://github.com/bcicen/ctop/releases/download/v${CTOP_VERSION}/ctop-${CTOP_VERSION}-linux-amd64" \
    && chmod +x "/cloud-tools/ctop" \
    # Kubectl
    && wget -q -O "/cloud-tools/kubectl" "https://storage.googleapis.com/kubernetes-release/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
    && chmod +x "/cloud-tools/kubectl" \
    && /cloud-tools/kubectl completion bash > "/cloud-tools/bash_completion.d/kubectl" \
	# Kubectl Krew
    && wget -q -O "/tmp/krew.tar.gz" "https://github.com/kubernetes-sigs/krew/releases/download/v${KUBECTL_KREW_VERSION}/krew-linux_amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory "/tmp" --file="/tmp/krew.tar.gz" \
    && /tmp/krew-linux_amd64 install krew > /dev/null \
    && mv "/root/.krew"/* "/cloud-tools/.krew" \
    # K3d
    && wget -q -O "/cloud-tools/k3d" "https://github.com/k3d-io/k3d/releases/download/v${K3D_VERSION}/k3d-linux-amd64" \
    && chmod +x "/cloud-tools/k3d" \
    && /cloud-tools/k3d completion bash > "/cloud-tools/bash_completion.d/k3d" \
    # Helm
    && wget -q -O "helm.tar.gz" "https://get.helm.sh/helm-v${HELM_VERSION}-linux-amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory "/cloud-tools" --strip-components=1 --file="helm.tar.gz" "linux-amd64/helm" \
    && chmod +x "/cloud-tools/helm" \
    && /cloud-tools/helm completion bash > "/cloud-tools/bash_completion.d/helm" \
    # K9s
    && wget -q -O "k9s.tar.gz" "https://github.com/derailed/k9s/releases/download/v${K9S_VERSION}/k9s_Linux_amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory "/cloud-tools" --file="k9s.tar.gz" "k9s" \
    && chmod +x "/cloud-tools/k9s" \
    && /cloud-tools/k9s completion bash > "/cloud-tools/bash_completion.d/k9s"

FROM scratch AS all-tools
COPY --from=dev-tools /dev-tools /all-tools/dev-tools/
COPY --from=cloud-tools /cloud-tools /all-tools/cloud-tools/
COPY --chmod=644 docker-config /all-tools/docker-config/
COPY --chmod=644 home-config /all-tools/home-config/
COPY --chmod=644 kitty-config /all-tools/kitty-config/
COPY scripts yad-panel/yad-panel /all-tools/scripts/
COPY --chmod=644 yad-panel/config /all-tools/yad-panel/config/
COPY --chmod=644 yad-panel/service /all-tools/yad-panel/service/

FROM dokken/ubuntu-${UBUNTU_VERSION}:main
ARG UBUNTU_DEV_VM_VERSION
ARG UID=1001
ENV USER=${image.user} \
	DISPLAY=${image.display}

# System setup
RUN apt-get -qq update -y \
    # Custom repositories enablement
    && apt-get -qq install -y --no-install-recommends software-properties-common \
    # Custom repositories installation
    && add-apt-repository -y "ppa:mozillateam/ppa" > /dev/null \
    && add-apt-repository -y "ppa:git-core/ppa" > /dev/null \
    && wget -q -O "/etc/apt/trusted.gpg.d/git-lfs.asc" "https://packagecloud.io/github/git-lfs/gpgkey" \
        && printf "deb [arch=amd64] https://packagecloud.io/github/git-lfs/ubuntu $(sed --quiet 's/VERSION_CODENAME=//p' "/etc/os-release") main\n" \
           > "/etc/apt/sources.list.d/git-lfs.list" \
    && wget -q -O "/etc/apt/trusted.gpg.d/docker.asc" "https://download.docker.com/linux/ubuntu/gpg" \
        && printf "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(sed --quiet 's/VERSION_CODENAME=//p' "/etc/os-release") stable\n" \
           > "/etc/apt/sources.list.d/docker.list" \
    # Firefox Apt over Snap prioritization
    && printf "Package: *\n\
Pin: release o=LP-PPA-mozillateam\n\
Pin-Priority: 1001\n\
Package: firefox\n\
Pin: version 1:1snap*\n\
Pin-Priority: -1\n" > "/etc/apt/preferences.d/mozilla-firefox" \
    # Package installation
    && apt-get -qq update -y \
    && apt-get -qq install -y \
        # Utilities
        bash-completion \
        ca-certificates \
        dbus-x11 \
        gedit \
        gnome-keyring \
        jq \
        nano \
        unzip \
        yad \
        zip \
        # Packages from custom repositories
        firefox libpci3 \
        git git-lfs \
        docker-ce docker-ce-cli containerd.io \
        # Kitty Terminal dependency
        libxcb-xkb1 \
		# Postman dependency
		libnss3 \
        # Node Package Manager
        npm \
    # Yarn Package Manager installation
    && npm install --silent --global yarn \
    # Cleanup
    && apt-get -qq autoremove -y --purge \
    && apt-get -qq clean -y \
    && npm cache clean --silent --force \
    && yarn cache clean --silent \
    && rm -rf "/root/.launchpadlib" \
        "/root/.npm"/* \
        "/tmp"/* \
        "/var/tmp"/* \
        "/var/cache/apt"/* \
        "/var/lib/apt/lists"/* \
        "/usr/local/share/.cache"/* \
        "/var/log"/* \
    && find "/etc/systemd/system" \
        "/lib/systemd/system" \
        -path '*.wants/*' \
        \( -name '*getty*' \
        -or -name '*apt-daily*' \
        -or -name '*systemd-timesyncd*' \
        -or -name '*systemd-logind*' \
        -or -name '*systemd-vconsole-setup*' \
        -or -name '*systemd-readahead*' \
        -or -name '*udev*' \) \
        -exec rm {} \;

COPY --from=all-tools /all-tools /tmp/all-tools/

# User setup
RUN useradd --uid ${UID} --user-group --create-home --comment "Developer" --shell "/bin/bash" "${USER}" \
    && printf "${USER} ALL=(ALL) NOPASSWD: ALL\n" > "/etc/sudoers.d/${USER}" \
    && usermod --home "/nonexistent" ubuntu \
    && rm -rf "/home/ubuntu" \
    # Move configurations from COPY step
    && bash -c "shopt -s dotglob; mv \"/tmp/all-tools/home-config\"/* \"/home/${USER}\"" \
    && mkdir --parents "/home/${USER}/.config/kitty" \
    && mv "/tmp/all-tools/kitty-config"/* "/home/${USER}/.config/kitty" \
    && mv "/tmp/all-tools/docker-config"/* "/etc/docker" \
    && mv "/tmp/all-tools/scripts"/* "/usr/local/bin" \
    && mv "/tmp/all-tools/yad-panel/service"/* "/etc/systemd/system" \
    && mkdir "/etc/yad-panel" \
    && mv "/tmp/all-tools/yad-panel/config"/* "/etc/yad-panel" \
    && chmod 755 "/etc/yad-panel/apps" \
    # Bash completion configuration
    && mv "/tmp/all-tools/cloud-tools/bash_completion.d"/* "/etc/bash_completion.d" \
    && rm -rf "/tmp/all-tools/cloud-tools/bash_completion.d" \
    # Kubectl Krew configuration
    && mv "/tmp/all-tools/cloud-tools/.krew" "/home/${USER}" \
    && ln --symbolic --force "/home/${USER}/.krew/store/krew/$(ls "/home/${USER}/.krew/store/krew")/krew" "/home/${USER}/.krew/bin/kubectl-krew" \
    # Cloud Tools configuration
    && mv "/tmp/all-tools/cloud-tools"/* "/usr/local/bin" \
	# Dev Tools configuration
    && mv "/tmp/all-tools/dev-tools"/* "/opt" \
    && ln --symbolic "/opt/intellij-idea/bin/idea" "/usr/local/bin/idea" \
    && ln --symbolic "/opt/keystore-explorer/kse.sh" "/usr/local/bin/kse" \
    && ln --symbolic "/opt/kitty/bin/kitty" "/usr/local/bin/kitty" \
    && ln --symbolic "/opt/postman/Postman" "/usr/local/bin/postman" \
    # Git configuration
    && git config --global "init.defaultBranch" "main" \
    && mv "/root/.gitconfig" "/home/${USER}" \
    # Docker configuration
    && usermod --append --groups "docker" "${USER}" \
    && docker completion bash > "/etc/bash_completion.d/docker" \
	# Maven configuration
    && mkdir --parents "/home/${USER}/.m2/repository" \
    && cp "/opt/sdkman/candidates/maven/current/conf/settings.xml" "/opt/sdkman/candidates/maven/current/conf/toolchains.xml" \
        "/home/${USER}/.m2" \
    # Create projects directory
    && mkdir "/home/${USER}/projects" \
    # Create .ubuntu-dev-vm.version file
    && printf "${UBUNTU_DEV_VM_VERSION}\n" > "/etc/.ubuntu-dev-vm.version" \
    # Source .environment in .bashrc
    && printf "\nif [ -f ~/.environment ]; then\n\
    . ~/.environment\n\
fi\n" >> "/home/${USER}/.bashrc" \
    # XDG_RUNTIME_DIR setup
    && mkdir "/opt/xdg-runtime-dir-${UID}" \
    # Make sure everything in /home/${USER} belongs to ${USER}
    && chown --recursive "${USER}":"${USER}" "/home/${USER}" \
    # Make sure everything in /opt belongs to ${USER}
    && chown --recursive "${USER}":"${USER}" "/opt"/* \
    && chmod go+w "/opt" \
	# Enable YAD Panel
	&& systemctl enable yad-panel.service > /dev/null \
    # Cleanup
    && rm -rf "/tmp"/* \
		"/var/tmp"/* \
		"/usr/local/share/.cache"/* \
		"/var/log"/* \
    && find "/etc/systemd/system" \
        "/lib/systemd/system" \
        -path '*.wants/*' \
        \( -name '*getty*' \
        -or -name '*apt-daily*' \
        -or -name '*systemd-timesyncd*' \
        -or -name '*systemd-logind*' \
        -or -name '*systemd-vconsole-setup*' \
        -or -name '*systemd-readahead*' \
        -or -name '*udev*' \) \
        -exec rm {} \;

COPY --from=intellij-idea --chown=${USER}:${USER} /intellij-idea /opt/intellij-idea/

WORKDIR /home/${USER}
