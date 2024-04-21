package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> browsingHistory = new LinkedList<>();


    @Override
    public void add(Task task) {
        if (browsingHistory.size() == MAX_HISTORY_SIZE) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
}
