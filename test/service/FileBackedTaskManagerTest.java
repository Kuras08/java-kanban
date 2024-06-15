package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FileBackedTaskManagerTest")
public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path file;
    private TaskManager loadedTaskManager;

    @BeforeEach
    public void beforeEach() {
        file = Paths.get("resources/testFile.csv");
        super.beforeEach();
        loadedTaskManager = FileBackedTaskManager.loadFromFile(file);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Managers.getDefaultHistory(), file);
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких задач")
    void shouldCheckSavingAndLoadingTasks() {
        assertEqualsListTasks(manager.getAllTasks(), loadedTaskManager.getAllTasks());
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких эпиков")
    void shouldCheckSavingAndLoadingEpics() {
        assertEqualsListTasks(manager.getAllEpics(), loadedTaskManager.getAllEpics());
    }

    @Test
    @DisplayName("Должен проверять сохранение и загрузку нескольких подзадач")
    void shouldCheckSavingAndLoadingSubtasks() {
        assertEqualsListTasks(manager.getAllSubtasks(), loadedTaskManager.getAllSubtasks());
    }

    private static void assertEqualsListTasks(List<? extends Task> expected, List<? extends Task> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEqualsTask(expected.get(i), actual.get(i));
        }
    }
}

