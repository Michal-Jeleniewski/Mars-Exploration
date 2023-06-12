package com.codecool.marsexploration.mapexplorer;

import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.logger.LoggerImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

public class Application {
    private static final String workDir = "src/main";

    public static void main(String[] args) {
        String mapFile = workDir + "/resources/exploration-0.map";
        Coordinate landingSpot = new Coordinate(6, 6);
        Logger logger = new LoggerImpl();
        logger.clearFile();
        // Add your code here
    }
}

