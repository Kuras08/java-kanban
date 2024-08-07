package service;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();

    protected final Map<Integer, Epic> epics = new HashMap<>();

    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager;

    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int idCounter;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasksFromManager() {
        removeFromHistory(tasks.keySet());
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        final Task task = Optional.ofNullable(tasks.get(id))
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " not found"));

        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        checkTaskTime(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        final Task savedTask = Optional.ofNullable(tasks.get(task.getId()))
                .orElseThrow(() -> new NotFoundException("Task with id " + task.getId() + " not found"));

        checkTaskTime(task);
        prioritizedTasks.remove(savedTask);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void removeTaskById(Integer id) {
        final Task task = Optional.ofNullable(tasks.remove(id))
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " not found"));

        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpicsFromManager() {
        Stream.of(epics.keySet(), subtasks.keySet()).forEach(this::removeFromHistory);
        Stream.of(epics, subtasks).forEach(Map::clear);
    }

    @Override
    public Epic getEpicById(Integer id) {
        final Epic epic = Optional.ofNullable(epics.get(id))
                .orElseThrow(() -> new NotFoundException("Epic with id " + id + " not found"));

        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = Optional.ofNullable(epics.get(epic.getId()))
                .orElseThrow(() -> new NotFoundException("Epic with id " + epic.getId() + " not found"));

        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void removeEpicById(Integer id) {
        final Epic epic = Optional.ofNullable(epics.remove(id))
                .orElseThrow(() -> new NotFoundException("Epic with id " + id + " not found"));

        epic.getSubtasks().stream()
                .map(Subtask::getId)
                .forEach(subtaskId -> {
                    historyManager.remove(subtaskId);
                    subtasks.remove(subtaskId);
                });
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasksFromManager() {
        epics.values().forEach(epic -> {
            epic.removeAllSubtasks();
            calculateEpicStatus(epic);
            calculateEpicTime(epic);
        });
        removeFromHistory(subtasks.keySet());
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        final Subtask subtask = Optional.ofNullable(subtasks.get(id))
                .orElseThrow(() -> new NotFoundException("Subtask with id " + id + " not found"));

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        final Epic epic = Optional.ofNullable(epics.get(subtask.getEpicId()))
                .orElseThrow(() -> new NotFoundException("Epic with id " + subtask.getEpicId() + " not found"));

        checkTaskTime(subtask);
        subtask.setId(generateId());

        epic.addSubtask(subtask);
        calculateEpicStatus(epic);
        calculateEpicTime(epic);

        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Epic savedEpic = Optional.ofNullable(epics.get(subtask.getEpicId()))
                .orElseThrow(() -> new NotFoundException("Epic with id " + subtask.getEpicId() + " not found"));

        final Subtask savedSubtask = Optional.ofNullable(subtasks.get(subtask.getId()))
                .orElseThrow(() -> new NotFoundException("Subtask with id " + subtask.getId() + " not found"));

        checkTaskTime(subtask);
        prioritizedTasks.remove(savedSubtask);

        savedEpic.removeSubtask(savedSubtask);
        savedEpic.addSubtask(subtask);

        calculateEpicStatus(savedEpic);
        calculateEpicTime(savedEpic);

        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        final Subtask subtask = Optional.ofNullable(subtasks.remove(id))
                .orElseThrow(() -> new NotFoundException("Subtask with id " + id + " not found"));

        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubtask(subtask);
        calculateEpicStatus(epic);
        calculateEpicTime(epic);

        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
    }

    @Override
    public List<Subtask> getAllSubtasksEpic(Integer id) {
        final Epic epic = Optional.ofNullable(epics.get(id))
                .orElseThrow(() -> new NotFoundException("Epic with id " + id + " not found"));

        return epic.getSubtasks();
    }

    protected void calculateEpicTime(Epic epic) {
        LocalDateTime startTime = null;
        Duration duration = Duration.ZERO;
        LocalDateTime endTime = null;

        final List<Subtask> listOfEpicSubtasks = epic.getSubtasks();

        for (Subtask subtask : listOfEpicSubtasks) {
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
            duration = duration.plus(subtask.getDuration());
        }
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    protected void checkTaskTime(Task currentTask) {
        prioritizedTasks.stream()
                .filter(existingTask -> !Objects.equals(existingTask.getId(), currentTask.getId()))
                .filter(existingTask -> checkTaskTimeIntersection(existingTask, currentTask))
                .findFirst()
                .ifPresent(existingTask -> {
                    throw new ValidationException("New task" + " intersects with task " + existingTask.getId());
                });
    }

    private int generateId() {
        return ++idCounter;
    }

    private void removeFromHistory(Collection<Integer> ids) {
        ids.forEach(historyManager::remove);
    }

    private void calculateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        final Set<TaskStatus> uniqueStatus = epic.getSubtasks()
                .stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toSet());

        if (uniqueStatus.size() == 1) {
            epic.setStatus(uniqueStatus.iterator().next());
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private boolean checkTaskTimeIntersection(Task existingTask, Task currentTask) {
        return (existingTask.getStartTime().isBefore(currentTask.getEndTime())
                && existingTask.getEndTime().isAfter(currentTask.getStartTime()));
    }
}
