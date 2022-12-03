package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

public class FileParser {
    /**
     * Parse a JSON file and return a list of persons
     *
     * @param path the path to the JSON file
     * @return a list of persons
     * @throws IOException if the file is not found
     */
    public static ArrayList<Person> parsePersons(@NotNull String path) throws IOException {
        Gson gson = new Gson();
        try (FileReader rf = new FileReader(path, StandardCharsets.UTF_8)) {
            Email[] emails = gson.fromJson(rf, Email[].class);
            ArrayList<Person> persons = new ArrayList<Person>(emails.length);
            for (Email e : emails) persons.add(new Person(e));
            return persons;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a JSON file and return a list of messages
     *
     * @param path the path to the JSON file
     * @return a list of messages
     */
    public static ArrayList<Message> parseMessages(@NotNull String path) {
        Gson gson = new Gson();
        try (FileReader rf = new FileReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(rf, new TypeToken<ArrayList<Message>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a properties file and return its content
     *
     * @param f the properties file TODO: CHECK IF STRING IS BETTER THAN FILE
     * @return a list of groups
     * @throws IOException if the file is not found
     */
    public static Properties parseConfig(@NotNull String f) throws IOException, RuntimeException {
        try (FileInputStream content = new FileInputStream(f)) {
            Properties prop = new Properties();
            prop.load(content);
            return prop;
        } catch (IOException e) {
            throw new IOException("Error while reading file");
        }
    }
}