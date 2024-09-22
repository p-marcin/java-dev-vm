package pl.javowiec;

import com.github.dockerjava.api.model.Volume;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.javowiec.util.CommandExecutor;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.javowiec.util.CommandExecutor.IMAGE_USER;
import static pl.javowiec.util.CommandExecutor.USER_HOME;
import static pl.javowiec.util.FileProperties.MAVEN;

/**
 * Java DEV VM Tests
 */
@Testcontainers
class JavaDevVmTest {

    private static final String IMAGE_TAG = MAVEN.getProperty("image.namespace")
            + "/" + MAVEN.getProperty("image.name")
            + ":" + MAVEN.getProperty("image.version")
            + "-" + MAVEN.getProperty("image.tag.rc")
            + MAVEN.getProperty("image.tag.edition");

    @Container
    private static final GenericContainer<?> JAVA_DEV_VM = new GenericContainer<>(DockerImageName.parse(IMAGE_TAG))
            .withPrivilegedMode(true)
            .withCreateContainerCmdModifier(cmd -> cmd.withVolumes(new Volume("/var/lib/docker")));

    private final CommandExecutor commandExecutor = new CommandExecutor(JAVA_DEV_VM);

    @Test
    void testRunningAndHealthy() {
        assertThat(JAVA_DEV_VM.isPrivilegedMode()).isTrue();
        assertThat(JAVA_DEV_VM.isRunning()).isTrue();
    }

    @Test
    void testJavaDevVmVersion() throws IOException, InterruptedException {
        commandExecutor.assertVersionEquals("image.version", "cat \"/etc/versions/" + MAVEN.getProperty("image.name") + ".version\"");
    }

    @Test
    void testUbuntu() throws IOException, InterruptedException {
        commandExecutor.assertVersionEquals("ubuntu.version", "grep \"VERSION=\" \"/etc/os-release\" | sed \"s/.*=\\\"//;s/ .*//\"");
    }

