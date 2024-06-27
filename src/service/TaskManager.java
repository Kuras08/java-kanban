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

    Task getTaskById(Integer id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeTaskById(Integer id);

    List<Epic> getAllEpics();

    void removeAllEpicsFromManager();

    Epic getEpicById(Integer id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(Integer id);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasksFromManager();

    Subtask getSubtaskById(Integer id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtaskById(Integer id);

    List<Subtask> getAllSubtasksEpic(Integer id);
}
