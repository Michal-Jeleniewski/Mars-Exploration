package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;

import java.sql.*;
import java.util.Set;

public class ExplorationSimulator {

    private final ExplorationResultDisplay explorationResultDisplay;
    private final MapLoader mapLoader;
    private final ConfigurationValidator configurationValidator;
    private final AllOutcomeAnalyzer allOutcomeAnalyzer;
    private final MovementService movementService;
    private final Logger logger;

    public ExplorationSimulator(ExplorationResultDisplay explorationResultDisplay,
                                MapLoader mapLoader,
                                ConfigurationValidator configurationValidator,
                                MovementService movementService,
                                AllOutcomeAnalyzer allOutcomeAnalyzer,
                                Logger logger) {
        this.explorationResultDisplay = explorationResultDisplay;
        this.mapLoader = mapLoader;
        this.configurationValidator = configurationValidator;
        this.movementService = movementService;
        this.allOutcomeAnalyzer = allOutcomeAnalyzer;
        this.logger = logger;
    }

    public void runSimulation(ConfigurationParameters configurationParameters, Rover rover) {
        Simulation simulation = new Simulation(
                configurationParameters.maxSteps(),
                rover,
                configurationParameters.spaceshipLandingPoint(),
                mapLoader.load(configurationParameters.mapPath()),
                configurationParameters.symbols()
        );

        SimulationStepsLogging simulationStepsLogging = new SimulationStepsLogging(simulation, logger, allOutcomeAnalyzer);

        while (simulation.explorationOutcome() == null) {
            movementService.move();

            configurationParameters.symbols().forEach(rover::checkForObjectsAround);
            rover.addScannedCoordinates();

            ExplorationOutcome explorationOutcome = allOutcomeAnalyzer.analyze(simulation);

            if (explorationOutcome != null) {
                int numberOfResources = rover.getObjectsPoints().values().stream().mapToInt(Set::size).sum();
                saveInDatabase(simulation.numberOfSteps(), numberOfResources, explorationOutcome);
                simulation.setExplorationOutcome(explorationOutcome);
            }

            simulationStepsLogging.logSteps();

            simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);
        }
        explorationResultDisplay.displayExploredMap(rover);
    }

    public void saveInDatabase(int steps, int numberOfResources, ExplorationOutcome explorationOutcome) {
        String DB_URL = "jdbc:sqlite:src/main/resources/exploration.db";
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            Statement statement = connection.createStatement();
            if (!tableExists(connection, "Explorations")) {
                String createTableQuery = "CREATE TABLE Explorations ( id INTEGER NOT NULL UNIQUE, steps INTEGER NOT NULL, resources INTEGER NOT NULL, outcome TEXT NOT NULL, PRIMARY KEY(id AUTOINCREMENT) )";
                statement.executeUpdate(createTableQuery);
            }
            String query = "INSERT INTO Explorations (steps, resources, outcome) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, steps);
            preparedStatement.setInt(2, numberOfResources);
            preparedStatement.setString(3, String.valueOf(explorationOutcome));
            preparedStatement.executeUpdate();
            System.out.println("Exploration data added to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null);
        return resultSet.next();
    }
}
