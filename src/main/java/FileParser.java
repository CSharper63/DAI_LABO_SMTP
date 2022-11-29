import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;


public class FileParser {
    public static ArrayList<Group> parseGroup(@NotNull String filename) throws IOException {
        Gson gson = new Gson();
        try {
            Email[] emails = gson.fromJson(new FileReader(filename, StandardCharsets.UTF_8), Email[].class);

            ArrayList<Group> groups = new ArrayList<Group>();
            ArrayList<Person> p = new ArrayList<Person>();


            for (Email e : emails) { // add email to each person
                p.add(new Person(e));
            }

            groups.add(new Group(p.get(0), p));

            return groups;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static Message[] parseMessage(String filename) {

        Gson gson = new Gson();
        try {

            return gson.fromJson(new FileReader(filename, StandardCharsets.UTF_8), Message[].class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static Properties parseConfig(@NotNull File f) throws IOException, RuntimeException {
        if (!f.exists()) throw new RuntimeException("File does not exist");
        try (FileInputStream content = new FileInputStream(f)
        ) {
            Properties prop = new Properties();
            prop.load(content);
            return prop;
        } catch (IOException e) {
            throw new IOException("Error while reading file");
        }
    }
}
