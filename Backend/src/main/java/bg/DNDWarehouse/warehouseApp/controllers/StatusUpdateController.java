package bg.DNDWarehouse.warehouseApp.controllers;

import bg.DNDWarehouse.warehouseApp.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

public class StatusUpdateController {

    @SpringBootApplication
    @EnableScheduling
    @RestController
    public static class StatusUpdater {

        @Autowired
        private TaskController tc;

        public void update(String[] args) {
            SpringApplication.run(StatusUpdater.class, args);
        }
        @Scheduled(fixedRate = 5*60*1000) //5min
        @PostMapping("/task")
        public void updateTasks() {
            tc.actualizeTaskInfo();
        }
    }
}
