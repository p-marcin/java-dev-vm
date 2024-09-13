# syntax=docker/dockerfile:labs
ARG UBUNTU_VERSION=${ubuntu.version}
ARG BUILDKIT_SBOM_SCAN_CONTEXT=true
ARG BUILDKIT_SBOM_SCAN_STAGE=true

FROM ubuntu:${UBUNTU_VERSION} AS dependencies
ARG KITTY_VERSION
ARG DBEAVER_VERSION
ARG POSTMAN_VERSION
ARG KEYSTORE_EXPLORER_VERSION
ARG GIT_FILTER_REPO_VERSION
ARG GITHUB_CLI_VERSION
ARG JAVA_VERSION
ARG MAVEN_VERSION
ARG SPRING_BOOT_CLI_VERSION
ARG KAFKA_VERSION
ARG SCALA_VERSION
ARG NODE_VERSION
ARG PNPM_VERSION
ARG YARN_VERSION
ARG GULP_CLI_VERSION
ARG GO_VERSION
ARG DOCKER_SCOUT_VERSION
ARG KUBECTL_VERSION
ARG KUBECTL_KREW_VERSION
ARG K3D_VERSION
ARG HELM_VERSION
ENV DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes"
RUN echo "> Preparation..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends ca-certificates curl git unzip wget xz-utils zip > /dev/null \
        && mkdir "/bash_completion.d" "/binaries" "/dev-tools" "/user-home" "/versions" \
    && echo "> Install Kitty..." \
        && mkdir "/dev-tools/kitty" \
        && wget -q -O "kitty.txz" "https://github.com/kovidgoyal/kitty/releases/download/v${KITTY_VERSION}/kitty-${KITTY_VERSION}-x86_64.txz" \
        && tar --extract --no-same-owner --directory="/dev-tools/kitty" --file="kitty.txz" "bin" "lib" \
    && echo "> Install DBeaver..." \
        && mkdir "/dev-tools/dbeaver" \
        && wget -q -O "dbeaver.tar.gz" "https://github.com/dbeaver/dbeaver/releases/download/${DBEAVER_VERSION}/dbeaver-ce-${DBEAVER_VERSION}-linux.gtk.x86_64-nojdk.tar.gz" \
        && tar --extract --gzip --no-same-owner \
            --exclude="dbeaver/dbeaver-ce.desktop" \
            --exclude="dbeaver/readme" \
            --directory="/dev-tools/dbeaver" --strip-components=1 --file="dbeaver.tar.gz" \
    && echo "> Install Postman..." \
        && mkdir "/dev-tools/postman" \
        && wget -q -O "postman.tar.gz" "https://dl.pstmn.io/download/${POSTMAN_VERSION}/linux64" \
        && tar --extract --gzip --no-same-owner --directory="/dev-tools/postman" --strip-components=2 --file="postman.tar.gz" "Postman/app" \
    && echo "> Install KeyStore Explorer..." \
        && mkdir "/dev-tools/keystore-explorer" \
        && KEYSTORE_EXPLORER_VERSION_NO_DOTS=$(printf "${KEYSTORE_EXPLORER_VERSION}" | tr -d ".") \
        && wget -q -O "kse.zip" "https://github.com/kaikramer/keystore-explorer/releases/download/v${KEYSTORE_EXPLORER_VERSION}/kse-${KEYSTORE_EXPLORER_VERSION_NO_DOTS}.zip" \
        && unzip -q "kse.zip" -x "*.exe" -d "/tmp" \
        && mv "/tmp/kse-${KEYSTORE_EXPLORER_VERSION_NO_DOTS}"/* "/dev-tools/keystore-explorer" \
    && echo "> Install Git Filter-Repo..." \
        && wget -q -O "git-filter-repo.tar.xz" "https://github.com/newren/git-filter-repo/releases/download/v${GIT_FILTER_REPO_VERSION}/git-filter-repo-${GIT_FILTER_REPO_VERSION}.tar.xz" \
        && tar --extract --no-same-owner --directory="/binaries" --strip-components=1 --file="git-filter-repo.tar.xz" "git-filter-repo-${GIT_FILTER_REPO_VERSION}/git-filter-repo" \
        && chmod g-w,+x "/binaries/git-filter-repo" \
        && printf "${GIT_FILTER_REPO_VERSION}\n" > "/versions/git-filter-repo.version" \
    && echo "> Install GitHub CLI..." \
        && wget -q -O "github-cli.tar.gz" "https://github.com/cli/cli/releases/download/v${GITHUB_CLI_VERSION}/gh_${GITHUB_CLI_VERSION}_linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/binaries" --strip-components=2 --file="github-cli.tar.gz" "gh_${GITHUB_CLI_VERSION}_linux_amd64/bin/gh" \
        && chmod +x "/binaries/gh" \
        && /binaries/gh completion -s bash > "/bash_completion.d/gh" \
    && echo "> Install SdkMan..." \
        && export SDKMAN_DIR="/dev-tools/sdkman" \
        && wget -q -O - "https://get.sdkman.io" | bash > /dev/null 2>&1 \
        && sed --in-place "/sdkman_auto_answer=/c sdkman_auto_answer=true" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place "/sdkman_auto_env=/c sdkman_auto_env=true" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place "/sdkman_colour_enable=/c sdkman_colour_enable=false" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place "/sdkman_curl_connect_timeout=/c sdkman_curl_connect_timeout=10" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place "/sdkman_curl_max_time=/c sdkman_curl_max_time=120" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place "/sdkman_selfupdate_feature=/c sdkman_selfupdate_feature=false" "${SDKMAN_DIR}/etc/config" \
        && sed --in-place 's/\bfind\b/find -L/g' "${SDKMAN_DIR}/bin/sdkman-init.sh" "${SDKMAN_DIR}/src/sdkman-upgrade.sh" "${SDKMAN_DIR}/src/sdkman-list.sh" \
        && bash -c ". \"${SDKMAN_DIR}/bin/sdkman-init.sh\" \
    && echo \"> Install Java...\" \
        && sdk flush > /dev/null 2>&1 \
        && sdk install \"java\" \"${JAVA_VERSION}\" > /dev/null \
    && echo \"> Install Maven...\" \
        && sdk install \"maven\" \"${MAVEN_VERSION}\" > /dev/null \
    && echo \"> Install Spring Boot CLI...\" \
        && sdk install \"springboot\" \"${SPRING_BOOT_CLI_VERSION}\" > /dev/null \
        && sdk flush > /dev/null" \
    && echo "> Install Kafka..." \
        && mkdir "/dev-tools/kafka" \
        && wget -q -O "kafka.tar.gz" "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" \
        && tar --extract --gzip --no-same-owner \
            --exclude="kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin/windows" \
            --directory="/dev-tools/kafka" --strip-components=1 --file="kafka.tar.gz" \
            "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin" \
            "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/config" \
            "kafka_${SCALA_VERSION}-${KAFKA_VERSION}/libs" \
    && echo "> Install Node..." \
        && mkdir --parents "/dev-tools/node/.cache" \
        && wget -q -O "node.tar.xz" "https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x64.tar.xz" \
        && tar --extract --no-same-owner --directory="/dev-tools/node" --strip-components=1 --file="node.tar.xz" \
            "node-v${NODE_VERSION}-linux-x64/bin" \
            "node-v${NODE_VERSION}-linux-x64/include" \
            "node-v${NODE_VERSION}-linux-x64/lib" \
        && export PATH="/dev-tools/node/bin:${PATH}" \
        && npm completion bash > "/bash_completion.d/npm" \
    && echo "> Install Pnpm/Yarn..." \
        && corepack enable \
        && corepack install --global pnpm@${PNPM_VERSION} yarn@${YARN_VERSION} > /dev/null \
        && mv "/root/.cache/node/corepack" "/dev-tools/node/.cache" \
        && pnpm completion bash > "/bash_completion.d/pnpm" 2> /dev/null \
        && printf "enableTelemetry: 0\n" > "/user-home/.yarnrc.yml" \
    && echo "> Install Gulp CLI..." \
        && npm install --silent --global gulp-cli@${GULP_CLI_VERSION} \
        && rm -r "/dev-tools/node/share" \
    && echo "> Install Go..." \
        && mkdir --parents "/go" "/user-home/.config/go/telemetry" \
        && wget -q -O "go.tar.gz" "https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/go" --strip-components=1 --file="go.tar.gz" \
        && printf "off\n" > "/user-home/.config/go/telemetry/mode" \
    && echo "> Install Docker Scout..." \
        && mkdir --parents "/docker/cli-plugins" \
        && wget -q -O "docker-scout.tar.gz" "https://github.com/docker/scout-cli/releases/download/v${DOCKER_SCOUT_VERSION}/docker-scout_${DOCKER_SCOUT_VERSION}_linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/docker/cli-plugins" --file="docker-scout.tar.gz" "docker-scout" \
        && chmod +x "/docker/cli-plugins/docker-scout" \
    && echo "> Install Kubectl..." \
        && wget -q -O "/binaries/kubectl" "https://storage.googleapis.com/kubernetes-release/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
        && chmod +x "/binaries/kubectl" \
        && /binaries/kubectl completion bash > "/bash_completion.d/kubectl" \
    && echo "> Install Kubectl Krew..." \
        && mkdir --parents "/dev-tools/krew/store" "/dev-tools/krew/receipts" "/user-home/.krew" \
        && wget -q -O "/tmp/krew.tar.gz" "https://github.com/kubernetes-sigs/krew/releases/download/v${KUBECTL_KREW_VERSION}/krew-linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/tmp" --file="/tmp/krew.tar.gz" \
        && /tmp/krew-linux_amd64 install krew > /dev/null 2>&1 \
        && mv "/root/.krew/store/krew" "/dev-tools/krew/store" \
        && mv "/root/.krew/index" "/dev-tools/krew" \
        && mv "/root/.krew/receipts"/* "/dev-tools/krew/receipts" \
        && mv "/root/.krew"/* "/user-home/.krew" \
    && echo "> Install K3d..." \
        && wget -q -O "/binaries/k3d" "https://github.com/k3d-io/k3d/releases/download/v${K3D_VERSION}/k3d-linux-amd64" \
        && chmod +x "/binaries/k3d" \
        && /binaries/k3d completion bash > "/bash_completion.d/k3d" \
    && echo "> Install Helm..." \
        && wget -q -O "helm.tar.gz" "https://get.helm.sh/helm-v${HELM_VERSION}-linux-amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/binaries" --strip-components=1 --file="helm.tar.gz" "linux-amd64/helm" \
        && chmod +x "/binaries/helm" \
        && /binaries/helm completion bash > "/bash_completion.d/helm"
COPY                                                                    scripts */yad-panel             /binaries/
COPY --chmod=644 --exclude=.root_environment --exclude=firefox-user.js  home-config                     /user-home/
COPY --chmod=644                                                        kitty-config                    /user-home/.config/kitty/
COPY --chmod=644                                                        yad-panel/config/yad-panel.png  /yad-panel/config/
COPY --chmod=644                                                        yad-panel/config/apps           /yad-panel/config/apps/

