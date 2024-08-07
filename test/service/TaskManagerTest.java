package service;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManagerTest")
abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected HistoryManagerStub historyManager;
    protected Task task;
    protected Task task2;
    protected Epic epic;
    protected Epic epic2;
    protected Subtask subtask;
    protected Subtask subtask2;

    protected abstract T createTaskManager();

    void setUp() {
        historyManager = new HistoryManagerStub();
        manager = createTaskManager();

        task = manager.createTask(new Task("Task1", "Description", TaskStatus.NEW,
                "26.06.2024, 22:00:00", 15));

        task2 = manager.createTask(new Task("Task2", "Description", TaskStatus.DONE,
                "26.06.2024, 18:00:00", 15));

        epic = manager.createEpic(new Epic("Epic1", "Description"));

        subtask = manager.createSubtask(new Subtask(epic.getId(), "Subtask1ForEpic1", "Description",
                TaskStatus.NEW, "26.06.2024, 20:00:00", 15));

        subtask2 = manager.createSubtask(new Subtask(epic.getId(), "Subtask2ForEpic1", "Description",
                TaskStatus.IN_PROGRESS, "26.06.2024, 09:00:00", 15));

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
        Task savedTask1 = manager.getTaskById(task.getId());
        Task savedTask2 = manager.getTaskById(task2.getId());
        assertEquals(task.getId(), savedTask1.getId());
        assertEquals(task2.getId(), savedTask2.getId());
    }

    @Test
    @DisplayName("Должен проверять сохранение эпиков по уникальному id")
    void shouldCheckEpicsSavedById() {
        Epic savedEpic1 = manager.getEpicById(epic.getId());
        Epic savedEpic2 = manager.getEpicById(epic2.getId());
        assertEquals(epic.getId(), savedEpic1.getId());
        assertEquals(epic2.getId(), savedEpic2.getId());

    }

    @Test
    @DisplayName("Должен проверять сохранение подзадач по уникальному id")
    void shouldCheckSubtasksSavedById() {
        Subtask savedSubtask1 = manager.getSubtaskById(subtask.getId());
        Subtask savedSubtask2 = manager.getSubtaskById(subtask2.getId());
        assertNotNull(savedSubtask1);
        assertNotNull(savedSubtask2);
        assertNotEquals(savedSubtask1, savedSubtask2);
    }

    @Test
    @DisplayName("Должен проверять обновление всех полей задачи")
    void shouldCheckUpdateAllFieldsTask() {
        Task updatedTask = new Task(task.getId(), "newTask", "newDescription", TaskStatus.IN_PROGRESS,
                "26.06.2024, 06:00:00", 5);

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
                "newDescription", TaskStatus.IN_PROGRESS, "26.06.2024, 12:00:00", 10);

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
        Task copyTask = new Task(1, "Task1", "Description", TaskStatus.NEW,
                "26.06.2024, 22:00:00", 15);

        assertDoesNotThrow(() -> manager.updateTask(copyTask));
    }

    @Test
    @DisplayName("Должен проверять пересечение задач")
    void shouldCheckIntersectionTasks() {
        Subtask intersectingSubtask = new Subtask(3, "Subtask", "Description", TaskStatus.NEW,
                "26.06.2024, 18:10:00", 15);

        assertThrows(ValidationException.class, () -> manager.createSubtask(intersectingSubtask));
    }

    @Test
    @DisplayName("Должен проверять, что список хранит отсортироаванные по времени задачи и подзадачи ")
    void shouldCheckSortedListOfTasks() {
        List<Task> sortedList = manager.getPrioritizedTasks();
        assertEquals(4, sortedList.size());
        assertEqualsTask(subtask2, sortedList.getFirst());
        assertEqualsTask(task, sortedList.getLast());
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
