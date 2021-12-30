package com.jackbracey.recipe.Cron;

import org.springframework.scheduling.annotation.Scheduled;

public class RecipeScraping {

    @Scheduled(cron = "0 0 1 * *")
    void scrapeBbc() {

    }

}
