package pl.szaklon.populator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.szaklon.populator.services.SongRecognitionService;

@RestController
@RequestMapping("recognize")
public class SongRecognitionController {

    @Autowired
    private SongRecognitionService songRecognitionService;

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.controllers.InitializationController.class);


    @PostMapping
    public ResponseEntity recognizeSong(@RequestParam MultipartFile song, @RequestParam(required = false, defaultValue = "3") int numberOfSongs, @RequestParam(required = false, defaultValue = "") String genre) {
        logger.info("Got POST call on endpoint '/recognize'");
        return songRecognitionService.rezognizeSong(song,numberOfSongs,genre);
    }

}
