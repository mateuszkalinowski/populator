package pl.szaklon.populator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.szaklon.populator.dtos.SongData;
import pl.szaklon.populator.services.InitializationService;

import java.util.ArrayList;

@RestController
@RequestMapping("initialization")
public class InitializationController {

    @Autowired
    private InitializationService initializationService;

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.controllers.InitializationController.class);


    @PostMapping
    public ResponseEntity getUrls(@RequestBody ArrayList<SongData> urls) {
        logger.info("Got POST call on endpoint '/initialization'");
        return initializationService.getUrls(urls);
    }
}
