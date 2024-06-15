package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(int epicId, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id,
                   int epicId,
                   String name,
                   String description,
                   TaskStatus status,
                   LocalDateTime startTime,
                   Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicId=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
