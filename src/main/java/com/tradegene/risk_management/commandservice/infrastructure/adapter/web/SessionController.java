package com.tradegene.risk_management.commandservice.infrastructure.adapter.web;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.SessionManagerRequest;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.SessionStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@RestController
public class SessionController {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Void>> start(@RequestBody SessionManagerRequest sessionManagerRequest) {

        String topicId = sessionManagerRequest.getTopicId();
        kafkaListenerEndpointRegistry.getListenerContainer(topicId).start();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<Void>> stop(@RequestBody SessionManagerRequest sessionManagerRequest) {

        String topicId = sessionManagerRequest.getTopicId();
        kafkaListenerEndpointRegistry.getListenerContainer(topicId).stop();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/start-all")
    public ResponseEntity<ApiResponse<Void>> startAll() {

        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(object -> {
            object.start();
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop-all")
    public ResponseEntity<ApiResponse<Void>> stopAll() {

        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(object -> {
            object.stop();
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SessionStatusResponse>> status() {

        SessionStatusResponse response = _setStatusResponse();

        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    private SessionStatusResponse _setStatusResponse() {

        List<SessionStatusResponse.Grid> grid = new ArrayList<>();

        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(object -> {

            SessionStatusResponse.Grid row = SessionStatusResponse.Grid.builder()
                    .topic(object.getContainerProperties().getTopics()[0])
                    .isRunning(object.isRunning())
                    .build();

            grid.add(row);
        });

        return SessionStatusResponse.builder()
                .objects(grid)
                .build()
                ;
    }
}
