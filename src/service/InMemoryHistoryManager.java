package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> browsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (browsingHistory.size() == 10) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
}
