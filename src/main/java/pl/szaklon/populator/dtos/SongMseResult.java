package pl.szaklon.populator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongMseResult implements Comparable<SongMseResult> {

    private String name;
    private String url;
    private String genre;
    private double mse;

    @Override
    public int compareTo(SongMseResult o) {
        if(this.mse > o.mse)
            return 1;
        else if (this.mse < o.mse)
            return -1;
        return 0;
    }

}
