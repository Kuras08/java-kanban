package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InMemoryHistoryManager")
class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();

        task = new Task("Task1", "description", TaskStatus.NEW, 1);
        epic = new Epic("Epic1", "Description", 2);
        subtask = new Subtask("Subtask1ForEpic1", "Description",
                TaskStatus.NEW, 3, epic.getId());
        subtask2 = new Subtask("Subtask2ForEpic1", "Description",
                TaskStatus.IN_PROGRESS, 4, epic.getId());
    }


    @Test
    @DisplayName("Должен проверять добавление задач, эпиков и подзадач в список истории")
    void shouldCheckAdditionTasksEpicsAndSubtasksInListHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask2);

        assertEquals(4, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Должен проверять, что список хранит только уникальные элементы")
    void shouldCheckStoresOnlyUniqueElements() {
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        historyManager.add(subtask2);

        assertEquals(4, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Должен проверять удаление по id задач, эпиков и подзадач из списка истории")
    void shouldCheckRemoveTasksEpicsAndSubtasksFromListHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);

        assertEquals(0, historyManager.getHistory().size());
    }
}