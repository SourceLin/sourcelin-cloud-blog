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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class NacosSharedConfigBootstrapTest {

    private static final List<String> CONFIG_PATHS = Arrays.asList(
        "sourcelin-auth/src/main/resources/application.yml",
        "sourcelin-gateway/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-system/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-blog/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-job/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-file/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-ai/src/main/resources/application.yml",
        "sourcelin-modules/sourcelin-message/src/main/resources/application.yml",
        "sourcelin-visual/sourcelin-monitor/src/main/resources/application.yml"
    );

    @Test
    void sharedApplicationConfigMustBindToBlogGroup() throws IOException {
        Path repoRoot = Paths.get("").toAbsolutePath().normalize().getParent();
        Yaml yaml = new Yaml();

        for (String configPath : CONFIG_PATHS) {
            Path path = repoRoot.resolve(configPath);
            try (InputStream inputStream = Files.newInputStream(path)) {
                Map<String, Object> document = yaml.load(inputStream);
                Map<?, ?> spring = assertInstanceOf(Map.class, document.get("spring"));
                Map<?, ?> config = assertInstanceOf(Map.class, spring.get("config"));
                assertEquals(List.of(
                        "optional:nacos:application-${spring.profiles.active}.yml?group=${spring.cloud.nacos.config.group}&refreshEnabled=true",
                        "optional:nacos:${spring.application.name}-${spring.profiles.active}.yml?group=${spring.cloud.nacos.config.group}&refreshEnabled=true"
                ), config.get("import"), configPath);
                Map<?, ?> cloud = assertInstanceOf(Map.class, spring.get("cloud"));
                Map<?, ?> nacos = assertInstanceOf(Map.class, cloud.get("nacos"));
                Map<?, ?> nacosConfig = assertInstanceOf(Map.class, nacos.get("config"));
                assertEquals("${NACOS_GROUP:BLOG_GROUP}", nacosConfig.get("group"), configPath);
            }
        }
    }
}
