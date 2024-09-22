ARG UBUNTU_VERSION
FROM alpine:latest AS kitty
ARG KITTY_VERSION
RUN wget -q -O "kitty.txz" "https://github.com/kovidgoyal/kitty/releases/download/v${KITTY_VERSION}/kitty-${KITTY_VERSION}-x86_64.txz" \
    && mkdir "/kitty" \
    && tar --extract --no-same-owner --directory="/kitty" --file="kitty.txz" "bin" "lib"

FROM alpine:latest AS intellij-idea
ARG INTELLIJ_IDEA_EDITION
ARG INTELLIJ_IDEA_VERSION
RUN wget -q -O "intellij-idea.tar.gz" "https://download.jetbrains.com/idea/ideaI${INTELLIJ_IDEA_EDITION}-${INTELLIJ_IDEA_VERSION}.tar.gz" \
    && mkdir "/intellij-idea" \
    && tar --extract --gzip --no-same-owner --directory="/intellij-idea" --strip-components=1 --file="intellij-idea.tar.gz" \
    && [ -d "/intellij-idea/help" ] \
    && rm -r "/intellij-idea/help"

FROM alpine:latest AS dbeaver
ARG DBEAVER_VERSION
RUN wget -q -O "dbeaver.tar.gz" "https://github.com/dbeaver/dbeaver/releases/download/${DBEAVER_VERSION}/dbeaver-ce-${DBEAVER_VERSION}-linux.gtk.x86_64-nojdk.tar.gz" \
    && mkdir "/dbeaver" \
    && tar --extract --gzip --no-same-owner \
        --exclude="dbeaver/dbeaver-ce.desktop" \
        --exclude="dbeaver/readme" \
        --directory="/dbeaver" --strip-components=1 --file="dbeaver.tar.gz"

FROM alpine:latest AS postman
ARG POSTMAN_VERSION
RUN wget -q -O "postman.tar.gz" "https://dl.pstmn.io/download/${POSTMAN_VERSION}/linux64" \
    && mkdir "/postman" \
    && tar --extract --gzip --no-same-owner --directory="/postman" --strip-components=2 --file="postman.tar.gz" "Postman/app"

