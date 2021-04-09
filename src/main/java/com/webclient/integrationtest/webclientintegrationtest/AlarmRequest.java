package com.webclient.integrationtest.webclientintegrationtest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmRequest {
    int year;
    int month;
    int day;
    int hour;
    String message;
}
