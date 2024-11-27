package uni.local.controllers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.async.ResultCallback.Adapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

public class PostgresContainerManager {

    private static final String CONTAINER_NAME = "my_postgres_container";

    public static void startContainer() {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        try {
            // Check if container already exists
            List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(List.of(CONTAINER_NAME))
                .withShowAll(true)
                .exec();

            if (containers != null && !containers.isEmpty()) {
                // Container already exists, start it if it's not running
                Container container = containers.get(0);
                if (!container.getState().equals("running")) {
                    dockerClient.startContainerCmd(container.getId()).exec();
                    System.out.println("PostgreSQL container started with ID: " + container.getId());
                } else {
                    System.out.println("PostgreSQL container is already running with ID: " + container.getId());
                }
            } else {
                // Pull postgres
                dockerClient.pullImageCmd("postgres:latest")
                        .exec(new Adapter<PullResponseItem>() {
                            @Override
                            public void onNext(PullResponseItem item) {
                                System.out.println("Pulling: " + item.getStatus());
                                super.onNext(item);
                            }
                        }).awaitCompletion();

                // PostgreSQL container
                ExposedPort tcp5432 = ExposedPort.tcp(5432);
                Ports.Binding binding = Ports.Binding.bindPort(5432);
                Ports portBindings = new Ports();
                portBindings.bind(tcp5432, binding);

                CreateContainerResponse container = dockerClient.createContainerCmd("postgres:latest")
                        .withEnv("POSTGRES_DB=mydb", "POSTGRES_USER=user", "POSTGRES_PASSWORD=password")
                        .withExposedPorts(tcp5432)
                        .withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings))
                        .withName(CONTAINER_NAME)
                        .exec();

                // Start container
                dockerClient.startContainerCmd(container.getId()).exec();
                System.out.println("PostgreSQL container started with ID: " + container.getId());

                // Wait for PostgreSQL to initialize
                Thread.sleep(20000);

                // Initialize database
                initializeDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeDatabase() {
        String url = "jdbc:postgresql://localhost:5432/mydb"; 
        String user = "user";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id SERIAL PRIMARY KEY, " +
                         "username VARCHAR(255) UNIQUE NOT NULL, " +
                         "password VARCHAR(255) NOT NULL);";

            stmt.executeUpdate(sql);
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
