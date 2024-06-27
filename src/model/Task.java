package model;

import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private TaskStatus status;
    @Expose
    private LocalDateTime startTime;
    @Expose
    private Duration duration;
    @Expose
    private LocalDateTime endTime;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now().withNano(0);
        this.duration = Duration.ZERO;
        this.endTime = startTime.plus(duration);
    }

    public Task(String name,
                String description,
                TaskStatus status,
                String startTimeStr,
                int durationMinutes) {
        this(name, description, status);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        this.startTime = LocalDateTime.parse(startTimeStr, formatter);
        this.duration = Duration.ofMinutes(durationMinutes);
        this.endTime = startTime.plusMinutes(duration.toMinutes());
    }

    public Task(Integer id,
                String name,
                String description,
                TaskStatus status,
                String startTime,
                int duration) {
        this(name, description, status, startTime, duration);
        this.id = id;
    }

    public Task(Integer id,
                String name,
                String description,
                TaskStatus status,
                LocalDateTime startTime,
                Duration duration) {
        this(name, description, status);
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
        this.id = id;
    }


    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

}
