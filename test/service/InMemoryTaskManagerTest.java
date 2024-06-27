package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        super.setUp();
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(historyManager);
    }
}


