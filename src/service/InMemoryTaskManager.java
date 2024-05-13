package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();

    private final Map<Integer, Epic> epics = new HashMap<>();

    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager;

    private int idCounter;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    private int generateId() {
        return ++idCounter;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());

        if (savedEpic == null) {
            return;
        }
        savedEpic.setTitle(epic.getTitle());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        Epic epic = epics.remove(id);

        if (epic == null) {
            return;
        }
        List<Subtask> listOfEpicSubtasks = epic.getSubtasks();

        for (Subtask subtask : listOfEpicSubtasks) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
    }

    private void calculateEpicStatus(Epic epic) {
        boolean hasDifferentStatuses = false;
        TaskStatus commonStatus = null;
        List<Subtask> listOfEpicSubtasks = epic.getSubtasks();

        if (listOfEpicSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        for (Subtask subtask : listOfEpicSubtasks) {
            if (commonStatus == null) {
                commonStatus = subtask.getStatus();
            } else if (!subtask.getStatus().equals(commonStatus)) {
                hasDifferentStatuses = true;
                break;
            }
        }
        if (hasDifferentStatuses) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(commonStatus);
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            calculateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            calculateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        epic.deleteSubtask(subtask);
        epic.addSubtask(subtask);
        calculateEpicStatus(epic);
    }

    @Override
    public void deleteSubtaskById(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.remove(id);

        if (subtask == null) {
            return;
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtask(subtask);
        calculateEpicStatus(epic);
    }

    @Override
    public List<Subtask> getAllSubtasksEpic(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            return epic.getSubtasks();
        } else {
            return new ArrayList<>();
        }
    }
}
