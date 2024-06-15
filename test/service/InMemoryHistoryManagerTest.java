package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();

        task = new Task(1, "Task1", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ZERO);

        epic = new Epic(2, "Epic1", "Description");

        subtask = new Subtask(3, 2, "Subtask1ForEpic1", "Description",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ZERO);
    }

    @Test
    @DisplayName("Должен проверять добавление задач, эпиков и подзадач в список истории")
    void shouldCheckAdditionTasksEpicsAndSubtasksInListHistory() {
        List.of(task, epic, subtask).forEach(historyManager::add);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Должен проверять, что список хранит только уникальные элементы")
    void shouldCheckStoresOnlyUniqueElements() {
        List.of(task, task, epic, epic, subtask, subtask).forEach(historyManager::add);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Должен проверять удаление по id задач, эпиков и подзадач из списка истории")
    void shouldCheckRemoveTasksEpicsAndSubtasksFromListHistory() {
        List.of(task, epic, subtask).forEach(historyManager::add);
        List.of(1, 2, 3).forEach(historyManager::remove);
        assertEquals(0, historyManager.getHistory().size());
    }
}