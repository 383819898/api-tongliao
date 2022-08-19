//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.service;

import cn.xyz.mianshi.scheduleds.Task;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TaskService {
    private TaskService taskService;
    private DelayQueue<Task> delayQueue = new DelayQueue();

    public TaskService() {
    }

    @PostConstruct
    private void init() {
        this.taskService = this;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Task task = (Task)TaskService.this.delayQueue.take();
                        task.run();
                    } catch (Exception var2) {
                        var2.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(Task task) {
        if (!this.delayQueue.contains(task)) {
            this.delayQueue.add(task);
        }
    }

    public void removeTask(Task task) {
        this.delayQueue.remove(task);
    }
}
