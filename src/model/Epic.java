package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, LocalDateTime.now(), Duration.ZERO);
    }

    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);

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
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
