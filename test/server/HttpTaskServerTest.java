package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.NotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import util.GsonFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("HttpTaskServerTest")
public class HttpTaskServerTest {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final String TASKS_ENDPOINT = "tasks/";
    private static final String EPICS_ENDPOINT = "epics/";
    private static final String SUBTASKS_ENDPOINT = "subtasks/";
    private static final String PRIORITIZED_ENDPOINT = "prioritized";
    private static final String HISTORY_ENDPOINT = "history";

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    Task task;
    Task task2;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;
    Epic epic2;

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefaultInMemory();
        taskServer = new HttpTaskServer(manager);
        gson = GsonFactory.createGson();
        manager.removeAllTasksFromManager();
        manager.removeAllEpicsFromManager();
        taskServer.start();

        task = new Task("Task1", "Description", TaskStatus.NEW,
                "26.06.2024, 22:00:00", 15);

        task2 = new Task("Task2", "Description", TaskStatus.DONE,
                "26.06.2024, 18:00:00", 15);

        epic = new Epic("Epic1", "Description");

        subtask = new Subtask(1, "Subtask1ForEpic1", "Description",
                TaskStatus.NEW, "26.06.2024, 20:00:00", 15);

        subtask2 = new Subtask(1, "Subtask2ForEpic1", "Description",
                TaskStatus.IN_PROGRESS, "26.06.2024, 09:00:00", 15);

        epic2 = new Epic("Epic2", "Description");
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Должен проверять получение задачи по id")
    void shouldCheckGetTaskById() throws IOException, InterruptedException {
        manager.createTask(task);

        HttpResponse<String> response = getResponse(null, TASKS_ENDPOINT + task.getId(), MethodType.GET);

        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEqualsTask(task, taskFromServer);
    }

