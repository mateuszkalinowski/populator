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

    public void dropSongsInfoTable() throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "DROP TABLE IF EXISTS `populator`.`SONGS_INFO`";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();

        connection.close();
    }

    public void dropSongsFeauturesTable() throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "DROP TABLE IF EXISTS `populator`.`SONGS_FEATURES`";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
        connection.close();
    }

    public void createSongsInfoTable() throws SQLException {

        Connection connection = dataSource.getConnection();

        String query = "CREATE TABLE `populator`.`SONGS_INFO` (\n" +
                "  `ID` INT NOT NULL,\n" +
                "  `GENRE` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`ID`));\n";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
        connection.close();

    }

    public void createSongsFeaturesTable(int numberOfFeatures) throws SQLException {

        Connection connection = dataSource.getConnection();

        String query = "CREATE TABLE `populator`.`SONGS_FEATURES` (\n" +
                "  `ID` INT NOT NULL,\n" +
                "  `NAME` VARCHAR(128) NOT NULL,\n" +
                "  `URL` VARCHAR(128) NOT NULL,\n";

        for (int i = 0; i < numberOfFeatures; i++) {
            query += String.format("  `FEATURE_%s` DECIMAL(20,16) NOT NULL,\n", i);
        }
        query += "  PRIMARY KEY (`ID`));";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
        connection.close();

    }

    public void insertIntoSongsFeaturesTable(int id, String name, String url, double[] features) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "INSERT INTO `populator`.`SONGS_FEATURES` VALUES\n" +
                "(?,?,?";

        for (int i = 0; i < features.length; i++) {
            query += ",?";
        }
        query += ");";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, url);

        for (int i = 0; i < features.length; i++) {
            preparedStatement.setDouble(i + 4, features[i]);
        }

        preparedStatement.execute();
        connection.close();
    }

    public void insertIntoSongsInfoTable(int id, String genre) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "INSERT INTO `populator`.`SONGS_INFO` VALUES (?,?);";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, genre);

        preparedStatement.execute();
        connection.close();

    }

    public SortedSet<SongMseResult> mse(double[] features, int numberOfSongs, String genre) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "SELECT * FROM populator.SONGS_FEATURES JOIN populator.SONGS_INFO ON SONGS_FEATURES.ID = SONGS_INFO.ID";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        ResultSet rs = preparedStatement.executeQuery();

        SortedSet<SongMseResult> songMseResultSortedSet = new TreeSet<>();

        while (rs.next()) {

            if(genre.equals("") || rs.getString("GENRE").toLowerCase().trim().equals(genre.toLowerCase().trim())) {

                double mse = 0;

                for (int i = 0; i < features.length; i++) {
                    double value = rs.getDouble("FEATURE_" + i);

                    mse += Math.pow(features[i] - value, 2);

                }

                mse /= features.length;

                songMseResultSortedSet.add(new SongMseResult(rs.getString("NAME"), rs.getString("URL"), rs.getString("GENRE"), mse));

                while (songMseResultSortedSet.size() > numberOfSongs) {
                    songMseResultSortedSet.remove(songMseResultSortedSet.last());
                }
            }
        }

        return songMseResultSortedSet;
    }

}
