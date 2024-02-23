//package com.tradegene.risk_management.commandservice.infrastructure.adapter.web;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.configuration.JobRegistry;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ScheduledFuture;
//
//@RequestMapping("/api/batch")
//@RequiredArgsConstructor
//@RestController
//public class BatchController {
//
//    private final TaskScheduler taskScheduler;
//
//    private final JobLauncher jobLauncher;
//    private final JobExplorer jobExplorer;
//    private final JobRegistry jobRegistry;
//
//    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
//
//    @PostMapping("/start")
//    public ResponseEntity<String> start(String jobName) {
//
//        try {
//
//            if (scheduledTasks.get(jobName) != null) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(jobName + " already started");
//            }
//
//            ScheduledFuture<?> future = taskScheduler.schedule(() -> _runBatchJob(jobName), new CronTrigger("*/1 * * * * *"));
//            scheduledTasks.put(jobName, future);
//
//            return ResponseEntity.ok(jobName + " started.");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(jobName + " start failed: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/stop")
//    public ResponseEntity<String> stop(String jobName) {
//
//        if (scheduledTasks.get(jobName) == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(jobName+ " not started");
//        }
//
//        scheduledTasks.get(jobName).cancel(true);
//        scheduledTasks.remove(jobName);
//
//        return ResponseEntity.ok(jobName + " stopped.");
//    }
//
//    private void _runBatchJob(String jobName) {
//
//        try {
//            Job job = jobRegistry.getJob(jobName);
//            JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
//                    .getNextJobParameters(job)
//                    .toJobParameters();
//
//            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//
//            // 정상종료가 아니면 정지 처리
//            if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus()) == false) {
//                if (scheduledTasks.get(jobName) != null) {
//                    scheduledTasks.get(jobName).cancel(true);
//                    scheduledTasks.remove(jobName);
//                }
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
