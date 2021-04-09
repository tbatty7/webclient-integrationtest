package com.webclient.integrationtest.webclientintegrationtest;

import com.webclient.integrationtest.webclientintegrationtest.service.WokeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WokeController {

    private WokeService service;

    public WokeController(WokeService service) {

        this.service = service;
    }

    @GetMapping(value = "/v1/alarms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WokeResponse> wakeUp(@RequestHeader(value = "Identification-No")
                                                           String identificationNo) {
        WokeResponse response = service.getAlarms();
        response.setIdentificationNumber(identificationNo);
        return ResponseEntity.ok(response);
    }

}