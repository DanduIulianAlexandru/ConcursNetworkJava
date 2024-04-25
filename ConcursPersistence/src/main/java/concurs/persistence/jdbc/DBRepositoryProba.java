package concurs.persistence.jdbc;


import concurs.model.Distanta;
import concurs.model.Proba;
import concurs.model.Stil;
import concurs.model.validators.ValidationException;
import concurs.model.validators.Validator;
import concurs.persistence.Repository;
import concurs.persistence.config.DBConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DBRepositoryProba implements Repository<Integer, Proba> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Proba> validator;
    private  final Logger logger = LogManager.getLogger();

    public DBRepositoryProba(DBConfig dbConfig, Validator<Proba> validator) {
        logger.traceEntry();
        this.url = dbConfig.getUrl();
        this.username = dbConfig.getUsername();
        this.password = dbConfig.getPassword();
        this.validator = validator;
        logger.info("trying to connect to database ... {}", url);
        logger.traceExit();
    }

    @Override
    public Proba findOne(Integer idS) {
        logger.traceEntry();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM proba WHERE id = " + idS.toString());
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String distantaS = resultSet.getString("distanta");
                String stilS = resultSet.getString("stil");
                Integer nrParticipanti = resultSet.getInt("nr_participanti");

                Distanta distanta = switch (distantaS){
                    case "M50" -> Distanta.M50;
                    case "M200" -> Distanta.M200;
                    case "M800" -> Distanta.M800;
                    case "M1500" -> Distanta.M1500;
                    default -> throw new ValidationException("Distanta enum error");
                };

                Stil stil = switch (stilS){
                    case "LIBER" -> Stil.LIBER;
                    case "FLUTURE" -> Stil.FLUTURE;
                    case "MIXT" -> Stil.MIXT;
                    case "SPATE" -> Stil.SPATE;
                    default -> throw new ValidationException("Stil enum error");
                };

                Proba proba = new Proba(distanta, stil);
                proba.setId(idS);
                proba.setNrParticipanti(nrParticipanti);
                logger.traceExit();
                return proba;
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
    public Iterable<Proba> findAll() {
        logger.traceEntry();
        Set<Proba> probe = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from proba");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String distantaS = resultSet.getString("distanta");
                String stilS = resultSet.getString("stil");
                Integer nrParticipanti = resultSet.getInt("nr_participanti");

                Distanta distanta = switch (distantaS){
                    case "M50" -> Distanta.M50;
                    case "M200" -> Distanta.M200;
                    case "M800" -> Distanta.M800;
                    case "M1500" -> Distanta.M1500;
                    default -> throw new ValidationException("Distanta enum error");
                };

                Stil stil = switch (stilS){
                    case "LIBER" -> Stil.LIBER;
                    case "FLUTURE" -> Stil.FLUTURE;
                    case "MIXT" -> Stil.MIXT;
                    case "SPATE" -> Stil.SPATE;
                    default -> throw new ValidationException("Stil enum error");
                };

                Proba proba = new Proba(distanta, stil);
                proba.setId(id);
                proba.setNrParticipanti(nrParticipanti);

                probe.add(proba);
            }
            logger.traceExit();
            return probe;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return probe;
    }

    @Override
    public Proba save(Proba entity) {
        logger.traceEntry("saving task {}", entity);
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        String sql = "INSERT INTO proba (distanta, stil, nr_participanti) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String distanta = switch (entity.getDistanta()){
                case M50 -> "M50";
                case M200 -> "M200";
                case M800 -> "M800";
                case M1500 -> "M1500";
            };

            String stil = switch (entity.getStil()){
                case MIXT -> "MIXT";
                case LIBER -> "LIBER";
                case SPATE -> "SPATE";
                case FLUTURE -> "FLUTURE";
            };


            statement.setString(1, distanta);
            statement.setString(2, stil);
            statement.setInt(3, entity.getNrParticipanti());

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
    public Proba delete(Integer idS) {
        logger.traceEntry("deleting task {}", idS);
        Proba proba = findOne(idS);
        if (proba == null) {
            return null;
        }
        String sql = "DELETE FROM proba WHERE id = " + idS.toString();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int result = statement.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return proba;
    }

    @Override
    public Proba update(Proba entity) {
        logger.traceEntry("updating task {}", entity);
        Integer id = entity.getId();
        Proba oldProba = findOne(id);
        if (oldProba == null) {
            logger.traceExit();
            return null;
        }
        String sql = "UPDATE proba SET distanta = ?, stil = ?, nr_participanti = ? WHERE id = " + id.toString();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String distanta = switch (entity.getDistanta()){
                case M50 -> "M50";
                case M200 -> "M200";
                case M800 -> "M800";
                case M1500 -> "M1500";
            };

            String stil = switch (entity.getStil()){
                case MIXT -> "MIXT";
                case LIBER -> "LIBER";
                case SPATE -> "SPATE";
                case FLUTURE -> "FLUTURE";
            };


            statement.setString(1, distanta);
            statement.setString(2, stil);
            statement.setInt(3, entity.getNrParticipanti());

            int result = statement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return oldProba;
    }

    public Proba incrementNr(Integer id){
        logger.traceEntry("incrementing task {}", id.toString());
        if(findOne(id) == null){
            return null;
        }
        String sql = "UPDATE proba SET nr_participanti = nr_participanti + 1 WHERE id = " + id.toString();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int result = statement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return findOne(id);
    }
}
