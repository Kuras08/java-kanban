package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private HistoryManagerStub historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new HistoryManagerStub();
        super.beforeEach();
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(historyManager);
    }
}