FROM alpine:latest AS keystore-explorer
ARG KEYSTORE_EXPLORER_VERSION
RUN wget -q -O "kse.zip" "https://github.com/kaikramer/keystore-explorer/releases/download/v${KEYSTORE_EXPLORER_VERSION}/kse-${KEYSTORE_EXPLORER_VERSION//./}.zip" \
    && mkdir "/keystore-explorer" \
    && unzip -q "kse.zip" -x "*.exe" -d "/tmp" \
    && mv "/tmp/kse-${KEYSTORE_EXPLORER_VERSION//./}"/* "/keystore-explorer"

FROM dokken/ubuntu-${UBUNTU_VERSION}:main AS sdkman
ARG JAVA_VERSION
ARG MAVEN_VERSION
ARG SPRING_BOOT_VERSION
RUN apt-get -qq update -y \
    && apt-get install -y --no-install-recommends ca-certificates unzip zip > /dev/null \
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
    && sdk install \"springboot\" \"${SPRING_BOOT_VERSION}\" > /dev/null \
    && sdk flush > /dev/null" \
    && sed -i 's/\bfind\b/find -L/g' "/sdkman/bin/sdkman-init.sh" "/sdkman/src/sdkman-upgrade.sh" "/sdkman/src/sdkman-list.sh"

FROM alpine:latest AS kafka
ARG KAFKA_VERSION
ARG SCALA_VERSION
RUN wget -q -O "kafka.tar.gz" "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" \
    && mkdir "/kafka" \
    && tar --extract --gzip --no-same-owner \
        --exclude="kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin/windows" \
        --directory="/kafka" --strip-components=1 --file="kafka.tar.gz" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/config" \
        "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/libs"

FROM dokken/ubuntu-${UBUNTU_VERSION}:main AS node
ARG NODE_VERSION
ARG PNPM_VERSION
ARG YARN_VERSION
ARG GULP_CLI_VERSION
RUN apt-get -qq update -y \
    && apt-get install -y --no-install-recommends xz-utils > /dev/null \
    && wget -q -O "node.tar.xz" "https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x64.tar.xz" \
    && mkdir --parents "/node/.cache" "/bash_completion.d" \
    && tar --extract --no-same-owner --directory="/node" --strip-components=1 --file="node.tar.xz" \
        "node-v${NODE_VERSION}-linux-x64/bin" \
        "node-v${NODE_VERSION}-linux-x64/include" \
        "node-v${NODE_VERSION}-linux-x64/lib" \
    && export PATH="/node/bin:${PATH}" \
    && corepack enable \
    && corepack install --global pnpm@${PNPM_VERSION} yarn@${YARN_VERSION} > /dev/null \
    && mv "/root/.cache/node/corepack" "/node/.cache" \
    && npm install --silent --global gulp-cli@${GULP_CLI_VERSION} \
    && npm completion bash > "/bash_completion.d/npm" \
    && pnpm completion bash > "/bash_completion.d/pnpm" \
    && rm -r "/node/share"

FROM dokken/ubuntu-${UBUNTU_VERSION}:main AS krew
ARG KUBECTL_KREW_VERSION
RUN apt-get -qq update -y \
    && apt-get install -y --no-install-recommends git > /dev/null \
    && mkdir --parents "/krew/store" "/krew/receipts" \
    && wget -q -O "/tmp/krew.tar.gz" "https://github.com/kubernetes-sigs/krew/releases/download/v${KUBECTL_KREW_VERSION}/krew-linux_amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory="/tmp" --file="/tmp/krew.tar.gz" \
    && /tmp/krew-linux_amd64 install krew > /dev/null \
    && mv "/root/.krew/store/krew" "/krew/store" \
    && mv "/root/.krew/index" "/krew" \
    && mv "/root/.krew/receipts"/* "/krew/receipts" \
    && mv "/root/.krew" "/"

FROM scratch AS dev-tools
COPY --from=kitty /kitty /dev-tools/kitty/
COPY --from=dbeaver /dbeaver /dev-tools/dbeaver/
COPY --from=postman /postman /dev-tools/postman/
COPY --from=keystore-explorer /keystore-explorer /dev-tools/keystore-explorer/
COPY --from=sdkman /sdkman /dev-tools/sdkman/
COPY --from=kafka /kafka /dev-tools/kafka/
COPY --from=node /node /dev-tools/node/
COPY --from=node /bash_completion.d /bash_completion.d/
COPY --from=krew /.krew /dev-tools/.krew/
COPY --from=krew /krew /dev-tools/krew/

FROM alpine:latest AS cloud-tools
ARG CTOP_VERSION
ARG KUBECTL_VERSION
ARG K3D_VERSION
ARG HELM_VERSION
ARG K9S_VERSION
RUN apk -q update \
    && apk -q add --no-cache git \
    && mkdir "/cloud-tools" "/bash_completion.d" \
    # Ctop
    && wget -q -O "/cloud-tools/ctop" "https://github.com/bcicen/ctop/releases/download/v${CTOP_VERSION}/ctop-${CTOP_VERSION}-linux-amd64" \
    && chmod +x "/cloud-tools/ctop" \
    # Kubectl
    && wget -q -O "/cloud-tools/kubectl" "https://storage.googleapis.com/kubernetes-release/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
    && chmod +x "/cloud-tools/kubectl" \
    && /cloud-tools/kubectl completion bash > "/bash_completion.d/kubectl" \
    # K3d
    && wget -q -O "/cloud-tools/k3d" "https://github.com/k3d-io/k3d/releases/download/v${K3D_VERSION}/k3d-linux-amd64" \
    && chmod +x "/cloud-tools/k3d" \
    && /cloud-tools/k3d completion bash > "/bash_completion.d/k3d" \
    # Helm
    && wget -q -O "helm.tar.gz" "https://get.helm.sh/helm-v${HELM_VERSION}-linux-amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory="/cloud-tools" --strip-components=1 --file="helm.tar.gz" "linux-amd64/helm" \
    && chmod +x "/cloud-tools/helm" \
    && /cloud-tools/helm completion bash > "/bash_completion.d/helm" \
    # K9s
    && wget -q -O "k9s.tar.gz" "https://github.com/derailed/k9s/releases/download/v${K9S_VERSION}/k9s_Linux_amd64.tar.gz" \
    && tar --extract --gzip --no-same-owner --directory="/cloud-tools" --file="k9s.tar.gz" "k9s" \
    && chmod +x "/cloud-tools/k9s" \
    && /cloud-tools/k9s completion bash > "/bash_completion.d/k9s"

FROM scratch AS all-tools
COPY --chmod=644 docker-config /all-tools/docker-config/
COPY --chmod=644 home-config /all-tools/home-config/
COPY --chmod=644 kitty-config /all-tools/kitty-config/
COPY scripts yad-panel/yad-panel /all-tools/scripts/
COPY --chmod=644 yad-panel/config /all-tools/yad-panel/config/
COPY --chmod=644 yad-panel/service /all-tools/yad-panel/service/
COPY --from=cloud-tools /bash_completion.d /all-tools/bash_completion.d/
COPY --from=cloud-tools /cloud-tools /all-tools/cloud-tools/
COPY --from=dev-tools /bash_completion.d /all-tools/bash_completion.d/
COPY --from=dev-tools /dev-tools /all-tools/dev-tools/

FROM dokken/ubuntu-${UBUNTU_VERSION}:main
ARG UBUNTU_DEV_VM_VERSION
ARG VIRTUALENV_VERSION
ARG UID=1001
ENV USER=${image.user} \
    DISPLAY=${image.display}

# System setup
RUN apt-get -qq update -y \
    && apt-get install -y --no-install-recommends \
        # Custom repositories
        software-properties-common \
        # Utilities
        bash-completion \
        ca-certificates \
        dbus-x11 \
        gedit \
        gnome-keyring \
        jq \
        nano \
        tree \
        unzip \
        xz-utils \
        yad \
        zip \
        # OpenGL dependency
        libgl1 libegl1 \
        # IntelliJ IDEA dependency
        libxtst6 \
        # Kitty Terminal dependency
        libxcb-xkb1 \
        # Postman dependency
        libnss3 \
        # Python
        python3-dev pipx \
        > /dev/null \
    # Custom repositories installation
    && add-apt-repository -y "ppa:mozillateam/ppa" > /dev/null \
    && add-apt-repository -y "ppa:git-core/ppa" > /dev/null \
    && wget -q -O "/etc/apt/trusted.gpg.d/git-lfs.asc" "https://packagecloud.io/github/git-lfs/gpgkey" \
        && printf "deb [arch=amd64] https://packagecloud.io/github/git-lfs/ubuntu $(sed --quiet "s/VERSION_CODENAME=//p" "/etc/os-release") main\n" \
           > "/etc/apt/sources.list.d/git-lfs.list" \
    && wget -q -O "/etc/apt/trusted.gpg.d/docker.asc" "https://download.docker.com/linux/ubuntu/gpg" \
        && printf "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(sed --quiet "s/VERSION_CODENAME=//p" "/etc/os-release") stable\n" \
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
    && apt-get install -y --no-install-recommends \
        # Packages from custom repositories
        firefox libpci3 \
        git git-lfs \
        docker-ce docker-ce-cli containerd.io \
        > /dev/null \
    # User setup
    && useradd --uid ${UID} --user-group --create-home --comment "Developer" --shell "/bin/bash" "${USER}" \
    && printf "${USER} ALL=(ALL) NOPASSWD: ALL\n" > "/etc/sudoers.d/${USER}" \
    && usermod --home "/nonexistent" ubuntu \
    && rm -rf "/home/ubuntu" \
    # Create projects directory
    && mkdir "/home/${USER}/projects" \
    # Source .environment in .bashrc
    && printf "\nif [ -f ~/.environment ]; then\n\
    . ~/.environment\n\
fi\n" >> "/home/${USER}/.bashrc" \
    && printf "\nif [ -f ~/.environment ]; then\n\
    . ~/.environment\n\
fi\n" >> "/root/.bashrc" \
    # Create .ubuntu-dev-vm.version file
    && printf "${UBUNTU_DEV_VM_VERSION}\n" > "/etc/.ubuntu-dev-vm.version" \
    # Cleanup
    && apt-get -qq autoremove -y --purge \
    && apt-get -qq clean -y \
    && rm -rf "/root/.cache" \
        "/root/.dbus" \
        "/root/.launchpadlib" \
        "/root/.wget-hsts" \
        "/tmp"/* \
        "/usr/local/share/.cache"/* \
        "/var/cache"/* \
        "/var/lib/apt/lists"/* \
        "/var/log"/* \
        "/var/tmp"/* \
    && find "/etc/systemd/system" \
        "/lib/systemd/system" \
        -path "*.wants/*" \
        \( -name "*getty*" \
        -or -name "*apt-daily*" \
        -or -name "*systemd-timesyncd*" \
        -or -name "*systemd-logind*" \
        -or -name "*systemd-vconsole-setup*" \
        -or -name "*systemd-readahead*" \
        -or -name "*udev*" \) \
        -exec rm {} +

COPY --from=all-tools /all-tools /tmp/all-tools/

# Move docker-config
RUN mv "/tmp/all-tools/docker-config"/* "/etc/docker" \
    # Move home-config
    && su -c "firefox -CreateProfile ${USER}" "${USER}" \
    && mv "/tmp/all-tools/home-config/firefox-user.js" "/home/${USER}/.mozilla/firefox/$(ls "/home/${USER}/.mozilla/firefox" | grep "${USER}")/user.js" \
    && mv "/tmp/all-tools/home-config/.root_environment" "/root/.environment" \
    && bash -c "shopt -s dotglob; mv \"/tmp/all-tools/home-config\"/* \"/home/${USER}\"" \
    # Move kitty-config
    && mkdir --parents "/home/${USER}/.config/kitty" \
    && mv "/tmp/all-tools/kitty-config"/* "/home/${USER}/.config/kitty" \
    # Move scripts
    && mv "/tmp/all-tools/scripts"/* "/usr/local/bin" \
    # Move yad-panel
    && mkdir "/etc/yad-panel" \
    && mv "/tmp/all-tools/yad-panel/config"/* "/etc/yad-panel" \
    && chmod 755 "/etc/yad-panel/apps" \
    && mv "/tmp/all-tools/yad-panel/service"/* "/etc/systemd/system" \
    && systemctl enable yad-panel.service > /dev/null \
    # Move bash_completion.d
    && mv "/tmp/all-tools/bash_completion.d"/* "/etc/bash_completion.d" \
    # Move dev-tools
    && mv "/tmp/all-tools/dev-tools"/* "/opt" \
    && ln --symbolic "/opt/intellij-idea/bin/idea" "/usr/local/bin/idea" \
    && ln --symbolic "/opt/dbeaver/dbeaver" "/usr/local/bin/dbeaver" \
    && ln --symbolic "/opt/keystore-explorer/kse.sh" "/usr/local/bin/kse" \
    && ln --symbolic "/opt/kitty/bin/kitty" "/usr/local/bin/kitty" \
    && ln --symbolic "/opt/postman/Postman" "/usr/local/bin/postman" \
    # Move sdkman
    && mkdir --parents "/home/${USER}/.sdkman/candidates" \
    && for dir in "/opt/sdkman"/*; do [ "$(basename "${dir}")" != "candidates" ] && ln -s "${dir}" "/home/${USER}/.sdkman/"; done \
    && for dir in "/opt/sdkman/candidates"/*; do ln -s "${dir}" "/home/${USER}/.sdkman/candidates"; done \
    # Move dev-tools/.krew
    && mv "/tmp/all-tools/dev-tools/.krew" "/home/${USER}" \
    && ln --symbolic "/opt/krew/store/krew" "/home/${USER}/.krew/store/krew" \
    && ln --symbolic "$(ls "/opt/krew/store/krew")" "/opt/krew/store/krew/current" \
    && ln --symbolic --force "/home/${USER}/.krew/store/krew/current/krew" "/home/${USER}/.krew/bin/kubectl-krew" \
    && ln --symbolic "/opt/krew/index" "/home/${USER}/.krew/index" \
    && ln --symbolic "/opt/krew/receipts/krew.yaml" "/home/${USER}/.krew/receipts/krew.yaml" \
    # Move cloud-tools
    && mv "/tmp/all-tools/cloud-tools"/* "/usr/local/bin" \
    # Corepack/Yarn configuration
    && mkdir --parents "/home/${USER}/.cache/node" \
    && ln --symbolic "/opt/node/.cache/corepack" "/home/${USER}/.cache/node/corepack" \
    && printf "enableTelemetry: 0" > "/home/${USER}/.yarnrc.yml" \
    # Docker configuration
    && usermod --append --groups "docker" "${USER}" \
    && docker completion bash > "/etc/bash_completion.d/docker" \
    # Gedit configuration
    && gsettings set "org.gnome.gedit.preferences.editor" "insert-spaces" "true" \
    && gsettings set "org.gnome.gedit.preferences.editor" "tabs-size" "4" \
    && gsettings set "org.gnome.gedit.preferences.editor" "scheme" "oblivion" \
    && gsettings set "org.gnome.gedit.preferences.ui" "statusbar-visible" "false" \
    && cp -r "/root/.config/dconf" "/home/${USER}/.config" \
    # Git configuration
    && git config --global "init.defaultBranch" "main" \
    && mv "/root/.gitconfig" "/home/${USER}" \
    # Maven configuration
    && mkdir --parents "/home/${USER}/.m2/repository" \
    && cp "/home/${USER}/.sdkman/candidates/maven/current/conf/settings.xml" "/home/${USER}/.sdkman/candidates/maven/current/conf/toolchains.xml" \
        "/home/${USER}/.m2" \
    # Python configuration
    && su -c "pipx install virtualenv==${VIRTUALENV_VERSION}" "${USER}" \
    && mkdir "/opt/pipx" \
    && mv "/home/${USER}/.local/share/pipx/venvs/virtualenv" "/opt/pipx" \
    && ln --symbolic "/opt/pipx/virtualenv" "/home/${USER}/.local/share/pipx/venvs/virtualenv" \
    # Make sure everything in /home/${USER} belongs to ${USER}
    && chown --recursive "${USER}":"${USER}" "/home/${USER}" \
    # Make sure everything in /opt belongs to ${USER}
    && chown --recursive "${USER}":"${USER}" "/opt"/* \
    && chmod go+w "/opt" \
    # Cleanup
    && rm -rf "/home/${USER}/.cache/pip" \
        "/home/${USER}/.cache/pipx" \
        "/home/${USER}/.local/share/man" \
        "/home/${USER}/.local/state" \
        "/root/.cache" \
        "/root/.dbus" \
        "/tmp"/* \
        "/usr/local/share/.cache"/* \
        "/var/cache"/* \
        "/var/log"/* \
        "/var/tmp"/* \
    && find "/etc/systemd/system" \
        "/lib/systemd/system" \
        -path "*.wants/*" \
        \( -name "*getty*" \
        -or -name "*apt-daily*" \
        -or -name "*systemd-timesyncd*" \
        -or -name "*systemd-logind*" \
        -or -name "*systemd-vconsole-setup*" \
        -or -name "*systemd-readahead*" \
        -or -name "*udev*" \) \
        -exec rm {} +

COPY --from=intellij-idea --chown=${USER}:${USER} /intellij-idea /opt/intellij-idea/

WORKDIR /home/${USER}
