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
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @DisplayName("должен возвращать список с задачами")
    @Test
    void shouldReturnListTasks() {
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEqualsTask(task, tasks.getFirst());
    }

    @DisplayName("должен возвращать список с эпиками")
    @Test
    void shouldReturnListEpics() {
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEqualsTask(epic, epics.getFirst());
    }

    @DisplayName("должен возвращать список с подзадачами")
    @Test
    void shouldReturnListSubtasks() {
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEqualsTask(subtask, subtasks.getFirst());
    }

    @DisplayName("должен удалять все задачи из списка")
    @Test
    void shouldRemoveAllTasksFromList() {
        manager.deleteAllTasks();
        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size());
    }

    @DisplayName("должен удалять все эпики из списка и все подзадачи данных эпиков")
    @Test
    void shouldRemoveAllEpicsFromList() {
        manager.deleteAllEpics();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
        assertEquals(0, epicSubtasks.size());
    }

    @DisplayName("должен удалять все подзадачи из общего списка, затем удалить подзадачи из списка эпиков," +
            " после обновить статусы эпиков")
    @Test
    void shouldRemoveAllSubtasksFromList() {
        manager.deleteAllSubtasks();

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEquals(0, subtasks.size());
        assertEquals(0, epicSubtasks.size());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @DisplayName("должен получать задачу по Id")
    @Test
    void shouldGetTaskById() {
        Task retrievedTask = manager.getTaskById(task.getId());
        assertEqualsTask(task, retrievedTask);
    }

    @DisplayName("должен получать эпик по Id")
    @Test
    void shouldGetEpicById() {
        Epic retrievedEpic = manager.getEpicById(epic.getId());
        assertEqualsTask(epic, retrievedEpic);
    }

    @DisplayName("должен получать подзадачу по Id")
    @Test
    void shouldGetSubtaskById() {
        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());
        assertEqualsTask(subtask, retrievedSubtask);
    }

    @DisplayName("должен создавать Id и сохранять задачу по этому Id")
    @Test
    void shouldCreateIdAndSaveTaskById() {
        Task createdTask = manager.createTask(task);
        assertEqualsTask(task, createdTask);
        assertEquals(task.getId(), createdTask.getId());
    }


    @DisplayName("должен создавать Id и сохранять эпик по этому Id")
    @Test
    void shouldCreateIdAndSaveEpicById() {
        Epic createdEpic = manager.getEpicById(epic.getId());
        assertEqualsTask(epic, createdEpic);
        assertEquals(epic.getId(), createdEpic.getId());
    }

    @DisplayName("должен создавать Id, сохранять подзадачу по этому Id в общем списке и добавлять ее в список эпика," +
            " после обновлять статус эпика")
    @Test
    void shouldCreateIdAndSaveSubtaskById() {
        Task createdSubtask = manager.getSubtaskById(subtask.getId());
        assertEqualsTask(subtask, createdSubtask);
        assertEquals(subtask.getId(), createdSubtask.getId());

        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(1, subtasks.size());
    }

    @DisplayName("должен обновлять и сохранять новую задачу взамен старой")
    @Test
    void shouldUpdateAndSaveNewTaskInsteadOld() {
        Task updatedTask = new Task("newTask", "newDescription", TaskStatus.IN_PROGRESS, task.getId());
        manager.updateTask(updatedTask);

        Task savedTask = manager.getTaskById(updatedTask.getId());
        List<Task> tasks = manager.getAllTasks();

        assertEqualsTask(savedTask, updatedTask);
        assertEquals(1, tasks.size());
    }

    @DisplayName("должен обновлять только имя и описание эпика")
    @Test
    void shouldUpdateNameAndDescriptionEpic() {
        Epic updatedEpic = new Epic("newEpic", "newDescription", epic.getId());
        manager.updateEpic(updatedEpic);

        Epic savedEpic = manager.getEpicById(updatedEpic.getId());
        List<Epic> epics = manager.getAllEpics();

        assertEquals(savedEpic.getTitle(), updatedEpic.getTitle());
        assertEquals(savedEpic.getDescription(), updatedEpic.getDescription());
        assertEquals(savedEpic.getId(), updatedEpic.getId());
        assertEquals(1, epics.size());
    }

    @DisplayName("должен обновлять и сохранять новую подзадачу взамен старой в общем списке," +
            " обновлять ее в списке подзадач эпика, затем обновлять статус эпика")
    @Test
    void shouldUpdateAndSaveNewSubtaskInsteadOld() {
        Subtask updatedSubtask = new Subtask("newSubtask", "newDescription",
                TaskStatus.IN_PROGRESS, subtask.getId(), epic.getId());
        manager.updateSubtask(updatedSubtask);

        Subtask savedSubtask = manager.getSubtaskById(subtask.getId());
        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEqualsTask(savedSubtask, updatedSubtask);
        assertEquals(1, subtasks.size());
        assertEquals(1, epicSubtasks.size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("должен удалять задачу по Id")
    @Test
    void shouldDeleteTaskById() {
        manager.deleteTaskById(task.getId());
        Task remoteTask = manager.getTaskById(task.getId());
        List<Task> tasks = manager.getAllTasks();

        assertEquals(0, tasks.size());
        assertNull(remoteTask);
    }

    @DisplayName("должен удалять эпик по Id и все подзадачи данного эпика")
    @Test
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

    @DisplayName("должен удалять подзадачу по Id в общем списке и удалять подзадачу в списке эпика," +
            " затем обновлять статус эпика")
    @Test
    void shouldDeleteSubtaskById() {
        manager.deleteSubtaskById(subtask.getId());

        Subtask remoteSubtask = manager.getSubtaskById(subtask.getId());

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Subtask> subtasksEpic = epic.getSubtasks();

        assertNull(remoteSubtask);
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasksEpic.size());
    }

    @DisplayName("должен возвращать список подзадач эпика по его Id")
    @Test
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