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
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        historyManager = new HistoryManagerStub();
        manager = new InMemoryTaskManager(historyManager);

        task = manager.createTask(new Task("Task1", "Description", TaskStatus.NEW));
        epic = manager.createEpic(new Epic("Epic1", "Description"));
        subtask = manager.createSubtask(new Subtask("Subtask1ForEpic1", "Description",
                TaskStatus.DONE, epic.getId()));
    }

    @Test
    @DisplayName("должен возвращать список с задачами")
    void shouldReturnListTasks() {
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEqualsTask(task, tasks.getFirst());
    }

    @Test
    @DisplayName("должен возвращать список с эпиками")
    void shouldReturnListEpics() {
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEqualsTask(epic, epics.getFirst());
    }

    @Test
    @DisplayName("должен возвращать список с подзадачами")
    void shouldReturnListSubtasks() {
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEqualsTask(subtask, subtasks.getFirst());
    }

    @Test
    @DisplayName("должен удалять все задачи из списка")
    void shouldRemoveAllTasksFromList() {
        manager.deleteAllTasks();
        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    @DisplayName("должен удалять все эпики из списка и все подзадачи данных эпиков")
    void shouldRemoveAllEpicsFromList() {
        manager.deleteAllEpics();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
        assertEquals(0, epicSubtasks.size());
    }

    @Test
    @DisplayName("должен удалять все подзадачи из общего списка, затем удалить подзадачи из списка эпиков," +
            " после обновить статусы эпиков")
    void shouldRemoveAllSubtasksFromList() {
        manager.deleteAllSubtasks();

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEquals(0, subtasks.size());
        assertEquals(0, epicSubtasks.size());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("должен получать задачу по Id")
    void shouldGetTaskById() {
        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEqualsTask(task, retrievedTask);
    }

    @Test
    @DisplayName("должен получать эпик по Id")
    void shouldGetEpicById() {
        Epic retrievedEpic = manager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEqualsTask(epic, retrievedEpic);
    }

    @Test
    @DisplayName("должен получать подзадачу по Id")
    void shouldGetSubtaskById() {
        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEqualsTask(subtask, retrievedSubtask);
    }

    @Test
    @DisplayName("должен создавать Id и сохранять задачу по этому Id")
    void shouldCreateIdAndSaveTaskById() {
        List<Task> tasks = manager.getAllTasks();

        assertEqualsTask(task, tasks.getFirst());
        assertEquals(1, tasks.size());
    }

    @Test
    @DisplayName("должен создавать Id и сохранять эпик по этому Id")
    void shouldCreateIdAndSaveEpicById() {
        List<Epic> epics = manager.getAllEpics();

        assertEqualsTask(epic, epics.getFirst());
        assertEquals(1, epics.size());
    }

    @Test
    @DisplayName("должен создавать Id, сохранять подзадачу по этому Id в общем списке и добавлять ее в список эпика," +
            " после обновлять статус эпика")
    void shouldCreateIdAndSaveSubtaskById() {
        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> subtasksEpic = epic.getSubtasks();

        assertEqualsTask(subtask, subtasks.getFirst());
        assertEquals(subtask, subtasksEpic.getFirst());
        assertEquals(1, subtasks.size());
        assertEquals(1, subtasksEpic.size());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    @DisplayName("должен обновлять и сохранять новую задачу взамен старой")
    void shouldUpdateAndSaveNewTaskInsteadOld() {
        manager.updateTask(new Task("newTask", "newDescription", TaskStatus.IN_PROGRESS, task.getId()));

        Task updatedTask = manager.getTaskById(task.getId());
        List<Task> tasks = manager.getAllTasks();

        assertEquals("newTask", updatedTask.getTitle());
        assertEquals("newDescription", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEqualsTask(updatedTask, tasks.getFirst());
        assertEquals(1, tasks.size());
    }

    @Test
    @DisplayName("должен обновлять только имя и описание эпика")
    void shouldUpdateNameAndDescriptionEpic() {
        manager.updateEpic(new Epic("newEpic", "newDescription", epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());
        List<Epic> epics = manager.getAllEpics();

        assertEquals("newEpic", updatedEpic.getTitle());
        assertEquals("newDescription", updatedEpic.getDescription());
        assertEquals(1, epics.size());
    }

    @Test
    @DisplayName("должен обновлять и сохранять новую подзадачу взамен старой в общем списке," +
            " обновлять ее в списке подзадач эпика, затем обновлять статус эпика")
    void shouldUpdateAndSaveNewSubtaskInsteadOld() {
        manager.updateSubtask(new Subtask("newSubtask", "newDescription",
                TaskStatus.IN_PROGRESS, subtask.getId(), epic.getId()));

        Subtask updatedSubtask = manager.getSubtaskById(subtask.getId());
        List<Subtask> subtasks = epic.getSubtasks();
        List<Subtask> subtasksEpic = epic.getSubtasks();

        assertEquals("newSubtask", updatedSubtask.getTitle());
        assertEquals("newDescription", updatedSubtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtask.getStatus());
        assertEquals(updatedSubtask, subtasksEpic.getFirst());
        assertEquals(1, subtasks.size());
        assertEquals(1, subtasksEpic.size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("должен удалять задачу по Id")
    void shouldDeleteTaskById() {
        manager.deleteTaskById(task.getId());
        Task remoteTask = manager.getTaskById(task.getId());
        List<Task> tasks = manager.getAllTasks();

        assertNull(remoteTask);
        assertEquals(0, tasks.size());

    }

    @Test
    @DisplayName("должен удалять эпик по Id и все подзадачи данного эпика")
    void shouldDeleteEpicById() {
        manager.deleteEpicById(epic.getId());

        Epic remoteEpic = manager.getEpicById(epic.getId());
        Subtask remoteSubtask = manager.getSubtaskById(subtask.getId());

        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> subtasksEpic = epic.getSubtasks();

        assertNull(remoteEpic);
        assertNull(remoteSubtask);
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasksEpic.size());
    }

    @Test
    @DisplayName("должен удалять подзадачу по Id в общем списке и удалять подзадачу в списке эпика," +
            " затем обновлять статус эпика")
    void shouldDeleteSubtaskById() {
        manager.deleteSubtaskById(subtask.getId());

        Subtask remoteSubtask = manager.getSubtaskById(subtask.getId());

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> subtasksEpic = epic.getSubtasks();

        assertNull(remoteSubtask);
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasksEpic.size());
    }

    @Test
    @DisplayName("должен возвращать список подзадач эпика по его Id")
    void shouldGetListSubtasksEpicByHisId() {
        List<Subtask> subtasksEpic = manager.getAllSubtasksEpic(epic.getId());
        assertEquals(1, subtasksEpic.size());
        assertEqualsTask(subtask, subtasksEpic.getFirst());
    }


    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getId(), actual.getId());
    }

    private static class HistoryManagerStub implements HistoryManager {
        @Override
        public void add(Task task) {

        }

        @Override
        public List<Task> getHistory() {
            return null;
        }
    }
}