    @Test
    void testKitty() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/kitty", "bin", "lib");
        commandExecutor.assertPathExists(USER_HOME + "/.config/kitty/kitty.conf");
        commandExecutor.assertExecutablePathAndSymLinkEquals("kitty", "/usr/local/bin/kitty", "/opt/kitty/bin/kitty");
        commandExecutor.assertExecutablePathAndSymLinkEquals("kitten", "/usr/local/bin/kitten", "/opt/kitty/bin/kitten");
        commandExecutor.assertVersionEquals("kitty.version", "kitty --version | sed \"s/kitty //;s/ .*//\"");
        commandExecutor.assertVersionEquals("kitty.version", "kitten --version | sed \"s/kitten //;s/ .*//\"");
    }

    @Test
    void testFirefox() throws IOException, InterruptedException {
        String firefoxProfile = commandExecutor.getCommandOutput("ls \"" + USER_HOME + "/.mozilla/firefox\" | grep \"" + IMAGE_USER + "\"");
        commandExecutor.assertPathExists(USER_HOME + "/.mozilla/firefox/" + firefoxProfile + "/user.js");
        commandExecutor.assertExecutablePathEquals("firefox", "/usr/bin/firefox");
        commandExecutor.assertVersionNotEmpty("firefox --version | sed \"s/.* //\"");
    }

    @Test
    void testIntellijIdea() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/intellij-idea", "bin");
        commandExecutor.assertPathExistsAndDoesNotContain("/opt/intellij-idea", "help");
        commandExecutor.assertExecutablePathAndSymLinkEquals("idea", "/usr/local/bin/idea", "/opt/intellij-idea/bin/idea");
        commandExecutor.assertVersionEquals("intellij-idea.version", "jq \".version\" \"/opt/intellij-idea/product-info.json\" | tr -d \"\\\"\"");
    }

    @Test
    void testDBeaver() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/dbeaver", "dbeaver", "dbeaver.ini");
        commandExecutor.assertPathExistsAndDoesNotContain("/opt/dbeaver", "dbeaver-ce.desktop", "readme ");
        commandExecutor.assertExecutablePathAndSymLinkEquals("dbeaver", "/usr/local/bin/dbeaver", "/opt/dbeaver/dbeaver");
        commandExecutor.assertVersionEquals("dbeaver.version", "grep \"version=\" \"/opt/dbeaver/.eclipseproduct\" | sed \"s/.*=//\"");
    }

    @Test
    void testPostman() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/postman", "postman");
        commandExecutor.assertPathExistsAndDoesNotContain("/opt/postman", "Postman");
        commandExecutor.assertExecutablePathAndSymLinkEquals("postman", "/usr/local/bin/postman", "/opt/postman/postman");
        commandExecutor.assertVersionNotEmpty("jq \".version\" \"/opt/postman/resources/app/package.json\" | tr -d \"\\\"\"");
    }

    @Test
    void testKeyStoreExplorer() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/keystore-explorer", "kse.sh", "kse.jar");
        commandExecutor.assertPathExistsAndDoesNotContain("/opt/keystore-explorer", "kse.exe", "JavaInfo.dll");
        commandExecutor.assertExecutablePathAndSymLinkEquals("kse", "/usr/local/bin/kse", "/opt/keystore-explorer/kse.sh");
        commandExecutor.assertVersionEquals("keystore-explorer.version",
                "unzip -p \"/opt/keystore-explorer/kse.jar\" \"org/kse/version.properties\" | grep \"KSE.Version\" | sed \"s/.*=//\"");
    }

    @Test
    void testGit() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("git", "/usr/bin/git");
        commandExecutor.assertPathExists("/etc/bash_completion.d/git-prompt");
        commandExecutor.assertVersionNotEmpty("git version | sed \"s/.*version //\"");
    }

    @Test
    void testGitFilterRepo() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/usr/local/bin/git-filter-repo");
        commandExecutor.assertVersionEquals("git-filter-repo.version", "cat /etc/versions/git-filter-repo.version");
    }

    @Test
    void testGitLFS() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("git-lfs", "/usr/bin/git-lfs");
        commandExecutor.assertVersionNotEmpty("git lfs version | sed \"s/.*\\///;s/ (.*//\"");
    }

    @Test
    void testGitHubCLI() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("gh", "/usr/local/bin/gh");
        commandExecutor.assertPathExists("/etc/bash_completion.d/gh");
        commandExecutor.assertVersionEquals("github-cli.version", "gh --version | grep gh | sed \"s/.*version //;s/ (.*//\"");
    }

    @Test
    void testSdkMan() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/sdkman", "bin", "src", "candidates");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.sdkman/bin", "/opt/sdkman/bin");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.sdkman/src", "/opt/sdkman/src");
        commandExecutor.assertNotSymLink(USER_HOME + "/.sdkman/candidates");
        commandExecutor.assertEnvPropertyEquals("SDKMAN_DIR", USER_HOME + "/.sdkman");
        commandExecutor.assertFileContains(USER_HOME + "/.sdkman/etc/config",
                "sdkman_auto_answer=true", "sdkman_auto_env=true", "sdkman_colour_enable=false",
                "sdkman_curl_connect_timeout=10", "sdkman_curl_max_time=120", "sdkman_selfupdate_feature=false");
        commandExecutor.assertFileContains(USER_HOME + "/.sdkman/bin", 1, "\\$(find", "\\$(find -L");
        commandExecutor.assertFileContains(USER_HOME + "/.sdkman/src", 2, "\\$(find", "\\$(find -L");
        commandExecutor.assertVersionNotEmpty("cat " + USER_HOME + "/.sdkman/var/version");
        commandExecutor.assertVersionNotEmpty("sdk version | grep \"script\" | sed \"s/.* //\"");
    }

    @Test
    void testJava() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/sdkman/candidates", "java");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.sdkman/candidates/java", "/opt/sdkman/candidates/java");
        commandExecutor.assertVersionEquals("jdk.version&jdk.distribution", "readlink " + USER_HOME + "/.sdkman/candidates/java/current");
        commandExecutor.assertVersionEquals("jdk.version", "java --version | grep \"java\" | sed \"s/java //;s/ .*//\"");
    }

    @Test
    void testMaven() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/sdkman/candidates", "maven");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.sdkman/candidates/maven", "/opt/sdkman/candidates/maven");
        commandExecutor.assertPathExists(USER_HOME + "/.m2/repository");
        commandExecutor.assertPathExists(USER_HOME + "/.m2/settings.xml");
        commandExecutor.assertPathExists(USER_HOME + "/.m2/toolchains.xml");
        commandExecutor.assertVersionEquals("maven.version", "readlink " + USER_HOME + "/.sdkman/candidates/maven/current");
        commandExecutor.assertVersionEquals("maven.version", "mvn --version | grep \" Maven \" | sed \"s/.* Maven //;s/ .*//\"");
    }

    @Test
    void testSpringBootCLI() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/sdkman/candidates", "springboot");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.sdkman/candidates/springboot", "/opt/sdkman/candidates/springboot");
        commandExecutor.assertVersionEquals("spring-boot-cli.version", "readlink " + USER_HOME + "/.sdkman/candidates/springboot/current");
        commandExecutor.assertVersionEquals("spring-boot-cli.version", "spring --version | sed \"s/.* v//\"");
    }

    @Test
    void testAsyncProfiler() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/async-profiler", "bin", "lib");
        commandExecutor.assertExecutablePathAndSymLinkEquals("asprof", "/usr/local/bin/asprof", "/opt/async-profiler/bin/asprof");
        commandExecutor.assertVersionEquals("async-profiler.version", "asprof --version | sed \"s/.*profiler //;s/ .*//\"");
        commandExecutor.assertPathExists("/etc/sysctl.d/999-async-profiler.conf");
        commandExecutor.assertCommandOutputEquals("kernel.perf_event_paranoid = 1", "sysctl kernel.perf_event_paranoid");
        commandExecutor.assertCommandOutputEquals("kernel.kptr_restrict = 0", "sysctl kernel.kptr_restrict");
    }

    @Test
    void testKafka() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/kafka", "bin", "config", "libs");
        commandExecutor.assertPathExistsAndDoesNotContain("/opt/kafka/bin", "windows");
        commandExecutor.assertExecutablePathEquals("kafka-topics.sh", "/opt/kafka/bin/kafka-topics.sh");
        commandExecutor.assertVersionEquals("kafka.version", "ls \"/opt/kafka/libs\" | grep -m 1 \"kafka-server\" | sed \"s/.*-//;s/.jar//\"");
    }

    @Test
    void testNode() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/node", "bin", "include", "lib");
        commandExecutor.assertExecutablePathEquals("node", "/opt/node/bin/node");
        commandExecutor.assertVersionEquals("node.version", "node --version | sed \"s/v//\"");
    }

    @Test
    void testCorepack() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/node", ".cache");
        commandExecutor.assertSymLinkEquals("/home/dev/.cache/node/corepack", "/opt/node/.cache/corepack");
        commandExecutor.assertExecutablePathEquals("corepack", "/opt/node/bin/corepack");
        commandExecutor.assertVersionNotEmpty("corepack --version");
    }

    @Test
    void testNpm() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("npm", "/opt/node/bin/npm");
        commandExecutor.assertExecutablePathEquals("npx", "/opt/node/bin/npx");
        commandExecutor.assertPathExists("/etc/bash_completion.d/npm");
        commandExecutor.assertVersionNotEmpty("npm --version");
        commandExecutor.assertVersionNotEmpty("npx --version");
    }

    @Test
    void testPnpm() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("pnpm", "/opt/node/bin/pnpm");
        commandExecutor.assertExecutablePathEquals("pnpx", "/opt/node/bin/pnpx");
        commandExecutor.assertPathExists("/etc/bash_completion.d/pnpm");
        commandExecutor.assertVersionEquals("pnpm.version", "pnpm --version");
    }

    @Test
    void testYarn() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("yarn", "/opt/node/bin/yarn");
        commandExecutor.assertExecutablePathEquals("yarnpkg", "/opt/node/bin/yarnpkg");
        commandExecutor.assertFileContains(USER_HOME + "/.yarnrc.yml", "enableTelemetry: 0");
        commandExecutor.assertVersionEquals("yarn.version", "yarn --version");
        commandExecutor.assertVersionEquals("yarn.version", "yarnpkg --version");
    }

    @Test
    void testGulpCLI() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("gulp", "/opt/node/bin/gulp");
        commandExecutor.assertVersionEquals("gulp-cli.version", "gulp --version | grep \"CLI\" | sed \"s/.*: //\"");
    }

    @Test
    void testPython() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/etc/bash_completion.d/global-python-argcomplete");
        commandExecutor.assertExecutablePathEquals("python3", "/usr/bin/python3");
        commandExecutor.assertVersionNotEmpty("python3 --version | sed \"s/.* //\"");
    }

    @Test
    void testPipx() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("pipx", "/usr/bin/pipx");
        commandExecutor.assertVersionNotEmpty("pipx --version");
    }

    @Test
    void testVirtualenv() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/pipx", "virtualenv");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.local/share/pipx/venvs/virtualenv", "/opt/pipx/virtualenv");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.local/bin/virtualenv", USER_HOME + "/.local/share/pipx/venvs/virtualenv/bin/virtualenv");
        commandExecutor.assertExecutablePathEquals("virtualenv", USER_HOME + "/.local/bin/virtualenv");
        commandExecutor.assertVersionEquals("virtualenv.version", "virtualenv --version | sed \"s/virtualenv //;s/ from.*//\"");
    }

    @Test
    void testGo() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/go", "bin");
        commandExecutor.assertEnvPropertyEquals("GOPATH", USER_HOME + "/.go");
        commandExecutor.assertFileContains(USER_HOME + "/.config/go/telemetry/mode", "off");
        commandExecutor.assertExecutablePathEquals("go", "/opt/go/bin/go");
        commandExecutor.assertVersionEquals("go.version", "go version | sed \"s/.* go//;s/ .*//\"");
    }

    @Test
    void testDocker() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/usr/bin/docker");
        commandExecutor.assertExecutablePathEquals("docker", "/usr/bin/docker");
        commandExecutor.assertPathExists("/etc/bash_completion.d/docker");
        commandExecutor.assertVersionNotEmpty("docker version --format \"{{.Client.Version}}\"");
        commandExecutor.assertVersionNotEmpty("docker version --format \"{{.Server.Version}}\"");
        commandExecutor.assertCommandOutputEquals("overlay2", "docker system info --format \"{{.Driver}}\"");
    }

    @Test
    void testDockerBuildx() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/usr/libexec/docker/cli-plugins/docker-buildx");
        commandExecutor.assertVersionNotEmpty("docker buildx version | sed \"s/.* v//;s/ .*//\"");
    }

    @Test
    void testDockerCompose() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/usr/libexec/docker/cli-plugins/docker-compose");
        commandExecutor.assertVersionNotEmpty("docker compose version --short");
    }

    @Test
    void testDockerScout() throws IOException, InterruptedException {
        commandExecutor.assertPathExists("/usr/local/lib/docker/cli-plugins/docker-scout");
        commandExecutor.assertVersionEquals("docker-scout.version", "docker scout version | grep version | sed \"s/.* v//;s/ (.*//\"");
    }

    @Test
    void testKubectl() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("kubectl", "/usr/local/bin/kubectl");
        commandExecutor.assertPathExists("/etc/bash_completion.d/kubectl");
        commandExecutor.assertVersionEquals("kubectl.version", "kubectl version --client | grep \"Client Version:\" | sed \"s/.*v//\"");
    }

    @Test
    void testKubectlKrew() throws IOException, InterruptedException {
        commandExecutor.assertPathExistsAndContains("/opt/krew", "index", "receipts", "store");
        commandExecutor.assertPathExistsAndContains(USER_HOME + "/.krew", "bin", "index", "receipts", "store");
        commandExecutor.assertSymLinkEquals("/opt/krew/store/krew/current", "v" + MAVEN.getProperty("kubectl-krew.version"));
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.krew/store/krew", "/opt/krew/store/krew");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.krew/bin/kubectl-krew", USER_HOME + "/.krew/store/krew/current/krew");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.krew/index", "/opt/krew/index");
        commandExecutor.assertSymLinkEquals(USER_HOME + "/.krew/receipts/krew.yaml", "/opt/krew/receipts/krew.yaml");
        commandExecutor.assertVersionEquals("kubectl-krew.version", "kubectl krew version | grep \"GitTag\" | sed \"s/.*v//\"");
    }

    @Test
    void testK3d() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("k3d", "/usr/local/bin/k3d");
        commandExecutor.assertPathExists("/etc/bash_completion.d/k3d");
        commandExecutor.assertVersionEquals("k3d.version", "k3d version | grep \"k3d\" | sed \"s/.*v//\"");
        commandExecutor.assertVersionNotEmpty("k3d version | grep \"k3s\" | sed \"s/.*v//;s/ (.*//\"");
    }

    @Test
    void testHelm() throws IOException, InterruptedException {
        commandExecutor.assertExecutablePathEquals("helm", "/usr/local/bin/helm");
        commandExecutor.assertPathExists("/etc/bash_completion.d/helm");
        commandExecutor.assertVersionEquals("helm.version", "helm version --template=\"Version: {{.Version}}\" | sed \"s/.*v//\"");
    }

}
