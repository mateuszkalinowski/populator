package pl.szaklon.populator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongData {
    private String id;
    private String url;
    private String genre;
    private String name;
}