    @Test
    @DisplayName("Должен проверять получение задач")
    void shouldCheckGetTasks() throws IOException, InterruptedException {
        List.of(task, task2).forEach(manager::createTask);

        HttpResponse<String> response = getResponse(null, TASKS_ENDPOINT, MethodType.GET);

        List<Task> tasksFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getAllTasks(), tasksFromServer);
    }

    @Test
    @DisplayName("Должен проверять добавление задачи")
    void shouldCheckAdditionTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        task.setId(1);

        HttpResponse<String> response = getResponse(taskJson, TASKS_ENDPOINT, MethodType.POST);

        Task taskFromManager = manager.getTaskById(task.getId());
        assertEquals(201, response.statusCode());
        assertEqualsTask(task, taskFromManager);
    }

    @Test
    @DisplayName("Должен проверять обновление задачи по id")
    void shouldCheckUpdateTaskById() throws IOException, InterruptedException {
        manager.createTask(task);
        Task updatedTask = new Task(1, "newTask1", "newDescription", TaskStatus.IN_PROGRESS,
                "26.06.2024, 06:00:00", 10);

        String taskJson = gson.toJson(updatedTask);
        HttpResponse<String> response = getResponse(taskJson, TASKS_ENDPOINT + task.getId(), MethodType.POST);

        Task taskFromManager = manager.getTaskById(task.getId());
        assertEquals(201, response.statusCode());
        assertEqualsTask(updatedTask, taskFromManager);
    }

    @Test
    @DisplayName("Должен проверять удаление задачи по id")
    void shouldCheckDeletionTaskById() throws IOException, InterruptedException {
        manager.createTask(task);

        HttpResponse<String> response = getResponse(null, TASKS_ENDPOINT + task.getId(), MethodType.DELETE);

        assertEquals(204, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getTaskById(task.getId()));
    }

    @Test
    @DisplayName("Должен проверять получение эпика по id")
    void shouldCheckGetEpicById() throws IOException, InterruptedException {
        manager.createEpic(epic);

        HttpResponse<String> response = getResponse(null, EPICS_ENDPOINT + epic.getId(), MethodType.GET);

        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEqualsTask(epic, epicFromServer);
    }

    @Test
    @DisplayName("Должен проверять получение эпиков")
    void shouldCheckGetEpics() throws IOException, InterruptedException {
        List.of(epic, epic2).forEach(manager::createEpic);

        HttpResponse<String> response = getResponse(null, EPICS_ENDPOINT, MethodType.GET);

        List<Epic> epicsFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getAllEpics(), epicsFromServer);
    }

    @Test
    @DisplayName("Должен проверять получение списка подзадач эпика по его id")
    void shouldCheckGetListSubtasksEpicByHisId() throws IOException, InterruptedException {
        manager.createEpic(epic);
        List.of(subtask, subtask2).forEach(manager::createSubtask);

        HttpResponse<String> response = getResponse(null, EPICS_ENDPOINT + epic.getId() + "/subtasks",
                MethodType.GET);

        List<Subtask> subtasksFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getAllSubtasks(), subtasksFromServer);
    }

    @Test
    @DisplayName("Должен проверять добавление эпика")
    void shouldCheckAdditionEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        epic.setId(1);

        HttpResponse<String> response = getResponse(epicJson, EPICS_ENDPOINT, MethodType.POST);

        Epic epicFromManager = manager.getEpicById(epic.getId());
        assertEquals(201, response.statusCode());
        assertEqualsTask(epic, epicFromManager);
    }

    @Test
    @DisplayName("Должен проверять удаление эпика по id")
    void shouldCheckDeletionEpicById() throws IOException, InterruptedException {
        manager.createEpic(epic);

        HttpResponse<String> response = getResponse(null, EPICS_ENDPOINT + epic.getId(), MethodType.DELETE);

        assertEquals(204, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epic.getId()));
    }

    @Test
    @DisplayName("Должен проверять получение подзадачи по id")
    void shouldCheckGetSubtaskById() throws IOException, InterruptedException {
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        HttpResponse<String> response = getResponse(null, SUBTASKS_ENDPOINT + subtask.getId(), MethodType.GET);

        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEqualsTask(subtask, subtaskFromServer);
    }

    @Test
    @DisplayName("Должен проверять получение подзадач ")
    void shouldCheckGetSubtasks() throws IOException, InterruptedException {
        manager.createEpic(epic);
        List.of(subtask, subtask2).forEach(manager::createSubtask);

        HttpResponse<String> response = getResponse(null, SUBTASKS_ENDPOINT, MethodType.GET);

        List<Subtask> subtasksFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getAllSubtasks(), subtasksFromServer);
    }

    @Test
    @DisplayName("Должен проверять добавление подзадачи")
    void shouldCheckAdditionSubtask() throws IOException, InterruptedException {
        manager.createEpic(epic);
        String subtaskJson = gson.toJson(subtask);
        subtask.setId(2);

        HttpResponse<String> response = getResponse(subtaskJson, SUBTASKS_ENDPOINT, MethodType.POST);

        Subtask subtaskFromManager = manager.getSubtaskById(subtask.getId());
        assertEquals(201, response.statusCode());
        assertEqualsTask(subtask, subtaskFromManager);
    }

    @Test
    @DisplayName("Должен проверять обновление подзадачи по id")
    void shouldCheckUpdateSubtaskById() throws IOException, InterruptedException {
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        Subtask updatedSubtask = new Subtask(2, 1, "newSubtask", "newDescription", TaskStatus.DONE,
                "26.06.2024, 09:00:00", 5);

        String subtaskJson = gson.toJson(updatedSubtask);
        HttpResponse<String> response = getResponse(subtaskJson, SUBTASKS_ENDPOINT + subtask.getId(), MethodType.POST);

        Subtask subtaskFromManager = manager.getSubtaskById(subtask.getId());
        assertEquals(201, response.statusCode());
        assertEqualsTask(updatedSubtask, subtaskFromManager);
    }

    @Test
    @DisplayName("Должен проверять удаление подзадачи по id")
    void shouldCheckDeletionSubtaskById() throws IOException, InterruptedException {
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        HttpResponse<String> response = getResponse(null, SUBTASKS_ENDPOINT + subtask.getId(),
                MethodType.DELETE);

        assertEquals(204, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getTaskById(subtask.getId()));
    }

    @Test
    @DisplayName("Должен проверять получение списка приоритетов")
    void shouldCheckGetListPrioritized() throws IOException, InterruptedException {
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        List.of(task, task2).forEach(manager::createTask);

        HttpResponse<String> response = getResponse(null, PRIORITIZED_ENDPOINT, MethodType.GET);

        List<Task> prioritizedFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getPrioritizedTasks(), prioritizedFromServer);
    }

    @Test
    @DisplayName("Должен проверять получение списка истории")
    void shouldCheckGetListHistory() throws IOException, InterruptedException {
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicById(epic.getId());

        HttpResponse<String> response = getResponse(null, HISTORY_ENDPOINT, MethodType.GET);

        List<Task> listHistoryFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEqualsListTasks(manager.getHistory(), listHistoryFromServer);
    }

    private HttpResponse<String> getResponse(String requestBody, String path, MethodType method) throws IOException,
            InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(BASE_URL + path);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(url);

            switch (method) {
                case GET -> requestBuilder.GET();
                case POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                case DELETE -> requestBuilder.DELETE();
                default -> throw new NotFoundException("Unsupported method type: " + method);
            }
            HttpRequest request = requestBuilder.build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    private static void assertEqualsListTasks(List<? extends Task> expected, List<? extends Task> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEqualsTask(expected.get(i), actual.get(i));
        }
    }

    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getEndTime(), actual.getEndTime());
    }
}
