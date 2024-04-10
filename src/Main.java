import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task firstTask = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        Task secondTask = taskManager.createTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS));

        Epic firstEpic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));

        Subtask firstSubtaskFirstEpic = taskManager.createSubtask(new Subtask("Подзадача 1 в Эпике 1",
                "Описание подзадачи 1", TaskStatus.DONE, firstEpic.getId()));

        Subtask secondSubtaskFirstEpic = taskManager.createSubtask(new Subtask("Подзадача 2 в Эпике 1",
                "Описание подзадачи 2", TaskStatus.NEW, firstEpic.getId()));

        Epic secondEpic = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));

        Subtask firstSubtaskSecondEpic = taskManager.createSubtask(new Subtask("Подзадача 1 в Эпике 2",
                "Описание подзадачи 1", TaskStatus.IN_PROGRESS, secondEpic.getId()));

        System.out.println("Задачи:\n" + taskManager.getAllTasks() + "\n");
        System.out.println("Эпики:\n" + taskManager.getAllEpics() + "\n");
        System.out.println("Подзадачи:\n" + taskManager.getAllSubtasks() + "\n");
        System.out.println();

        Task updatedFirstTask = new Task("Обновленная задача 1", "Описание обновленной задачи 1",
                TaskStatus.DONE, firstTask.getId());
        taskManager.updateTask(updatedFirstTask);

        Subtask updatedSecondSubtaskFirstEpic = new Subtask("Обновленная подзадача 2 в Эпике 1",
                "Описание обновленной подзадачи 2", TaskStatus.DONE, secondSubtaskFirstEpic.getId(), firstEpic.getId());
        taskManager.updateSubtask(updatedSecondSubtaskFirstEpic);

        taskManager.deleteTaskById(secondTask.getId());
        taskManager.deleteSubtaskById(firstSubtaskSecondEpic.getId());
        taskManager.deleteSubtaskById(firstSubtaskFirstEpic.getId());
        taskManager.deleteEpicById(secondEpic.getId());

        System.out.println("Задачи (обновление и удаление):\n" + taskManager.getAllTasks() + "\n");
        System.out.println("Эпики (обновление и удаление):\n" + taskManager.getAllEpics() + "\n");
        System.out.println("Подзадачи (обновление и удаление):\n" + taskManager.getAllSubtasks() + "\n");
    }
}
