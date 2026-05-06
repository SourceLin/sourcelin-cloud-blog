package com.sourcelin.gateway.config;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class NacosSharedConfigBootstrapTest {

    private static final List<String> BOOTSTRAP_PATHS = Arrays.asList(
        "sourcelin-auth/src/main/resources/bootstrap.yml",
        "sourcelin-gateway/src/main/resources/bootstrap.yml",
        "sourcelin-modules/sourcelin-system/src/main/resources/bootstrap.yml",
        "sourcelin-modules/sourcelin-blog/src/main/resources/bootstrap.yml",
        "sourcelin-modules/sourcelin-job/src/main/resources/bootstrap.yml",
        "sourcelin-modules/sourcelin-file/src/main/resources/bootstrap.yml"
    );

    @Test
    void sharedApplicationConfigMustBindToBlogGroup() throws IOException {
        Path repoRoot = Paths.get("").toAbsolutePath().normalize().getParent();
        Yaml yaml = new Yaml();

        for (String bootstrapPath : BOOTSTRAP_PATHS) {
            Path path = repoRoot.resolve(bootstrapPath);
            try (InputStream inputStream = Files.newInputStream(path)) {
                Map<String, Object> document = yaml.load(inputStream);
                List<?> sharedConfigs = readSharedConfigs(document);

                assertFalse(sharedConfigs.isEmpty(), bootstrapPath + " must declare shared-configs");

                Object firstEntry = sharedConfigs.get(0);
                Map<?, ?> sharedConfig = assertInstanceOf(Map.class, firstEntry,
                    bootstrapPath + " must use map-style shared-configs so group is explicit");

                assertEquals("application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}",
                    sharedConfig.get("data-id"),
                    bootstrapPath + " must load the shared application config");
                assertEquals("${spring.cloud.nacos.config.group}",
                    sharedConfig.get("group"),
                    bootstrapPath + " must read the shared config from BLOG_GROUP instead of DEFAULT_GROUP");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<?> readSharedConfigs(Map<String, Object> document) {
        Map<String, Object> spring = (Map<String, Object>) document.get("spring");
        Map<String, Object> cloud = (Map<String, Object>) spring.get("cloud");
        Map<String, Object> nacos = (Map<String, Object>) cloud.get("nacos");
        Map<String, Object> config = (Map<String, Object>) nacos.get("config");
        return (List<?>) config.get("shared-configs");
    }
}
