package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();

    protected final Map<Integer, Epic> epics = new HashMap<>();

    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager;

    protected int idCounter;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    private int generateId() {
        return ++idCounter;
    }

    private void removeFromHistory(Collection<Integer> ids) {
        for (Integer id : ids) {
            historyManager.remove(id);
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasksFromManager() {
        removeFromHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        historyManager.add(task);
        return task;
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
    public void removeTaskById(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        historyManager.remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpicsFromManager() {
        removeFromHistory(epics.keySet());
        removeFromHistory(subtasks.keySet());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new IllegalArgumentException("Epic with id " + id + " not found");
        }
        historyManager.add(epic);
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
            throw new IllegalArgumentException("Epic with id " + epic.getId() + " not found");
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);

        if (epic == null) {
            throw new IllegalArgumentException("Epic with id " + id + " not found");
        }
        List<Subtask> listOfEpicSubtasks = epic.getSubtasks();

        for (Subtask subtask : listOfEpicSubtasks) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
        historyManager.remove(id);
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
    public void removeAllSubtasksFromManager() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            calculateEpicStatus(epic);
        }
        removeFromHistory(subtasks.keySet());
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask with id " + id + " not found");
        }
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic with id " + subtask.getEpicId() + " not found");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        calculateEpicStatus(epic);

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic with id " + subtask.getEpicId() + " not found");
        }
        subtasks.put(subtask.getId(), subtask);
        epic.removeSubtask(subtask);
        epic.addSubtask(subtask);
        calculateEpicStatus(epic);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask with id " + id + " not found");
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubtask(subtask);
        calculateEpicStatus(epic);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getAllSubtasksEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new IllegalArgumentException("Epic with id " + id + " not found");
        }
        return epic.getSubtasks();
    }
}
