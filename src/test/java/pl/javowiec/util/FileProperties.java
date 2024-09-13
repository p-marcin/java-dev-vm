package pl.javowiec.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Enum representing various file properties configurations.
 *
 * <p>Each constant in this enum corresponds to a specific properties file
 * that can be loaded and queried for property values.</p>
 *
 * <p>The enum lazily loads the properties file when the {@code getProperty} method is called.
 * If a system property with the same name exists, it overrides the value from the properties file.</p>
 */
public enum FileProperties {

    /** Properties from maven.properties file generated with Maven execution id="generate-maven-properties" */
    MAVEN("maven.properties");

    private final String fileName;

    private Properties properties;

    FileProperties(String fileName) {
        this.fileName = fileName;
    }

    public String getProperty(String propertyName) {
        if (properties == null) {
            initProperties();
        }
        String systemProperty = System.getProperty(propertyName);
        if (StringUtils.isNotEmpty(systemProperty)) {
            return systemProperty;
        }
        return this.properties.getProperty(propertyName);
    }

    private void initProperties() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(fileName)) {
            this.properties = new Properties();
            this.properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
