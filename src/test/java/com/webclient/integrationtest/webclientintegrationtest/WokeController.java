package com.webclient.integrationtest.webclientintegrationtest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WokeController {

    private WokeService service;

    public WokeController(WokeService service) {

        this.service = service;
    }

    public ResponseEntity<WokeResponse> wakeUp() {
        return ResponseEntity.ok(service.getAlarms());
    }

}