package pl.szaklon.populator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.szaklon.populator.dtos.SongData;
import pl.szaklon.populator.services.InitializationService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("initialization")
public class InitializationController {

    @Autowired
    private InitializationService initializationService;

    @PostMapping
    public ResponseEntity getUrls(@RequestBody ArrayList<SongData> urls) {

        return initializationService.getUrls(urls);
    }
}
