package pl.szaklon.populator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.szaklon.populator.services.SongRecognitionService;

@RestController
@RequestMapping("recognize")
public class SongRecognitionController {

    @Autowired
    private SongRecognitionService songRecognitionService;

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.controllers.InitializationController.class);


    @PostMapping
    public ResponseEntity recognizeSong(@RequestBody byte[] bytes, @RequestParam(required = false, defaultValue = "3") int numberOfSongs) {
        logger.info("Got POST call on endpoint '/recognize'");
        return songRecognitionService.rezognizeSong(bytes,numberOfSongs);
    }

}
