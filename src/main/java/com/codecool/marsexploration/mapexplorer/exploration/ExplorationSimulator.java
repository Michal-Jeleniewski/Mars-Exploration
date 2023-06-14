package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;

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
                simulation.setExplorationOutcome(explorationOutcome);
            }

            simulationStepsLogging.logSteps();

            simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);
        }

        explorationResultDisplay.displayExploredMap(rover);

        // IN LOOP

        //Movement. The rover needs to determine an adjacent empty spot of the chart to move

        // Scanning. The rover needs to scan the area for resources based on how far it can see (its sight).

        // Analysis. After the information is gathered, you need to determine whether an outcome is reached.

        // Log. Write the current state of events in the simulation to the log file.

        // Step increment. Increment the context step variable by one.


    }
}
