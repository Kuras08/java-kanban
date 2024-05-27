package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest {

    TaskManager manager;
    HistoryManagerStub historyManager;
    Task task;
    Task task2;
    Epic epic;
    Epic epic2;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        historyManager = new HistoryManagerStub();
        manager = new InMemoryTaskManager(historyManager);

        task = manager.createTask(new Task("Task1", "Description", TaskStatus.NEW));
        task2 = manager.createTask(new Task("Task2", "Description", TaskStatus.DONE));
        epic = manager.createEpic(new Epic("Epic1", "Description"));
        subtask = manager.createSubtask(new Subtask("Subtask1ForEpic1", "Description",
                TaskStatus.NEW, epic.getId()));
        subtask2 = manager.createSubtask(new Subtask("Subtask2ForEpic1", "Description",
                TaskStatus.NEW, epic.getId()));
        epic2 = manager.createEpic(new Epic("Epic2", "Description"));
    }

    @Test
    @DisplayName("Должен проверять, что возвращаемый список содержит задачи")
    void shouldCheckReturnedListTasks() {
        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Должен проверять, что возвращаемый список содержит эпики")
    void shouldCheckReturnedListEpics() {
        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size());
    }

    @Test
    @DisplayName("Должен проверять, что возвращаемый список содержит подзадачи")
    void shouldCheckReturnedListSubtasks() {
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    @DisplayName("Должен проверять, что все задачи удалены из списка")
    void shouldCheckRemoveAllTasksFromList() {
        manager.removeAllTasksFromManager();
        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    @DisplayName("Должен проверять, что все эпики и подзадачи удалены из списков")
    void shouldCheckRemoveAllEpicsAndSubtasksFromLists() {
        manager.removeAllEpicsFromManager();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
    }

    @Test
    @DisplayName("Должен проверять, что подзадачи удалены из списков")
    void shouldCheckRemoveAllSubtasksFromLists() {
        manager.removeAllSubtasksFromManager();

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = manager.getAllSubtasksEpic(epic.getId());

        assertEquals(0, subtasks.size());
        assertEquals(0, epicSubtasks.size());
    }

    @Test
    @DisplayName("Должен проверять, что задача извлекается по существующему id")
    void shouldCheckTaskRetrievedByExistingId() {
        Task receivedTask = manager.getTaskById(task.getId());
        assertNotNull(receivedTask);
    }

    @Test
    @DisplayName("Должен проверять, что эпик извлекается по существующему id")
    void shouldCheckEpicRetrievedByExistingId() {
        Epic receivedEpic = manager.getEpicById(epic.getId());
        assertNotNull(receivedEpic);
    }

    @Test
    @DisplayName("Должен проверять, что подзадача извлекается по существующему id")
    void shouldCheckSubtaskRetrievedByExistingId() {
        Subtask receivedSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(receivedSubtask);
    }

    @Test
    @DisplayName("Должен проверять сохранение задач по уникальному id")
    void shouldCheckTasksSavedById() {
        assertNotEquals(task.getId(), task2.getId());
    }

    @Test
    @DisplayName("Должен проверять сохранение эпиков по уникальному id")
    void shouldCheckEpicsSavedById() {
        assertNotEquals(epic.getId(), epic2.getId());
    }

    @Test
    @DisplayName("Должен проверять сохранение подзадач по уникальному id")
    void shouldCheckSubtasksSavedById() {
        assertNotEquals(subtask.getId(), subtask2.getId());
    }

    @Test
    @DisplayName("Должен проверять обновление названия, описания и статуса задачи")
    void shouldCheckUpdateTitleDescriptionStatusTask() {
        manager.updateTask(new Task("newTask", "newDescription", TaskStatus.IN_PROGRESS, task.getId()));

        Task updatedTask = manager.getTaskById(task.getId());

        assertEquals("newTask", updatedTask.getName());
        assertEquals("newDescription", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    @DisplayName("Должен проверять обновление названия и описания эпика")
    void shouldCheckUpdateTitleDescriptionEpic() {
        manager.updateEpic(new Epic("newEpic", "newDescription", epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());

        assertEquals("newEpic", updatedEpic.getName());
        assertEquals("newDescription", updatedEpic.getDescription());
    }

    @Test
    @DisplayName("Должен проверять обновление названия, описания, статуса подзадачи и статуса ее эпика")
    void shouldCheckUpdateTitleDescriptionStatusSubtaskAndStatusEpic() {
        manager.updateSubtask(new Subtask("newSubtask", "newDescription",
                TaskStatus.DONE, subtask.getId(), epic.getId()));

        Subtask updatedSubtask = manager.getSubtaskById(subtask.getId());

        assertEquals("newSubtask", updatedSubtask.getName());
        assertEquals("newDescription", updatedSubtask.getDescription());
        assertEquals(TaskStatus.DONE, updatedSubtask.getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Должен проверять удаление задачи по id из списка")
    void shouldCheckRemoveTaskById() {
        manager.removeTaskById(task.getId());
        assertThrows(IllegalArgumentException.class, () -> manager.getTaskById(task.getId()));
    }

    @Test
    @DisplayName("Должен проверять удаление эпика по id из списка")
    void shouldCheckRemoveEpicAndSubtasksById() {
        manager.removeEpicById(epic.getId());
        assertThrows(IllegalArgumentException.class, () -> manager.getEpicById(epic.getId()));
    }

    @Test
    @DisplayName("Должен проверять удаление подзадачи по id из списка")
    void shouldCheckRemoveSubtaskById() {
        manager.removeSubtaskById(subtask.getId());
        assertThrows(IllegalArgumentException.class, () -> manager.getSubtaskById(subtask.getId()));
    }

    @Test
    @DisplayName("Должен проверять, получение списка подзадач эпика по его id")
    void shouldCheckReceivingListSubtasksEpicByHisId() {
        List<Subtask> subtasksEpic = manager.getAllSubtasksEpic(epic.getId());

        assertNotNull(subtasksEpic);
        assertEqualsTask(subtask, subtasksEpic.getFirst());
        assertEqualsTask(subtask2, subtasksEpic.get(1));
    }


    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getId(), actual.getId());
    }

    private static class HistoryManagerStub implements HistoryManager {
        @Override
        public void add(Task task) {

        }

        @Override
        public void remove(int id) {

        }

        @Override
        public List<Task> getHistory() {
            return null;
        }
    }
}