package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FileBackedTaskManagerTest")
public class FileBackedTaskManagerTest {

    TaskManager manager;
    TaskManager loadedTaskManager;
    Path testFile;

    @BeforeEach
    public void beforeEach() {
        testFile = Paths.get("resources/testFile.csv");
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), testFile);

        manager.createTask(new Task("Task1", "Description", TaskStatus.NEW, 1));
        manager.createTask(new Task("Task2", "Description", TaskStatus.DONE, 2));
        manager.createEpic(new Epic("Epic1", "Description", 3));
        manager.createEpic(new Epic("Epic2", "Description", 4));
        manager.createSubtask(new Subtask("Subtask1ForEpic1", "Description",
                TaskStatus.NEW, 5, 3));
        manager.createSubtask(new Subtask("Subtask1ForEpic2", "Description",
                TaskStatus.NEW, 6, 4));

        loadedTaskManager = FileBackedTaskManager.loadFromFile(testFile);
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких задач")
    void shouldCheckSavingAndLoadingTasks() {
        List<Task> savedListTasks = manager.getAllTasks();
        List<Task> loadedListTasks = loadedTaskManager.getAllTasks();

        assertEquals(savedListTasks.size(), loadedListTasks.size());
        for (int i = 1; i <= savedListTasks.size(); i++) {
            assertEqualsTask(manager.getTaskById(i), loadedTaskManager.getTaskById(i));
        }
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких эпиков")
    void shouldCheckSavingAndLoadingEpics() {
        List<Epic> savedListEpics = manager.getAllEpics();
        List<Epic> loadedListEpics = loadedTaskManager.getAllEpics();

        assertEquals(savedListEpics.size(), loadedListEpics.size());
        for (int i = 3; i <= savedListEpics.size(); i++) {
            assertEqualsTask(manager.getEpicById(i), loadedTaskManager.getEpicById(i));
        }
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких подзадач")
    void shouldCheckSavingAndLoadingSubtasks() {
        List<Subtask> savedListSubtasks = manager.getAllSubtasks();
        List<Subtask> loadedListSubtasks = loadedTaskManager.getAllSubtasks();

        assertEquals(savedListSubtasks.size(), loadedListSubtasks.size());
        for (int i = 5; i <= savedListSubtasks.size(); i++) {
            assertEqualsTask(manager.getSubtaskById(i), loadedTaskManager.getSubtaskById(i));
        }
    }

    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getId(), actual.getId());
    }
}

