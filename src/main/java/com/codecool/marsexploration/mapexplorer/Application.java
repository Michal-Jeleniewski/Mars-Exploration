package com.codecool.marsexploration.mapexplorer;


import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.logger.LoggerImpl;
=======
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;

public class Application {
    private static final String workDir = "src/main";

    public static void main(String[] args) {
        String mapFile = workDir + "/resources/exploration-1.map";
        Coordinate landingSpot = new Coordinate(6, 6);

        Logger logger = new LoggerImpl();
        logger.clearFile();

        MapLoader mapLoader = new MapLoaderImpl();

        Map map = mapLoader.load(mapFile);

        System.out.println(map);


        // Add your code here
    }
}

