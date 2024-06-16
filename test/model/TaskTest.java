package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("TaskTest")
class TaskTest {

    @Test
    @DisplayName("Должен проверять задачу со своей копией")
    void shouldEqualsWithCopy() {
        Task task = new Task(1, "Task1", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 15, 12, 0, 0), Duration.ofMinutes(15));

        Task taskExpected = new Task(1, "Task1", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 15, 12, 0, 0), Duration.ofMinutes(15));

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