import server.HttpTaskServer;
import service.Managers;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Path file = Path.of("resources/task_manager_data.csv");
        HttpTaskServer server = new HttpTaskServer(Managers.getDefaultFileBacked(file));
        server.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to stop the server...");
        scanner.nextLine();

        server.stop();
        scanner.close();
    }
}
