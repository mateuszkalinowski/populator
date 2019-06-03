package pl.szaklon.populator.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.szaklon.populator.dtos.SongMseResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class QueriesBuilder {

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(pl.szaklon.populator.sql.QueriesBuilder.class);

    @Autowired
    private DataSource dataSource;

    public void dropSongsFeauturesTable() throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "DROP TABLE IF EXISTS SONGS_FEATURES";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
        connection.close();
    }

    public void createSongsFeaturesTable(int numberOfFeatures) throws SQLException {

        Connection connection = dataSource.getConnection();

        String query = "CREATE TABLE SONGS_FEATURES (\n" +
                "  `ID` INT NOT NULL,\n";

        for (int i = 0; i < numberOfFeatures; i++) {
            query += String.format("  `FEATURE_%s` DECIMAL(20,16) NOT NULL,\n", i);
        }
        query += "  PRIMARY KEY (`ID`));";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
        connection.close();

    }

    public void insertIntoSongsFeaturesTable(int id, double[] features) throws SQLException {
        Connection connection = dataSource.getConnection();

        StringBuilder query = new StringBuilder("INSERT INTO `SONGS_FEATURES` VALUES\n" +
                "(?");

        for (double ignored : features) {
            query.append(",?");
        }
        query.append(");");

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

        preparedStatement.setInt(1, id);

        for (int i = 0; i < features.length; i++) {
            preparedStatement.setDouble(i + 2, features[i]);
        }

        preparedStatement.execute();
        connection.close();
    }

    public SortedSet<SongMseResult> mse(double[] features, int numberOfSongs) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "SELECT * FROM SONGS_FEATURES";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        ResultSet rs = preparedStatement.executeQuery();

        SortedSet<SongMseResult> songMseResultSortedSet = new TreeSet<>();

        while (rs.next()) {

                double mse = 0;

                for (int i = 0; i < features.length; i++) {
                    double value = rs.getDouble("FEATURE_" + i);

                    mse += Math.pow(features[i] - value, 2);

                }

                mse /= features.length;

                songMseResultSortedSet.add(new SongMseResult(Integer.valueOf(rs.getString("ID")), mse));

                while (songMseResultSortedSet.size() > numberOfSongs) {
                    songMseResultSortedSet.remove(songMseResultSortedSet.last());
                }
        }

        connection.close();

        return songMseResultSortedSet;
    }

}
