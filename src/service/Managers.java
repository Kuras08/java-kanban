package service;

public class Managers {

    public static TaskManager getDefaultInMemory() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefaultFileBacked() {
        return new FileBackedTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
