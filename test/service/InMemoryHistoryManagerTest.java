package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        historyManager = new InMemoryHistoryManager();

        task = new Task("Task1", "Description", TaskStatus.NEW, 1);
        epic = new Epic("Epic1", "Description", 2);
        subtask = new Subtask("Subtask1ForEpic1", "Description",
                TaskStatus.NEW, 3, epic.getId());
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