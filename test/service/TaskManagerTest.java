package service;

import exceptions.NotFoundException;
import exceptions.ValidationException;
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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManagerTest")
abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task task;
    protected Task task2;
    protected Epic epic;
    protected Epic epic2;
    protected Subtask subtask;
    protected Subtask subtask2;

    protected abstract T createTaskManager();

    @BeforeEach
    void beforeEach() {

        manager = createTaskManager();

        task = manager.createTask(new Task("Task1", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 15, 12, 0, 0), Duration.ofMinutes(15)));

        task2 = manager.createTask(new Task("Task2", "Description", TaskStatus.DONE,
                LocalDateTime.of(2024, 6, 15, 13, 0, 0), Duration.ofMinutes(15)));

        epic = manager.createEpic(new Epic("Epic1", "Description"));

        subtask = manager.createSubtask(new Subtask(epic.getId(), "Subtask1ForEpic1", "Description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 15, 15, 0, 0), Duration.ofMinutes(15)));

        subtask2 = manager.createSubtask(new Subtask(epic.getId(), "Subtask2ForEpic1", "Description",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 15, 16, 0, 0), Duration.ofMinutes(15)));

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
    @DisplayName("Должен проверять обновление всех полей задачи")
    void shouldCheckUpdateAllFieldsTask() {
        Task updatedTask = new Task(task.getId(), "newTask", "newDescription", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 15, 22, 0, 0), Duration.ofMinutes(5));

        manager.updateTask(updatedTask);
        Task savedTask = manager.getTaskById(task.getId());

        assertEqualsTask(updatedTask, savedTask);
    }

    @Test
    @DisplayName("Должен проверять обновление названия и описания эпика")
    void shouldCheckUpdateNameDescriptionEpic() {
        manager.updateEpic(new Epic(epic.getId(), "newEpic", "newDescription"));

        Epic updatedEpic = manager.getEpicById(epic.getId());

        assertEquals("newEpic", updatedEpic.getName());
        assertEquals("newDescription", updatedEpic.getDescription());
    }

    @Test
    @DisplayName("Должен проверять обновление всех полей подзадачи")
    void shouldCheckUpdateAllFieldSubtask() {
        Subtask updatedSubtask = new Subtask(subtask.getId(), epic.getId(), "newSubtask",
                "newDescription", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 15, 17, 0, 0), Duration.ofMinutes(5));

        manager.updateSubtask(updatedSubtask);
        Task savedSubtask = manager.getSubtaskById(subtask.getId());

        assertEqualsTask(updatedSubtask, savedSubtask);
    }

    @Test
    @DisplayName("Должен проверять обновление статуса, время начала, окончания и длительность эпика")
    void shouldCheckUpdateStatusStartTimeEndTimeDuration() {
        manager.removeSubtaskById(subtask2.getId());

        assertEquals(subtask.getStatus(), epic.getStatus());
        assertEquals(subtask.getStartTime(), epic.getStartTime());
        assertEquals(subtask.getDuration(), epic.getDuration());
        assertEquals(subtask.getEndTime(), epic.getEndTime());
    }


    @Test
    @DisplayName("Должен проверять удаление задачи по id из списка")
    void shouldCheckRemoveTaskById() {
        manager.removeTaskById(task.getId());
        assertThrows(NotFoundException.class, () -> manager.getTaskById(task.getId()));
    }

    @Test
    @DisplayName("Должен проверять удаление эпика по id из списка")
    void shouldCheckRemoveEpicAndSubtasksById() {
        manager.removeEpicById(epic.getId());
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epic.getId()));
    }

    @Test
    @DisplayName("Должен проверять удаление подзадачи по id из списка")
    void shouldCheckRemoveSubtaskById() {
        manager.removeSubtaskById(subtask.getId());
        assertThrows(NotFoundException.class, () -> manager.getSubtaskById(subtask.getId()));
    }

    @Test
    @DisplayName("Должен проверять, получение списка подзадач эпика по его id")
    void shouldCheckReceivingListSubtasksEpicByHisId() {
        List<Subtask> subtasksEpic = manager.getAllSubtasksEpic(epic.getId());

        assertNotNull(subtasksEpic);
        assertEqualsTask(subtask, subtasksEpic.getFirst());
        assertEqualsTask(subtask2, subtasksEpic.get(1));
    }

    @Test
    @DisplayName("Должен проверять пересечение задачи со своей копией")
    public void shouldCheckIntersectionTaskWithItsCopy() {
        assertDoesNotThrow(() -> {
            Task copyTask = new Task(1, "Task1", "Description", TaskStatus.NEW,
                    LocalDateTime.of(2024, 6, 15, 12, 0, 0), Duration.ofMinutes(15));

            manager.updateTask(copyTask);
        });
    }

    @Test
    @DisplayName("Должен проверять пересечение задач")
    void shouldCheckIntersectionTask() {
        Subtask intersectingSubtask = new Subtask(3, "Subtask", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 15, 12, 7, 0), Duration.ofMinutes(10));

        assertThrows(ValidationException.class, () -> manager.createSubtask(intersectingSubtask));
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

    protected static class HistoryManagerStub implements HistoryManager {
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
