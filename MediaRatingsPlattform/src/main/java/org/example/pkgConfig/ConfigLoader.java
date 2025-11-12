package org.example.pkgConfig;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {
    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml();
        var config = Files.newInputStream(Paths.get("config.yml"));
        var data = yaml.load(config);
        System.out.println("Loaded YAML: " + data);
    }
}
