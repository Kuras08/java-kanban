package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
    }

    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
