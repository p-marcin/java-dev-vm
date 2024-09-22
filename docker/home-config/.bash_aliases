# UBUNTU DEV VM
alias bashrc="(gedit ${HOME}/.bashrc &>/dev/null &)"
alias bashaliases="(gedit ${HOME}/.bash_aliases &>/dev/null &)"
alias clear="tput reset"
alias environment="(gedit ${HOME}/.environment &>/dev/null &)"
alias hosts="cat /etc/hosts"
alias largestDir='sudo du -hs --threshold=50M ?(.)[!.]* | sort -hr | head -n 20'
alias largestFile='sudo find . -type f -size +50M -exec du -h '{}' + | sort -hr | head -n 20'
alias src="cd ${HOME}/projects"
alias testInternet="curl --head google.com"

# MAVEN
alias m2="(gedit ${HOME}/.m2/settings.xml &>/dev/null &)"
alias mvnBuildClasspath="mvn dependency:build-classpath"
alias mvnDependencyTree="mvn dependency:tree"
alias mvnEffectivePom="mvn help:effective-pom"
alias mvnSourcesJavadoc="mvn dependency:sources dependency:resolve -Dclassifier=javadoc"

# DOCKER
alias d="docker"
complete -F __start_docker d
alias dls="docker container ls -a --format 'table {{.ID}}\t{{.Image}}\t{{.CreatedAt}}\t{{.Status}}\t{{.Names}}'"
alias dlsize="docker container ls -a --format 'table {{.ID}}\t{{.Image}}\t{{.CreatedAt}}\t{{.Status}}\t{{.Names}}\t{{.Size}}'"
alias dils="docker image ls -a"
alias dnls="docker network ls"
alias dvls="docker volume ls"
alias fixDockerDanglingImages="docker image prune -f"

# KUBERNETES
alias k="kubectl"
complete -F __start_kubectl k
alias h="helm"
complete -F __start_helm h
alias k3s-default="k3d cluster create --image \"rancher/k3s:v$(kubectl version --client | grep "Client Version:" | sed "s/.*v//")-k3s1\" --api-port 6443 --agents 1 -p \"80:80@loadbalancer\" -p \"443:443@loadbalancer\""
alias k3s-default-no-traefik="k3d cluster create --image \"rancher/k3s:v$(kubectl version --client | grep "Client Version:" | sed "s/.*v//")-k3s1\" --api-port 6443 --agents 1 -p \"80:80@loadbalancer\" -p \"443:443@loadbalancer\" --k3s-arg \"--disable=traefik@server:0\""
alias k3s-default-agent-0-images="docker exec k3d-k3s-default-agent-0 crictl images"
alias k3s-default-server-0-images="docker exec k3d-k3s-default-server-0 crictl images"
alias kcl="k3d cluster list"