FROM ubuntu:${UBUNTU_VERSION} AS intellij-idea
ARG INTELLIJ_IDEA_EDITION
ARG INTELLIJ_IDEA_VERSION
ENV DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes"
RUN echo "> Preparation..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends ca-certificates wget > /dev/null \
    && echo "> Install IntelliJ IDEA..." \
        && mkdir "/intellij-idea" \
        && wget -q -O "intellij-idea.tar.gz" "https://download.jetbrains.com/idea/ideaI${INTELLIJ_IDEA_EDITION}-${INTELLIJ_IDEA_VERSION}.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/intellij-idea" --strip-components=1 --file="intellij-idea.tar.gz" \
        && if [ -d "/intellij-idea/help" ]; then rm -r "/intellij-idea/help"; fi

FROM ubuntu:${UBUNTU_VERSION} AS main
ARG UBUNTU_DEV_VM_VERSION
ARG GIT_REVISION
ARG UID=1001
ENV USER=${image.user} \
    DISPLAY=${image.display} \
    DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes"
LABEL org.opencontainers.image.authors="https://github.com/P-Marcin" \
      org.opencontainers.image.vendor="P-Marcin" \
      org.opencontainers.image.title="${image.namespace}/${project.artifactId}" \
      org.opencontainers.image.description="Ubuntu DEV VM for Java Developers running on Windows in container (WSL2/Docker Desktop/MobaXterm)" \
      org.opencontainers.image.ref.name="${project.artifactId}" \
      org.opencontainers.image.version="v${project.version}" \
      org.opencontainers.image.source="https://github.com/P-Marcin/${project.artifactId}" \
      org.opencontainers.image.url="https://hub.docker.com/r/${image.namespace}/${project.artifactId}" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.revision="${GIT_REVISION}"

