package com.example.ocpspring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GardeScheduler {

    @Autowired
    private GardeService gardeService;
    private static final Logger log = LoggerFactory.getLogger(GardeScheduler.class);



    // @Scheduled(cron = "0 0 0 * * FRI")
    @Scheduled(cron = "0 0 0 * * MON")
    //@Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void scheduleGarde() {
        gardeService.assignGardeForNext8Weeks();
        log.info("Guard duty assigned for the next 8 weekends.");
    }
}

