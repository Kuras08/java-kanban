package service;

import model.Epic;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InMemoryHistoryManager")
class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    List<Task> history;
    Task task1;
    Task task2;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        history = historyManager.getHistory();
        task1 = new Task("Task1", "description", TaskStatus.NEW);
        task2 = new Task("Task2", "description", TaskStatus.NEW);
    }


    @Test
    @DisplayName("должен добавлять задачу и сохранять ее предыдущую версию")
    void shouldAddTaskSavePreviousVersion() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(3, history.size());
        assertEqualsTask(history.get(0), history.get(2));
    }

    @Test
    @DisplayName("должен удалить самую первую задачу и добавить новую, если размер списка равен 10")
    void shouldRemoveOldestTaskAndAddNewIfListSize10() {
        for (int i = 1; i <= 10; i++) {
            historyManager.add(task1);
            assertEquals(i, history.size());
        }

        Epic newEpic = new Epic("newTask", "description");
        historyManager.add(newEpic);
        assertEqualsTask(newEpic, history.get(9));
        assertEquals(10, history.size());
    }

    @Test
    @DisplayName("список не должен быть null")
    void shouldNotBeNull() {
        assertNotNull(history);
    }

    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getId(), actual.getId());
    }
}