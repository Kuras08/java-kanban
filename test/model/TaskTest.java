package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("TaskTest")
class TaskTest {

    @Test
    @DisplayName("Должен проверять задачу со своей копией")
    void shouldEqualsWithCopy() {
        Task task = new Task("Task1", "Description", TaskStatus.NEW);
        Task taskExpected = new Task("Task1", "Description", TaskStatus.NEW);
        assertEqualsTask(taskExpected, task);
    }

    protected static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getEndTime(), actual.getEndTime());
    }
}