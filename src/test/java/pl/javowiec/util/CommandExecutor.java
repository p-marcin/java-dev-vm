package pl.javowiec.util;

import org.apache.commons.lang3.StringUtils;
import org.testcontainers.containers.ExecConfig;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.javowiec.util.FileProperties.MAVEN;

/**
 * Command Executor is responsible for executing commands in a Docker Container
 * and asserting the results of those commands.
 *
 * <p>It leverages Testcontainers' {@link GenericContainer} to execute commands inside a running container.</p>
 */
public record CommandExecutor(GenericContainer<?> container) {

    public static final String IMAGE_USER = MAVEN.getProperty("image.user");

    public static final String USER_HOME = "/home/" + IMAGE_USER;

    public void assertCommandOutputEquals(String expected, String command) throws IOException, InterruptedException {
        assertThat(getCommandOutput(command)).isEqualTo(expected);
    }

    public String getCommandOutput(String command) throws IOException, InterruptedException {
        GenericContainer.ExecResult executedCommand = container().execInContainer(commandAsImageUser("bash", "-i", "-c", command));
        assertThat(executedCommand.getExitCode()).isZero();
        return executedCommand.getStdout()
                .trim();
    }

    public void assertPathExistsAndContains(String path, String... expected) throws IOException, InterruptedException {
        GenericContainer.ExecResult ls = container().execInContainer(commandAsImageUser("ls", "-a", path));
        assertThat(ls.getExitCode()).isZero();
        assertThat(ls.getStdout()).contains(expected);
    }

    public void assertPathExistsAndDoesNotContain(String path, String... expected) throws IOException, InterruptedException {
        GenericContainer.ExecResult ls = container().execInContainer(commandAsImageUser("ls", "-a", path));
        assertThat(ls.getExitCode()).isZero();
        assertThat(ls.getStdout()).doesNotContain(expected);
    }

    public void assertPathExists(String path) throws IOException, InterruptedException {
        GenericContainer.ExecResult ls = container().execInContainer(commandAsImageUser("ls", "-a", path));
        assertThat(ls.getExitCode()).isZero();
    }

    public void assertExecutablePathEquals(String executable, String expectedPath) throws IOException, InterruptedException {
        GenericContainer.ExecResult command = container().execInContainer(commandAsImageUser("bash", "-i", "-c", "command -v " + executable));
        assertThat(command.getExitCode()).isZero();
        assertThat(command.getStdout()).isEqualToIgnoringNewLines(expectedPath);
    }

    public void assertExecutablePathAndSymLinkEquals(String executable, String expectedPath, String expectedLink) throws IOException, InterruptedException {
        GenericContainer.ExecResult command = container().execInContainer(commandAsImageUser("bash", "-i", "-c", "command -v " + executable));
        assertThat(command.getExitCode()).isZero();
        assertThat(command.getStdout()).isEqualToIgnoringNewLines(expectedPath);
        assertSymLinkEquals(expectedPath, expectedLink);
    }

    public void assertSymLinkEquals(String expectedPath, String expectedLink) throws IOException, InterruptedException {
        GenericContainer.ExecResult readLink = container().execInContainer(commandAsImageUser("readlink", expectedPath));
        assertThat(readLink.getExitCode()).isZero();
        assertThat(readLink.getStdout()).isEqualToIgnoringNewLines(expectedLink);
    }

    public void assertNotSymLink(String expectedPath) throws IOException, InterruptedException {
        GenericContainer.ExecResult readLink = container().execInContainer(commandAsImageUser("readlink", expectedPath));
        assertThat(readLink.getExitCode()).isNotZero();
    }

    public void assertVersionEquals(String mavenProperty, String command) throws IOException, InterruptedException {
        GenericContainer.ExecResult version = container().execInContainer(commandAsImageUser("bash", "-i", "-c", command));
        assertThat(version.getExitCode()).isZero();
        String convertedMavenProperty = Arrays.stream(StringUtils.split(mavenProperty, "&"))
                .map(MAVEN::getProperty)
                .collect(Collectors.joining("-"));
        assertThat(version.getStdout()).isEqualToIgnoringNewLines(convertedMavenProperty);
    }

    public void assertVersionNotEmpty(String command) throws IOException, InterruptedException {
        GenericContainer.ExecResult version = container().execInContainer(commandAsImageUser("bash", "-i", "-c", command));
        assertThat(version.getExitCode()).isZero();
        assertThat(version.getStdout()).isNotEmpty();
    }

    public void assertEnvPropertyEquals(String envProperty, String expected) throws IOException, InterruptedException {
        GenericContainer.ExecResult env = container().execInContainer(commandAsImageUser("bash", "-i", "-c", "echo $" + envProperty));
        assertThat(env.getExitCode()).isZero();
        assertThat(env.getStdout()).isEqualToIgnoringNewLines(expected);
    }

    public void assertFileContains(String path, String... expected) throws IOException, InterruptedException {
        GenericContainer.ExecResult cat = container().execInContainer(commandAsImageUser("cat", path));
        assertThat(cat.getExitCode()).isZero();
        assertThat(cat.getStdout()).contains(expected);
    }

    public void assertFileContains(String path, int count, String content1, String content2) throws IOException, InterruptedException {
        GenericContainer.ExecResult grepFind = container().execInContainer(commandAsImageUser("grep", "-R", content1, path));
        assertThat(grepFind.getExitCode()).isZero();
        assertThat(grepFind.getStdout()).hasLineCount(count);
        GenericContainer.ExecResult grepFindL = container().execInContainer(commandAsImageUser("grep", "-R", content2, path));
        assertThat(grepFindL.getExitCode()).isZero();
        assertThat(grepFindL.getStdout()).hasLineCount(count);
    }

    public static ExecConfig commandAsImageUser(String... command) {
        return ExecConfig.builder()
                .user(IMAGE_USER)
                .command(command)
                .build();
    }

}
