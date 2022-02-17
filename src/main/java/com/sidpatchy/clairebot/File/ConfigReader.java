package com.sidpatchy.clairebot.File;

import com.sidpatchy.clairebot.Main;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigReader {

    Yaml yaml = new Yaml();

    public Map<String, Object> GetConfig(String file) {
        try {
            InputStream inputStream = new FileInputStream(new File("config/" + file));
            return yaml.load(inputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.getLogger().error("Unable to read from file " + file + ". More errors will follow.");
            return null;
        }
    }

    public boolean getBool(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return (boolean) config.get(parameter);
    }

    public String getString(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return (String) config.get(parameter);
    }

    public Integer getInt(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return (Integer) config.get(parameter);
    }

    public Float getFloat(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return (Float) config.get(parameter);
    }

    public long getLong(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return (long) config.get(parameter);
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String file, String parameter) {
        return (List<String>) GetConfig(file).get(parameter);
    }

    public Object getObj(String file, String parameter) {
        Map<String, Object> config = GetConfig(file);
        return config.get(parameter);
    }
}
