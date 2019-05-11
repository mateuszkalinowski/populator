package pl.szaklon.populator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.szaklon.populator.dtos.Message;
import pl.szaklon.populator.dtos.SongData;
import pl.szaklon.populator.sql.QueriesBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class InitializationService {

    @Value("${services.extractor.address}")
    private String extractorUrl;

    @Autowired
    private QueriesBuilder queriesBuilder;

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.services.InitializationService.class);

    public ResponseEntity getUrls(ArrayList<SongData> songDataList) {
        ObjectMapper mapper = new ObjectMapper();

        logger.info(String.format("Got %s songs", songDataList.size()));

        try {
            logger.info("Asking exctractor about number of features");
            HttpResponse<String> response = Unirest.get(String.format("%s/status", extractorUrl)).asString();
            HashMap trainingResult =
                    new ObjectMapper().readValue(response.getBody(), HashMap.class);

            int numberOfFeatures = Integer.parseInt(trainingResult.get("n_features").toString());
            logger.info(String.format("Number of features: %s", numberOfFeatures + ""));

            try {
                queriesBuilder.dropSongsFeauturesTable();
                queriesBuilder.dropSongsInfoTable();

                queriesBuilder.createSongsInfoTable();
                queriesBuilder.createSongsFeaturesTable(numberOfFeatures);

                for (SongData songData : songDataList) {
                    if(songData.getId() == null) {
                        logger.error(String.format("Couldn't add song `%s`, id can't be null",songData.toString()));
                        continue;
                    }
                    File tmpMusicFile = new File("/tmp/tmpfile");
                    tmpMusicFile.deleteOnExit();
                    tmpMusicFile.createNewFile();
                    String url = songData.getUrl();
                    try {
                        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(tmpMusicFile);
                        byte dataBuffer[] = new byte[16654];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                        fileOutputStream.flush();

                        HttpResponse<String> extractingFeaturesResult = Unirest.post(String.format("%s/extract_features", extractorUrl))
                                .field("file", tmpMusicFile, "multipart/form-data")
                                .asString();

                        if(extractingFeaturesResult.getStatus() == 400) {
                            logger.error(String.format("Couldn't add song `%s`, this is not an audio file",songData.toString()));
                            return ResponseEntity.badRequest().build();
                        }

                        String extractingFeaturesResultString = extractingFeaturesResult.getBody();

                        extractingFeaturesResultString = extractingFeaturesResultString.trim();

                        String values[] = extractingFeaturesResultString.substring(2, extractingFeaturesResultString.length() - 2).split(",");

                        double[] features = new double[values.length];

                        for (int i = 0; i < values.length; i++) {
                            features[i] = Double.valueOf(values[i]);
                        }
                        try {
                            queriesBuilder.insertIntoSongsFeaturesTable(Integer.valueOf(songData.getId()), features);
                            queriesBuilder.insertIntoSongsInfoTable(Integer.valueOf(songData.getId()), songData.getName(), songData.getUrl(), songData.getGenre());
                        } catch (NumberFormatException e) {
                            logger.error(String.format("Couldn't add song `%s`, id is not a number",songData.toString()));
                            continue;
                        } catch (SQLIntegrityConstraintViolationException e) {
                            logger.error(String.format("Couldn't add song `%s`, id is already taken",songData.toString()));
                            continue;
                        }
                        logger.info(String.format("Added song `%s`, from url `%s`, with features: %s", songData.getName(), songData.getUrl(), Arrays.toString(features)));


                    } catch (IOException e) {
                        logger.error(String.format("Song `%s` couldn't have been downloaded from the URL `%s`", songData.getName(), songData.getUrl()));
                    }
                }


            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

        } catch (UnirestException exception) {
            String message = String.format("Couldn't access extractor, make sure that address '%s' provided in 'application.yml' file is correct", extractorUrl);
            logger.error(message);
            return ResponseEntity.status(500).body(new Message(message));
        } catch (IOException e) {
            String message = "Body of the request is in not proper format";
            logger.error(message);
            return ResponseEntity.badRequest().body(new Message(message));
        }
        logger.info("Initialization finished");
        return ResponseEntity.ok().build();
    }
}
