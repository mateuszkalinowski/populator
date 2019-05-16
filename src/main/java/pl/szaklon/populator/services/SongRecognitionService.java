package pl.szaklon.populator.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.szaklon.populator.dtos.Message;
import pl.szaklon.populator.dtos.SongMseResult;
import pl.szaklon.populator.sql.QueriesBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.SortedSet;

@Service
public class SongRecognitionService {

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.services.SongRecognitionService.class);

    @Value("${services.extractor.address}")
    private String extractorUrl;

    @Autowired
    private QueriesBuilder queriesBuilder;

    public ResponseEntity rezognizeSong(byte[] song, int numberOfSongs) {

        try {
            File songToRecognize = new File("/tmp/songToRecognize");
            songToRecognize.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(songToRecognize);
            fileOutputStream.write(song);
            fileOutputStream.close();

            HttpResponse<String> extractingFeaturesResult = Unirest.post(String.format("%s/extract_features",extractorUrl))
                    .field("file", songToRecognize,"multipart/form-data")
                    .asString();

            if(extractingFeaturesResult.getStatus() == 400) {
                logger.error("Provided file is not an audio file");
                return ResponseEntity.badRequest().build();
            }

            String extractingFeaturesResultString = extractingFeaturesResult.getBody();

            extractingFeaturesResultString = extractingFeaturesResultString.trim();

            String values[] = extractingFeaturesResultString.substring(2,extractingFeaturesResultString.length()-2).split(",");

            double[] features = new double[values.length];

            for(int i = 0; i < values.length;i++) {
                features[i] = Double.valueOf(values[i]);
            }

            logger.info(String.format("Features of song to recognize: %s", Arrays.toString(features)));

            SortedSet<SongMseResult> result = queriesBuilder.mse(features,numberOfSongs);

            logger.info("Songs recognition finished, result:");
            for (SongMseResult songMseResult : result) {
                logger.info(songMseResult.toString());
            }
            return ResponseEntity.ok(result);

        } catch (IOException e) {

        } catch (UnirestException e) {
            String message = String.format("Couldn't access extractor, make sure that address '%s' provided in 'application.yml' file is correct", extractorUrl);
            logger.error(message);
            return ResponseEntity.status(500).body(new Message(message));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();

    }
}
