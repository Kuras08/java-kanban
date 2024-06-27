package service;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefaultInMemory() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefaultFileBacked(Path file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
