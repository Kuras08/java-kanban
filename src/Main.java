import exceptions.ManagerLoadException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {

        File file = new File("resources", "task_manager_data.csv");
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerLoadException("kkkkkk");
        }

    }
}
