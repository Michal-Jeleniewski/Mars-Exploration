package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;

import java.util.Random;

public class RoverPlacement {
    private final Map map;

    public RoverPlacement(Map map) {
        this.map = map;
    }

    public Coordinate generateRandomCoordinateForRover() {
        int randomX = generateRandomNumber();
        int randomY = generateRandomNumber();

        Coordinate coordinate = new Coordinate(randomX, randomY);

        while (!map.isEmpty(coordinate)){
            randomX = generateRandomNumber();
            randomY = generateRandomNumber();
            coordinate = new Coordinate(randomX, randomY);
        }

        return coordinate;
    }

    private int generateRandomNumber() {
        Random random = new Random();
        int dimensionOfMap = map.getDimension();
        int MIN = 0;
        return random.nextInt(dimensionOfMap - MIN) + MIN;
    }

}
