# syntax=docker/dockerfile:labs
ARG UBUNTU_VERSION=${ubuntu.version}
ARG BUILDKIT_SBOM_SCAN_CONTEXT=${docker.buildx.build.sbom.scan.context}

FROM ubuntu:${UBUNTU_VERSION} AS dependencies
ENV DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes" \
    NO_AT_BRIDGE=1
RUN echo "> Preparation..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends ca-certificates curl git unzip wget xz-utils zip > /dev/null \
        && mkdir "/bash_completion.d" "/binaries" "/dev-tools" "/user-home" "/versions" \
    && echo "> Install Kitty..." \
        && mkdir "/dev-tools/kitty" \
        && wget -q -O "kitty.txz" "https://github.com/kovidgoyal/kitty/releases/download/v${kitty.version}/kitty-${kitty.version}-x86_64.txz" \
        && tar --extract --no-same-owner --directory="/dev-tools/kitty" --file="kitty.txz" "bin" "lib" \
    && echo "> Install DBeaver..." \
        && mkdir "/dev-tools/dbeaver" \
        && wget -q -O "dbeaver.tar.gz" "https://github.com/dbeaver/dbeaver/releases/download/${dbeaver.version}/dbeaver-ce-${dbeaver.version}-linux.gtk.x86_64-nojdk.tar.gz" \
        && tar --extract --gzip --no-same-owner \
            --exclude="dbeaver/dbeaver-ce.desktop" \
            --exclude="dbeaver/readme" \
            --directory="/dev-tools/dbeaver" --strip-components=1 --file="dbeaver.tar.gz" \
    && echo "> Install Postman..." \
        && mkdir "/dev-tools/postman" \
        && wget -q -O "postman.tar.gz" "https://dl.pstmn.io/download/${postman.version}/linux64" \
        && tar --extract --gzip --no-same-owner --exclude="**/Postman" --directory="/dev-tools/postman" --strip-components=2 --file="postman.tar.gz" "Postman/app" \
    && echo "> Install KeyStore Explorer..." \
        && mkdir "/dev-tools/keystore-explorer" \
        && KEYSTORE_EXPLORER_VERSION_NO_DOTS=$(printf "${keystore-explorer.version}" | tr -d ".") \
        && wget -q -O "kse.zip" "https://github.com/kaikramer/keystore-explorer/releases/download/v${keystore-explorer.version}/kse-${KEYSTORE_EXPLORER_VERSION_NO_DOTS}.zip" \
        && unzip -q "kse.zip" -x "*.exe" "*.dll" -d "/tmp" \
        && mv "/tmp/kse-${KEYSTORE_EXPLORER_VERSION_NO_DOTS}"/* "/dev-tools/keystore-explorer" \
    && echo "> Install Git Filter-Repo..." \
        && wget -q -O "git-filter-repo.tar.xz" "https://github.com/newren/git-filter-repo/releases/download/v${git-filter-repo.version}/git-filter-repo-${git-filter-repo.version}.tar.xz" \
        && tar --extract --no-same-owner --directory="/binaries" --strip-components=1 --file="git-filter-repo.tar.xz" "git-filter-repo-${git-filter-repo.version}/git-filter-repo" \
        && chmod g-w,+x "/binaries/git-filter-repo" \
        && printf "${git-filter-repo.version}\n" > "/versions/git-filter-repo.version" \
    && echo "> Install GitHub CLI..." \
        && wget -q -O "github-cli.tar.gz" "https://github.com/cli/cli/releases/download/v${github-cli.version}/gh_${github-cli.version}_linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/binaries" --strip-components=2 --file="github-cli.tar.gz" "gh_${github-cli.version}_linux_amd64/bin/gh" \
        && chmod +x "/binaries/gh" \
        && /binaries/gh completion -s bash > "/bash_completion.d/gh" \
    && echo "> Install Java LTS..." \
        && mkdir --parents "/dev-tools/java/${jdk-lts.version}-${jdk.distribution}" \
        && JDK_LTS_MAJOR_VERSION=$(printf "${jdk-lts.version}" | sed "s/\..*//") \
        && wget -q -O "java-lts.tar.gz" "https://download.oracle.com/java/${JDK_LTS_MAJOR_VERSION}/archive/jdk-${jdk-lts.version}_linux-x64_bin.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/dev-tools/java/${jdk-lts.version}-${jdk.distribution}" --strip-components=1 --file="java-lts.tar.gz" \
    && echo "> Install Java STS..." \
        && mkdir "/dev-tools/java/${jdk-sts.version}-${jdk.distribution}" \
        && JDK_STS_MAJOR_VERSION=$(printf "${jdk-sts.version}" | sed "s/\..*//") \
        && wget -q -O "java-sts.tar.gz" "https://download.oracle.com/java/${JDK_STS_MAJOR_VERSION}/archive/jdk-${jdk-sts.version}_linux-x64_bin.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/dev-tools/java/${jdk-sts.version}-${jdk.distribution}" --strip-components=1 --file="java-sts.tar.gz" \
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
    && echo \"> Install Maven...\" \
        && sdk install \"maven\" \"${mvn.version}\" > /dev/null 2>&1 \
    && echo \"> Install Spring Boot CLI...\" \
        && sdk install \"springboot\" \"${spring-boot-cli.version}\" > /dev/null 2>&1 \
        && sdk flush > /dev/null" \
    && echo "> Install Async Profiler..." \
        && mkdir "/dev-tools/async-profiler" \
        && wget -q -O "async-profiler.tar.gz" "https://github.com/async-profiler/async-profiler/releases/download/v${async-profiler.version}/async-profiler-${async-profiler.version}-linux-x64.tar.gz" \
        && tar --extract --gzip --no-same-owner \
            --directory="/dev-tools/async-profiler" --strip-components=1 --file="async-profiler.tar.gz" \
            "async-profiler-${async-profiler.version}-linux-x64/bin" \
            "async-profiler-${async-profiler.version}-linux-x64/lib" \
    && echo "> Install Kafka..." \
        && mkdir "/dev-tools/kafka" \
        && wget -q -O "kafka.tar.gz" "https://downloads.apache.org/kafka/${kafka.version}/kafka_${scala.version}-${kafka.version}.tgz" \
        && tar --extract --gzip --no-same-owner \
            --exclude="kafka_${scala.version}-${kafka.version}/bin/windows" \
            --directory="/dev-tools/kafka" --strip-components=1 --file="kafka.tar.gz" \
            "kafka_${scala.version}-${kafka.version}/bin" \
            "kafka_${scala.version}-${kafka.version}/config" \
            "kafka_${scala.version}-${kafka.version}/libs" \
    && echo "> Install Node..." \
        && mkdir "/dev-tools/node" \
        && wget -q -O "node.tar.xz" "https://nodejs.org/dist/v${node.version}/node-v${node.version}-linux-x64.tar.xz" \
        && tar --extract --no-same-owner --directory="/dev-tools/node" --strip-components=1 --file="node.tar.xz" \
            "node-v${node.version}-linux-x64/bin" \
            "node-v${node.version}-linux-x64/include" \
            "node-v${node.version}-linux-x64/lib" \
        && export PATH="/dev-tools/node/bin:${PATH}" \
        && npm completion bash > "/bash_completion.d/npm" \
    && echo "> Install Pnpm/Yarn..." \
        && mkdir "/dev-tools/node/.cache" \
        && corepack enable \
        && corepack install --global pnpm@${pnpm.version} yarn@${yarn.version} > /dev/null \
        && mv "/root/.cache/node/corepack" "/dev-tools/node/.cache" \
        && pnpm completion bash > "/bash_completion.d/pnpm" 2> /dev/null \
        && printf "enableTelemetry: 0\n" > "/user-home/.yarnrc.yml" \
    && echo "> Install Gulp CLI..." \
        && npm install --silent --global gulp-cli@${gulp-cli.version} \
        && rm -r "/dev-tools/node/share" \
    && echo "> Install Go..." \
        && mkdir --parents "/dev-tools/go" "/user-home/.config/go/telemetry" \
        && wget -q -O "go.tar.gz" "https://go.dev/dl/go${go.version}.linux-amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/dev-tools/go" --strip-components=1 --file="go.tar.gz" \
        && printf "off\n" > "/user-home/.config/go/telemetry/mode" \
    && echo "> Install Docker Scout..." \
        && mkdir --parents "/docker/cli-plugins" \
        && wget -q -O "docker-scout.tar.gz" "https://github.com/docker/scout-cli/releases/download/v${docker-scout.version}/docker-scout_${docker-scout.version}_linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/docker/cli-plugins" --file="docker-scout.tar.gz" "docker-scout" \
        && chmod +x "/docker/cli-plugins/docker-scout" \
    && echo "> Install Kubectl..." \
        && wget -q -O "/binaries/kubectl" "https://dl.k8s.io/release/v${kubectl.version}/bin/linux/amd64/kubectl" \
        && chmod +x "/binaries/kubectl" \
        && /binaries/kubectl completion bash > "/bash_completion.d/kubectl" \
    && echo "> Install Kubectl Krew..." \
        && mkdir --parents "/dev-tools/krew/store" "/dev-tools/krew/receipts" "/user-home/.krew" \
        && wget -q -O "/tmp/krew.tar.gz" "https://github.com/kubernetes-sigs/krew/releases/download/v${kubectl-krew.version}/krew-linux_amd64.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/tmp" --file="/tmp/krew.tar.gz" \
        && /tmp/krew-linux_amd64 install krew > /dev/null 2>&1 \
        && mv "/root/.krew/store/krew" "/dev-tools/krew/store" \
        && mv "/root/.krew/index" "/dev-tools/krew" \
        && mv "/root/.krew/receipts"/* "/dev-tools/krew/receipts" \
        && mv "/root/.krew"/* "/user-home/.krew" \
    && echo "> Install K3d..." \
        && wget -q -O "/binaries/k3d" "https://github.com/k3d-io/k3d/releases/download/v${k3d.version}/k3d-linux-amd64" \
        && chmod +x "/binaries/k3d" \
        && /binaries/k3d completion bash > "/bash_completion.d/k3d" \
    && echo "> Install Helm..." \
        && wget -q -O "helm.tar.gz" "https://get.helm.sh/helm-v${helm.version}-linux-amd64.tar.gz" \
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
ENV DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes" \
    NO_AT_BRIDGE=1
RUN echo "> Preparation..." \
        && apt-get -qq update -y \
        && apt-get install -y --no-install-recommends ca-certificates wget > /dev/null \
    && echo "> Install IntelliJ IDEA..." \
        && mkdir "/intellij-idea" \
        && wget -q -O "intellij-idea.tar.gz" "https://download.jetbrains.com/idea/ideaI${INTELLIJ_IDEA_EDITION}-${intellij-idea.version}.tar.gz" \
        && tar --extract --gzip --no-same-owner --directory="/intellij-idea" --strip-components=1 --file="intellij-idea.tar.gz" \
        && if [ -d "/intellij-idea/help" ]; then rm -r "/intellij-idea/help"; fi

FROM ubuntu:${UBUNTU_VERSION} AS main
ENV USER=${image.user} \
    DISPLAY=${image.display} \
    DEBIAN_FRONTEND=noninteractive \
    DEBCONF_NOWARNINGS="yes" \
    NO_AT_BRIDGE=1
LABEL org.opencontainers.image.authors="https://github.com/${repo.owner}" \
      org.opencontainers.image.vendor="${repo.owner}" \
      org.opencontainers.image.title="${image.namespace}/${image.name}" \
      org.opencontainers.image.description="Java DEV VM for Java Developers running on Windows in a container (WSL2/Docker Desktop/MobaXterm)" \
      org.opencontainers.image.ref.name="${image.name}" \
      org.opencontainers.image.version="v${image.version}" \
      org.opencontainers.image.source="https://github.com/${repo.owner}/${image.name}" \
      org.opencontainers.image.url="https://hub.docker.com/r/${image.namespace}/${image.name}" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.revision="${git.revision}"
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
            fonts-noto-color-emoji fontconfig \
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
    && echo "> Build font cache..." \
        && fc-cache \
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
        && useradd --uid ${image.user.uid} --user-group --create-home --comment "Developer" --shell "/bin/bash" "${USER}" \
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
    && echo "> Create ${image.name}.version file..." \
        && mkdir "/etc/versions" \
        && printf "${image.version}\n" > "/etc/versions/${image.name}.version" \
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
COPY --from=dependencies                            /docker                         /usr/local/lib/docker/
COPY --from=dependencies                            /bash_completion.d              /etc/bash_completion.d/
COPY --from=dependencies                            /versions                       /etc/versions/
COPY --from=dependencies --chown=${USER}:${USER}    /user-home                      /home/${USER}/
COPY --from=dependencies                            /yad-panel/config               /etc/yad-panel/
COPY --chmod=644                                    home-config/.root_environment   /root/.environment
COPY --chmod=644                                    docker-config                   /etc/docker/
COPY --chmod=644                                    yad-panel/service               /etc/systemd/system/
RUN --mount=type=bind,source=home-config/firefox-user.js,target=/mnt/firefox-user.js \
    echo "> Configure Firefox profile..." \
        && sudo -u ${USER} firefox --headless -CreateProfile ${USER} > /dev/null 2>&1 \
        && cp "/mnt/firefox-user.js" "/home/${USER}/.mozilla/firefox/$(ls "/home/${USER}/.mozilla/firefox" | grep "${USER}")/user.js" \
    && echo "> Enable Yad-Panel..." \
        && systemctl enable yad-panel.service > /dev/null 2>&1 \
    && echo "> Configure Gedit..." \
        && dbus-run-session -- bash -c "gsettings set \"org.gnome.gedit.preferences.editor\" \"insert-spaces\" \"true\" \
            && gsettings set \"org.gnome.gedit.preferences.editor\" \"tabs-size\" \"4\" \
            && gsettings set \"org.gnome.gedit.preferences.editor\" \"scheme\" \"oblivion\" \
            && gsettings set \"org.gnome.gedit.preferences.ui\" \"statusbar-visible\" \"false\"" > /dev/null 2>&1 \
        && cp -r "/root/.config/dconf" "/home/${USER}/.config" \
    && echo "> Configure Kitty..." \
        && ln --symbolic "/opt/kitty/bin/kitten" "/usr/local/bin/kitten" \
        && ln --symbolic "/opt/kitty/bin/kitty" "/usr/local/bin/kitty" \
    && echo "> Configure GUI Apps..." \
        && ln --symbolic "/opt/intellij-idea/bin/idea" "/usr/local/bin/idea" \
        && ln --symbolic "/opt/dbeaver/dbeaver" "/usr/local/bin/dbeaver" \
        && ln --symbolic "/opt/postman/postman" "/usr/local/bin/postman" \
        && ln --symbolic "/opt/keystore-explorer/kse.sh" "/usr/local/bin/kse" \
    && echo "> Configure Git..." \
        && sudo -u ${USER} git config --global "init.defaultBranch" "main" \
    && echo "> Configure Java LTS..." \
        && ln --symbolic "${jdk-lts.version}-${jdk.distribution}" "/opt/java/current" \
    && echo "> Configure SdkMan..." \
        && mkdir --parents "/home/${USER}/.sdkman/candidates" \
        && for dir in "/opt/sdkman"/*; do if [ "$(basename "${dir}")" != "candidates" ]; then ln -s "${dir}" "/home/${USER}/.sdkman/"; fi; done \
        && for dir in "/opt/sdkman/candidates"/*; do ln -s "${dir}" "/home/${USER}/.sdkman/candidates"; done \
    && echo "> Configure Maven..." \
        && mkdir --parents "/home/${USER}/.m2/repository" \
        && cp "/home/${USER}/.sdkman/candidates/maven/current/conf/settings.xml" "/home/${USER}/.sdkman/candidates/maven/current/conf/toolchains.xml" \
            "/home/${USER}/.m2" \
    && echo "> Configure Async Profiler..." \
        && ln --symbolic "/opt/async-profiler/bin/asprof" "/usr/local/bin/asprof" \
        && printf "kernel.perf_event_paranoid=1\nkernel.kptr_restrict=0\n" > "/etc/sysctl.d/999-async-profiler.conf" \
    && echo "> Configure Corepack..." \
        && mkdir --parents "/home/${USER}/.cache/node" \
        && ln --symbolic "/opt/node/.cache/corepack" "/home/${USER}/.cache/node/corepack" \
    && echo "> Install VirtualEnv..." \
        && sudo -u ${USER} pipx install virtualenv==${virtualenv.version} > /dev/null 2>&1 \
        && mkdir "/opt/pipx" \
        && chown "${USER}":"${USER}" "/opt/pipx" \
        && mv "/home/${USER}/.local/share/pipx/venvs/virtualenv" "/opt/pipx" \
        && ln --symbolic "/opt/pipx/virtualenv" "/home/${USER}/.local/share/pipx/venvs/virtualenv" \
    && echo "> Configure Docker..." \
        && usermod --append --groups "docker" "${USER}" \
        && docker completion bash > "/etc/bash_completion.d/docker" \
    && echo "> Configure Kubectl Krew..." \
        && sudo -u ${USER} ln --symbolic "$(ls "/opt/krew/store/krew")" "/opt/krew/store/krew/current" \
        && ln --symbolic "/opt/krew/store/krew" "/home/${USER}/.krew/store/krew" \
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
ARG BUILD_TIMESTAMP
LABEL org.opencontainers.image.created="${BUILD_TIMESTAMP}"
HEALTHCHECK --interval=5s --retries=3 --timeout=10s CMD systemctl status yad-panel | grep "Active: active (running)" || exit 1
CMD [ "/bin/systemd" ]
