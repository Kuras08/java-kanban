import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        TaskStatus aNew = TaskStatus.NEW;
        TaskStatus inProgress = TaskStatus.IN_PROGRESS;
        TaskStatus done = TaskStatus.DONE;

        Task task1 = manager.createTask(new Task("Task1", "Description", aNew));
        int idTask1 = task1.getId();

        Task task2 = manager.createTask(new Task("Task2", "Description", inProgress));
        int idTask2 = task2.getId();

        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description"));
        int idEpic1 = epic1.getId();

        Subtask subtask1ForEpic1 = manager.createSubtask(new Subtask("Subtask1ForEpic1",
                "Description", done, idEpic1));
        int idSubtask1ForEpic1 = subtask1ForEpic1.getId();

        Subtask subtask2ForEpic1 = manager.createSubtask(new Subtask("Subtask2ForEpic1",
                "Description", aNew, idEpic1));
        int idSubtask2ForEpic1 = subtask2ForEpic1.getId();

        Epic epic2 = manager.createEpic(new Epic("Epic2", "Description"));
        int idEpic2 = epic2.getId();

        manager.createSubtask(new Subtask("Subtask1ForEpic2", "Description",
                inProgress, idEpic2));

        /*System.out.println("Tasks:\n" + manager.getAllTasks() + "\n");
        System.out.println("Epics:\n" + manager.getAllEpics() + "\n");
        System.out.println("Subtasks:\n" + manager.getAllSubtasks() + "\n");

        manager.updateTask(new Task("newTask1", "newDescription", DONE, idTask1));

        manager.updateEpic(new Epic("newEpic1", "newDescription", idEpic1));

        manager.updateSubtask(new Subtask("newSubtask1ForEpic1", "newDescription",
                DONE, idSubtask1ForEpic1, idEpic1));*/

        manager.getTaskById(idTask1);
        manager.getTaskById(idTask2);
        manager.getEpicById(idEpic1);
        manager.getSubtaskById(idSubtask1ForEpic1);
        manager.getSubtaskById(idSubtask2ForEpic1);
        manager.getTaskById(idTask1);
        manager.getTaskById(idTask2);
        manager.getEpicById(idEpic1);
        manager.getSubtaskById(idSubtask1ForEpic1);
        manager.getSubtaskById(idSubtask2ForEpic1);

        /*System.out.println("Tasks (updating and deletion):\n" + manager.getAllTasks() + "\n");
        System.out.println("Epics (updating and deletion):\n" + manager.getAllEpics() + "\n");
        System.out.println("Subtasks (updating and deletion):\n" + manager.getAllSubtasks() + "\n");*/

        System.out.println("History:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.deleteTaskById(idTask2);
        manager.deleteEpicById(idEpic1);
        System.out.println();

        System.out.println("History (deletion) :");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
