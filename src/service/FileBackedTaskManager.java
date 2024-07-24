package service;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path file;

    public FileBackedTaskManager(HistoryManager historyManager, Path file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        final FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        int maxId = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                final Task task = manager.fromString(line);
                final Integer id = task.getId();
                final TaskType type = task.getType();

                switch (type) {
                    case TASK -> {
                        manager.checkTaskTime(task);
                        manager.tasks.put(id, task);
                        manager.prioritizedTasks.add(task);
                    }
                    case EPIC -> manager.epics.put(id, (Epic) task);
                    case SUBTASK -> {
                        manager.checkTaskTime(task);
                        manager.subtasks.put(id, (Subtask) task);
                        manager.prioritizedTasks.add(task);
                        final Epic epic = manager.epics.get(task.getEpicId());
                        epic.addSubtask((Subtask) task);
                        manager.calculateEpicTime(epic);
                    }
                }
                maxId = Math.max(maxId, id);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Error reading file!");
        }
        manager.idCounter = maxId;
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public void removeAllTasksFromManager() {
        super.removeAllTasksFromManager();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllEpicsFromManager() {
        super.removeAllEpicsFromManager();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllSubtasksFromManager() {
        super.removeAllSubtasksFromManager();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            writer.write("id,type,name,status,description,epic,duration,startTime\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");

                for (Subtask subtask : getAllSubtasksEpic(epic.getId())) {
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing file!");
        }
    }

    private Task fromString(String value) {
        final String[] parts = value.split(",");
        final Integer id = Integer.parseInt(parts[0]);
        final TaskType type = TaskType.valueOf(parts[1]);
        final String name = parts[2];
        final TaskStatus status = TaskStatus.valueOf(parts[3]);
        final String description = parts[4];
        final Duration duration = Duration.parse(parts[6]);
        final LocalDateTime startTime = LocalDateTime.parse(parts[7]);

        return switch (type) {
            case TASK -> new Task(id, name, description, status, startTime, duration);
            case EPIC -> new Epic(id, name, description, status, startTime, duration);
            case SUBTASK -> {
                final int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(id, epicId, name, description, status, startTime, duration);
            }
        };
    }

    private String toString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicId() + "," +
                task.getDuration() + "," +
                task.getStartTime();
    }
}
