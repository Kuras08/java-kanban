package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

    List<Task> getAllTasks();

    void removeAllTasksFromManager();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int id);

    List<Epic> getAllEpics();

    void removeAllEpicsFromManager();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasksFromManager();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtaskById(int id);

    List<Subtask> getAllSubtasksEpic(int id);
}
