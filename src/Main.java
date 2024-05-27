import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        TaskStatus aNew = TaskStatus.NEW;
        TaskStatus inProgress = TaskStatus.IN_PROGRESS;
        TaskStatus done = TaskStatus.DONE;

        Task task1 = manager.createTask(new Task("Task1", "Description task1", aNew));
        int idTask1 = task1.getId();

        Task task2 = manager.createTask(new Task("Task2", "Description task2", inProgress));
        int idTask2 = task2.getId();

        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description task3"));
        int idEpic1 = epic1.getId();

        Subtask subtask1ForEpic1 = manager.createSubtask(new Subtask("Subtask1ForEpic1",
                "Description subtask1ForEpic1", done, idEpic1));
        int idSubtask1ForEpic1 = subtask1ForEpic1.getId();

        Subtask subtask2ForEpic1 = manager.createSubtask(new Subtask("Subtask2ForEpic1",
                "Description subtask2ForEpic1", aNew, idEpic1));
        int idSubtask2ForEpic1 = subtask2ForEpic1.getId();

        Epic epic2 = manager.createEpic(new Epic("Epic2", "Description epic2"));
        int idEpic2 = epic2.getId();

        manager.createSubtask(new Subtask("Subtask1ForEpic2", "Description subtask1ForEpic2",
                aNew, idEpic2));

        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getAllEpics()) {
            System.out.printf("%n%s", epic);
        }
        System.out.println();
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.printf("%n%s", subtask);
        }

        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(Paths.get("resources/task_manager_data.csv"));

        loadedTaskManager.createTask(new Task("Task3", "Description task3", aNew));

        loadedTaskManager.updateEpic(new Epic("New epic1", "New description epic1", idEpic1));

        loadedTaskManager.removeTaskById(idTask1);
        loadedTaskManager.removeEpicById(idEpic2);
        loadedTaskManager.removeSubtaskById(idSubtask2ForEpic1);

        System.out.printf("%n%nLoadedTaskManager:");
        for (Task task : loadedTaskManager.getAllTasks()) {
            System.out.printf("%n%s", task);
        }
        System.out.println();
        for (Epic epic : loadedTaskManager.getAllEpics()) {
            System.out.printf("%n%s", epic);
        }
        System.out.println();
        for (Subtask subtask : loadedTaskManager.getAllSubtasks()) {
            System.out.printf("%n%s", subtask);
        }
    }
}
