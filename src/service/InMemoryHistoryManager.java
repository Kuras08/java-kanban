package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history = new HashMap<>();

    private Node first;
    private Node last;

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
    }

    private List<Task> getTasks() {
        final List<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.item);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

    @Override
    public void add(Task task) {
        final Node node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
        history.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        final Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        public Node(Node prev, Task item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }
}
