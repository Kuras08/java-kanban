package service;

import exceptions.FileOperationException;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FileBackedTaskManagerTest")
public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path file;
    private TaskManager loadedTaskManager;

    @BeforeEach
    public void beforeEach() {
        try {
            file = Files.createTempFile("testFile", "csv");
        } catch (IOException e) {
            throw new FileOperationException("Error creating temporary file!");
        }
        super.setUp();
        loadedTaskManager = createTaskManager();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return FileBackedTaskManager.loadFromFile(file);
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

