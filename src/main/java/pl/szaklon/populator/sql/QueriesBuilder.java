package pl.szaklon.populator.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class QueriesBuilder {

    @Autowired
    private DataSource dataSource;

    public void dropSongsInfoTable() throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "DROP TABLE IF EXISTS `populator`.`SONGS_INFO`";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
    }

    public void dropSongsFeauturesTable() throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "DROP TABLE IF EXISTS `populator`.`SONGS_FEATURES`";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();
    }

    public void createSongsInfoTable() throws SQLException {

        Connection connection = dataSource.getConnection();

        String query = "CREATE TABLE `populator`.`SONGS_INFO` (\n" +
                "  `id` INT NOT NULL,\n" +
                "  `genre` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`id`));\n";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();

    }

    public void createSongsFeaturesTable(int numberOfFeatures) throws SQLException {

        Connection connection = dataSource.getConnection();

        String query = "CREATE TABLE `populator`.`SONGS_FEATURES` (\n" +
                "  `ID` INT NOT NULL,\n" +
                "  `NAME` VARCHAR(128) NOT NULL,\n" +
                "  `URL` VARCHAR(128) NOT NULL,\n";

        for(int i = 0; i < numberOfFeatures; i++) {
            query += String.format("  `FEATURE_%s` DECIMAL(20,16) NOT NULL,\n",i);
        }
        query += "  PRIMARY KEY (`ID`));";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.executeUpdate();

    }

    public void insertIntoSongsFeaturesTable(int id, String name, String url, double[] features) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query = "INSERT INTO `populator`.`SONGS_FEATURES` VALUES\n" +
            "(?,?,?";

        for(int i = 0; i < features.length;i++) {
            query += ",?";
        }
        query += ");";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1,id);
        preparedStatement.setString(2,name);
        preparedStatement.setString(3,url);

        for(int i = 0; i < features.length;i++) {
            preparedStatement.setDouble(i+4,features[i]);
        }

        preparedStatement.execute();
    }

    public void insertIntoSongsInfoTable(int id, String genre) throws SQLException{
        Connection connection = dataSource.getConnection();

        String query = "INSERT INTO `populator`.`SONGS_INFO` VALUES (?,?);";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1,id);
        preparedStatement.setString(2,genre);

        preparedStatement.execute();

    }

}
