package model;

import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    @Expose
    private final Integer epicId;

    public Subtask(Integer epicId,
                   String name,
                   String description,
                   TaskStatus status,
                   String startTime,
                   int duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id,
                   Integer epicId,
                   String name,
                   String description,
                   TaskStatus status,
                   String startTime,
                   int duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id,
                   Integer epicId,
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
