package com.codecool.marsexploration.mapexplorer;


import com.codecool.marsexploration.mapexplorer.configuration.*;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.logger.LoggerImpl;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;

import java.util.List;
import java.util.Set;

public class Application {
    private static final String workDir = "src/main";

    public static void main(String[] args) {
        String mapFile = workDir + "/resources/exploration-1.map";
        Coordinate landingSpot = new Coordinate(6, 6);

        Logger logger = new LoggerImpl();
        logger.clearFile();

        ConfigurationParameters configurationParameters = new ConfigurationParameters(mapFile, landingSpot, List.of("%", "*"), 1000);

        MapLoader mapLoader = new MapLoaderImpl();

        Map map = mapLoader.load(mapFile);

        Set<Validator> validators = Set.of(new EmptyLandingSpotValidator(), new FilePathValidator(), new AdjacentCoordinateValidator(), new ResourcesValidator(), new TimeoutValidator());

        ConfigurationValidator configurationValidator = new ConfigurationValidator(map, validators);

    }
}

