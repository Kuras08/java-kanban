package service;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path file;

    public FileBackedTaskManager(HistoryManager historyManager, Path file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.file = Paths.get("resources/task_manager_data.csv");
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        int maxId = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = manager.fromString(line);
                int id = task.getId();
                TaskType type = task.getType();

                switch (type) {
                    case TASK -> manager.tasks.put(id, task);
                    case EPIC -> manager.epics.put(id, (Epic) task);
                    case SUBTASK -> {
                        manager.subtasks.put(id, (Subtask) task);
                        Epic epic = manager.getEpicById(task.getEpicId());
                        epic.addSubtask((Subtask) task);
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

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {

            bw.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                bw.write(toString(task).replace(",null", "") + "\n");
            }
            for (Epic epic : getAllEpics()) {
                bw.write(toString(epic).replace(",null", "") + "\n");
                for (Subtask subtask : getAllSubtasksEpic(epic.getId())) {
                    bw.write(toString(subtask) + "\n");
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error writing file!");
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        return switch (type) {
            case TASK -> new Task(name, description, status, id);
            case EPIC -> new Epic(name, description, status, id);
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(name, description, status, id, epicId);
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
    }

    private String toString(Task task) {
        return task.getId() + "," +
                task.getType().toString() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicId();
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
    public void removeTaskById(int id) {
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
    public void removeEpicById(int id) {
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
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}