RUN echo "> Install packages..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends \
            # Custom repositories
            software-properties-common \
            # System
            apt-utils \
            ca-certificates \
            dbus \
            dbus-x11 \
            cron \
            iproute2 \
            iptables \
            iputils-ping \
            net-tools \
            procps \
            sudo \
            systemd \
            tzdata \
            udev \
            # Utilities
            bash-completion \
            curl \
            gedit \
            gnome-keyring \
            gnupg \
            jq \
            less \
            nano \
            tree \
            unzip \
            vim-tiny \
            wget \
            xz-utils \
            yad \
            zip \
            # OpenGL dependency
            libgl1 libegl1 \
            # Kitty Terminal dependency
            libxcb-xkb1 \
            # IntelliJ IDEA dependency
            libxtst6 \
            # Postman dependency
            libnss3 \
            # Python
            python3-dev pipx \
            > /dev/null \
    && echo "> Install custom repositories..." \
        && wget -q -O "/etc/apt/keyrings/docker.asc" "https://download.docker.com/linux/ubuntu/gpg" \
            && printf "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(sed --quiet "s/VERSION_CODENAME=//p" "/etc/os-release") stable\n" \
               > "/etc/apt/sources.list.d/docker.list" \
        && add-apt-repository -y "ppa:git-core/ppa" > /dev/null \
        && wget -q -O "/etc/apt/keyrings/git-lfs.asc" "https://packagecloud.io/github/git-lfs/gpgkey" \
            && printf "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/git-lfs.asc] https://packagecloud.io/github/git-lfs/ubuntu $(sed --quiet "s/VERSION_CODENAME=//p" "/etc/os-release") main\n" \
               > "/etc/apt/sources.list.d/git-lfs.list" \
        && add-apt-repository -y "ppa:mozillateam/ppa" > /dev/null \
    && echo "> Prioritize Firefox Apt over Snap..." \
        && printf "Package: *\n\
Pin: release o=LP-PPA-mozillateam\n\
Pin-Priority: 1001\n\
Package: firefox\n\
Pin: version 1:1snap*\n\
Pin-Priority: -1\n" > "/etc/apt/preferences.d/mozilla-firefox" \
    && echo "> Install packages from custom repositories..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends \
            docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin \
            git git-lfs \
            firefox libpci3 \
            > /dev/null \
    && echo "> Setup Systemd..." \
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
            -exec rm {} + \
        && systemctl set-default multi-user.target > /dev/null 2>&1 \
        && systemctl mask dev-hugepages.mount sys-fs-fuse-connections.mount network.service > /dev/null 2>&1 \
    && echo "> Setup ${USER} user..." \
        && useradd --uid ${UID} --user-group --create-home --comment "Developer" --shell "/bin/bash" "${USER}" \
        && printf "${USER} ALL=(ALL) NOPASSWD: ALL\n" > "/etc/sudoers.d/${USER}" \
    && echo "> Remove ubuntu user's home directory..." \
        && usermod --home "/nonexistent" ubuntu \
        && rm -r "/home/ubuntu" \
    && echo "> Create projects directory..." \
        && mkdir "/home/${USER}/projects" \
    && echo "> Source .environment in .bashrc..." \
        && printf "\nif [ -f ~/.environment ]; then\n\
    . ~/.environment\n\
fi\n" >> "/home/${USER}/.bashrc" \
        && printf "\nif [ -f ~/.environment ]; then\n\
    . ~/.environment\n\
fi\n" >> "/root/.bashrc" \
    && echo "> Create ${project.artifactId}.version file..." \
        && mkdir "/etc/versions" \
        && printf "${UBUNTU_DEV_VM_VERSION}\n" > "/etc/versions/${project.artifactId}.version" \
    && echo "> Cleanup..." \
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
            "/var/tmp"/*

COPY --from=dependencies --chown=${USER}:${USER}    /dev-tools                      /opt/
COPY --from=dependencies                            /binaries                       /usr/local/bin/
COPY --from=dependencies                            /go                             /usr/local/go/
COPY --from=dependencies                            /docker                         /usr/local/lib/docker/
COPY --from=dependencies                            /bash_completion.d              /etc/bash_completion.d/
COPY --from=dependencies                            /versions                       /etc/versions/
COPY --from=dependencies --chown=${USER}:${USER}    /user-home                      /home/${USER}/
COPY --from=dependencies                            /yad-panel/config               /etc/yad-panel/
COPY --chmod=644                                    home-config/.root_environment   /root/.environment
COPY --chmod=644                                    docker-config                   /etc/docker/
COPY --chmod=644                                    yad-panel/service               /etc/systemd/system/
RUN --mount=type=bind,source=home-config/firefox-user.js,target=/tmp/firefox-user.js \
    echo "> Configure Firefox profile..." \
        && sudo -u ${USER} firefox --headless -CreateProfile ${USER} > /dev/null 2>&1 \
        && cp "/tmp/firefox-user.js" "/home/${USER}/.mozilla/firefox/$(ls "/home/${USER}/.mozilla/firefox" | grep "${USER}")/user.js"

ARG VIRTUALENV_VERSION
RUN echo "> Enable Yad-Panel..." \
        && systemctl enable yad-panel.service > /dev/null 2>&1 \
    && echo "> Configure Gedit..." \
        && dbus-run-session -- bash -c "gsettings set \"org.gnome.gedit.preferences.editor\" \"insert-spaces\" \"true\" \
            && gsettings set \"org.gnome.gedit.preferences.editor\" \"tabs-size\" \"4\" \
            && gsettings set \"org.gnome.gedit.preferences.editor\" \"scheme\" \"oblivion\" \
            && gsettings set \"org.gnome.gedit.preferences.ui\" \"statusbar-visible\" \"false\"" > /dev/null 2>&1 \
        && cp -r "/root/.config/dconf" "/home/${USER}/.config" \
    && echo "> Configure Kitty..." \
        && ln --symbolic "/opt/kitty/bin/kitty" "/usr/local/bin/kitty" \
    && echo "> Configure GUI Apps..." \
        && ln --symbolic "/opt/intellij-idea/bin/idea" "/usr/local/bin/idea" \
        && ln --symbolic "/opt/dbeaver/dbeaver" "/usr/local/bin/dbeaver" \
        && ln --symbolic "/opt/postman/Postman" "/usr/local/bin/postman" \
        && ln --symbolic "/opt/keystore-explorer/kse.sh" "/usr/local/bin/kse" \
    && echo "> Configure Git..." \
        && sudo -u ${USER} git config --global "init.defaultBranch" "main" \
    && echo "> Configure SdkMan..." \
        && mkdir --parents "/home/${USER}/.sdkman/candidates" \
        && for dir in "/opt/sdkman"/*; do if [ "$(basename "${dir}")" != "candidates" ]; then ln -s "${dir}" "/home/${USER}/.sdkman/"; fi; done \
        && for dir in "/opt/sdkman/candidates"/*; do ln -s "${dir}" "/home/${USER}/.sdkman/candidates"; done \
    && echo "> Configure Maven..." \
        && mkdir --parents "/home/${USER}/.m2/repository" \
        && cp "/home/${USER}/.sdkman/candidates/maven/current/conf/settings.xml" "/home/${USER}/.sdkman/candidates/maven/current/conf/toolchains.xml" \
            "/home/${USER}/.m2" \
    && echo "> Configure Corepack..." \
        && mkdir --parents "/home/${USER}/.cache/node" \
    && echo "> Configure Yarn..." \
        && ln --symbolic "/opt/node/.cache/corepack" "/home/${USER}/.cache/node/corepack" \
    && echo "> Install VirtualEnv..." \
        && sudo -u ${USER} pipx install virtualenv==${VIRTUALENV_VERSION} > /dev/null 2>&1 \
        && mkdir "/opt/pipx" \
        && chown "${USER}":"${USER}" "/opt/pipx" \
        && mv "/home/${USER}/.local/share/pipx/venvs/virtualenv" "/opt/pipx" \
        && ln --symbolic "/opt/pipx/virtualenv" "/home/${USER}/.local/share/pipx/venvs/virtualenv" \
    && echo "> Configure Docker..." \
        && usermod --append --groups "docker" "${USER}" \
        && docker completion bash > "/etc/bash_completion.d/docker" \
    && echo "> Configure Kubectl Krew..." \
        && ln --symbolic "/opt/krew/store/krew" "/home/${USER}/.krew/store/krew" \
        && sudo -u ${USER} ln --symbolic "$(ls "/opt/krew/store/krew")" "/opt/krew/store/krew/current" \
        && ln --symbolic --force "/home/${USER}/.krew/store/krew/current/krew" "/home/${USER}/.krew/bin/kubectl-krew" \
        && ln --symbolic "/opt/krew/index" "/home/${USER}/.krew/index" \
        && ln --symbolic "/opt/krew/receipts/krew.yaml" "/home/${USER}/.krew/receipts/krew.yaml" \
    && echo "> Make sure everything in /home/${USER} belongs to ${USER} user..." \
        && chown --recursive "${USER}":"${USER}" "/home/${USER}" \
    && echo "> Grant write permission on /opt to ${USER} user..." \
        && chmod go+w "/opt" \
    && echo "> Cleanup..." \
        && find "/home/${USER}/.cache" -mindepth 1 -maxdepth 1 -not -name "node" -exec rm -r {} + \
        && rm -rf "/home/${USER}/.dbus" \
            "/home/${USER}/.local/share/man" \
            "/home/${USER}/.local/state" \
            "/root/.cache" \
            "/root/.dbus" \
            "/tmp"/* \
            "/usr/local/share/.cache"/* \
            "/var/cache"/* \
            "/var/log"/* \
            "/var/tmp"/*

COPY --from=intellij-idea --chown=${USER}:${USER} /intellij-idea /opt/intellij-idea/

WORKDIR /home/${USER}

ARG BUILD_TIMESTAMP
LABEL org.opencontainers.image.created="${BUILD_TIMESTAMP}"

CMD [ "/bin/systemd" ]
