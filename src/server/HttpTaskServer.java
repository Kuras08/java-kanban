package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import exceptions.ServerCreatingException;
import handlers.*;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final TaskManager manager;

    private final HttpServer httpServer;

    private final Gson gson;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException exception) {
            throw new ServerCreatingException("Failed to create HTTP server on port " + PORT);
        }
        this.gson = getGson();
    }

    public void start() {
        createContexts();
        httpServer.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stopped");
    }

    private void createContexts() {
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }
}
