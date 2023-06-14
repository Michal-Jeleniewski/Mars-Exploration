package com.codecool.marsexploration.mapexplorer;


import com.codecool.marsexploration.mapexplorer.analizer.*;
import com.codecool.marsexploration.mapexplorer.configuration.*;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationSimulator;
import com.codecool.marsexploration.mapexplorer.exploration.RandomMovementService;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.logger.LoggerImpl;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverPlacement;

import java.util.List;
import java.util.Set;

public class Application {
    private static final String workDir = "src/main";

    public static void main(String[] args) {
        String mapFile = workDir + "/resources/exploration-1.map";
        Coordinate landingSpot = new Coordinate(6, 6);

        Logger logger = new LoggerImpl();
        logger.clearFile();

        MapLoader mapLoader = new MapLoaderImpl();

        Map map = mapLoader.load(mapFile);

        Set<Validator> validators = Set.of(new EmptyLandingSpotValidator(), new FilePathValidator(), new AdjacentCoordinateValidator(), new ResourcesValidator(), new TimeoutValidator());
        Set<OutcomeAnalyzer> analyzers = Set.of(new SuccessAnalizer(15), new TimeoutAnalizer(), new LackOfResourcesAnalizer(0.7));
        AllOutcomeAnalyzer allOutcomeAnalyzer = new AllOutcomeAnalyzer(analyzers);

        ConfigurationValidator configurationValidator = new ConfigurationValidator(map, validators);

        RoverPlacement roverPlacement = new RoverPlacement(map);


        Coordinate spaceshipLandingPoint = roverPlacement.generateRandomCoordinateForRover();
        List<String> resourcesToMonitor = List.of("%", "&", "*", "#");
        int maxSteps = 1000;

        String roverId = "rover-1";
        int sightRange = 3;
        Rover rover = new Rover(roverId, spaceshipLandingPoint, sightRange, map);

        RandomMovementService randomMovementService = new RandomMovementService(rover, map);

        ConfigurationParameters configurationParameters = new ConfigurationParameters(mapFile, spaceshipLandingPoint, resourcesToMonitor, maxSteps);
        ExplorationSimulator explorationSimulator = new ExplorationSimulator(configurationParameters, mapLoader, configurationValidator, roverPlacement, rover, randomMovementService, allOutcomeAnalyzer);
        explorationSimulator.runSimulation(configurationParameters);
    }
}

