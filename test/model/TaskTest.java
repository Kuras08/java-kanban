package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("Эпик")
class TaskTest {

    @Test
    @DisplayName("должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Task task = new Task("Task1", "Description", TaskStatus.NEW);
        Task taskExpected = new Task("Task1", "Description", TaskStatus.NEW);
        assertEqualsTask(taskExpected, task);
    }

    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getId(), actual.getId());
    }
}