package concurs.persistence.jdbc;


import java.sql.*;
import java.util.*;

import concurs.model.Participant;
import concurs.model.validators.Validator;
import concurs.persistence.Repository;
import concurs.persistence.config.DBConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBRepositoryParticipant implements Repository<Integer, Participant> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Participant> validator;

    private  final Logger logger = LogManager.getLogger();

    public DBRepositoryParticipant(DBConfig dbConfig, Validator<Participant> validator) {
        logger.traceEntry();
        this.url = dbConfig.getUrl();
        this.username = dbConfig.getUsername();
        this.password = dbConfig.getPassword();
        this.validator = validator;
        logger.info("trying to connect to database ... {}", url);
        logger.traceExit();
    }

    @Override
    public Participant findOne(Integer idS) {
        logger.traceEntry();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM participant WHERE id = " + idS.toString());
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String nume = resultSet.getString("nume");
                Integer varsta = resultSet.getInt("varsta");
                Array probeArray = resultSet.getArray("probe");

                Long[] longs = (Long[])probeArray.getArray();
                Integer[] integers = new Integer[longs.length];
                for (int i = 0; i < longs.length; i++){
                    integers[i] = Math.toIntExact(longs[i]);
                }
                List<Integer> probe = Arrays.asList(integers);

                Participant participant = new Participant(nume, varsta, probe);
                participant.setId(idS);
                return participant;

            } else {
                logger.traceExit();
                return null;
            }

        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry();
        Set<Participant> participants = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from participant");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                Integer varsta = resultSet.getInt("varsta");
                Array probeArray = resultSet.getArray("probe");

                Long[] longs = (Long[])probeArray.getArray();
                Integer[] integers = new Integer[longs.length];
                for (int i = 0; i < longs.length; i++){
                    integers[i] = Math.toIntExact(longs[i]);
                }
                logger.info("Test");

                List<Integer> probe = Arrays.asList(integers);
                probe.sort(Comparator.naturalOrder());

                Participant participant = new Participant(nume, varsta, probe);
                participant.setId(id);
                participants.add(participant);
            }
            logger.traceExit();
            return participants;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return participants;
    }

    public Iterable<Participant> findAllByName(String name) {
        logger.traceEntry();
        String like_name = "'%" + name + "%'";
        Set<Participant> participants = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from participant WHERE nume like " +
                     like_name);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                Integer varsta = resultSet.getInt("varsta");
                Array probeArray = resultSet.getArray("probe");

                Long[] longs = (Long[])probeArray.getArray();
                Integer[] integers = new Integer[longs.length];
                for (int i = 0; i < longs.length; i++){
                    integers[i] = Math.toIntExact(longs[i]);
                }

                List<Integer> probe = Arrays.asList(integers);
                probe.sort(Comparator.naturalOrder());

                Participant participant = new Participant(nume, varsta, probe);
                participant.setId(id);
                participants.add(participant);
            }
            logger.traceExit();
            return participants;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return participants;
    }

    public Iterable<Participant> findAllByAge(Integer age) {
        logger.traceEntry();
        Set<Participant> participants = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from participant WHERE varsta = '"
                     + age.toString()+ "'");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                Integer varsta = resultSet.getInt("varsta");
                Array probeArray = resultSet.getArray("probe");

                Long[] longs = (Long[])probeArray.getArray();
                Integer[] integers = new Integer[longs.length];
                for (int i = 0; i < longs.length; i++){
                    integers[i] = Math.toIntExact(longs[i]);
                }

                List<Integer> probe = Arrays.asList(integers);
                probe.sort(Comparator.naturalOrder());

                Participant participant = new Participant(nume, varsta, probe);
                participant.setId(id);
                participants.add(participant);
            }
            logger.traceExit();
            return participants;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return participants;
    }

    @Override
    public Participant save(Participant entity) {
        logger.traceEntry("saving task {}", entity);
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        String sql = "INSERT INTO participant (nume, varsta, probe) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getNume());
            statement.setInt(2, entity.getVarsta());

            Integer[] probeSimple = entity.getProbe().toArray(new Integer[0]);
            Array probeArray = connection.createArrayOf("BIGINT", probeSimple);
            statement.setArray(3, probeArray);

            int result = statement.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Participant delete(Integer idS) {
        logger.traceEntry("deleting task {}", idS);
        Participant participant = findOne(idS);
        if (participant == null) {
            return null;
        }
        String sql = "DELETE FROM participant WHERE id = " + idS.toString();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int result = statement.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return participant;
    }

    @Override
    public Participant update(Participant entity) {
        logger.traceEntry("updating task {}", entity);
        Integer id = entity.getId();
        Participant oldParticipant = findOne(id);
        if (oldParticipant == null) {
            logger.traceExit();
            return null;
        }
        String sql = "UPDATE participant SET nume = ?, varsta = ?, probe = ? WHERE id = " + id.toString();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getNume());
            statement.setInt(2, entity.getVarsta());

            Integer[] probeSimple = entity.getProbe().toArray(new Integer[0]);
            Array probeArray = connection.createArrayOf("BIGINT", probeSimple);
            statement.setArray(3, probeArray);

            int result = statement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return oldParticipant;
    }
}
