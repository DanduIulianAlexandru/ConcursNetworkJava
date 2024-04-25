package concurs;

import concurs.model.validators.AdminValidator;
import concurs.model.validators.ParticipantValidator;
import concurs.model.validators.ProbaValidator;
import concurs.networking.utils.AbstractServer;
import concurs.networking.utils.ConcursProtobuffConcurrentServer;
import concurs.networking.utils.ConcursRpcConcurrentServer;
import concurs.networking.utils.ServerException;
import concurs.persistence.config.DBConfig;
import concurs.persistence.jdbc.DBRepositoryHbm;
import concurs.persistence.jdbc.DBRepositoryParticipant;
import concurs.persistence.jdbc.DBRepositoryProba;
import concurs.server.ConcursServicesImpl;
import concurs.services.IConcursServices;

public class StartRpcServer {
    private static final int defaultPort = 55555;

    public static void main(String[] args) {
        DBConfig dbConfig = new DBConfig();
        dbConfig.loadConfig("db.config");
        // DBRepositoryAdmin repositoryAdmin = new DBRepositoryAdmin(dbConfig, new AdminValidator());
        DBRepositoryHbm repositoryAdmin = new DBRepositoryHbm(new AdminValidator());
        DBRepositoryParticipant repositoryParticipant = new DBRepositoryParticipant(dbConfig, new ParticipantValidator());
        DBRepositoryProba repositoryProba = new DBRepositoryProba(dbConfig, new ProbaValidator());

        repositoryAdmin.findAll().forEach(System.out::println);
        System.out.println(repositoryAdmin.findOne(1));

        IConcursServices concursServices = new ConcursServicesImpl(repositoryAdmin, repositoryParticipant, repositoryProba);

        int concursServerPort = defaultPort;

        System.out.println("Starting server on port: " + concursServerPort);

        AbstractServer server = new ConcursRpcConcurrentServer(concursServerPort, concursServices);
        //AbstractServer server = new ConcursProtobuffConcurrentServer(concursServerPort, concursServices);

        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
