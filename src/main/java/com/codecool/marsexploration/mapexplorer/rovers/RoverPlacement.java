package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;

import java.util.Random;

public class RoverPlacement {
    private final Map map;
    private final Coordinate spaceshipCoordinate;

    public RoverPlacement(Map map, Coordinate spaceshipCoordinate) {
        this.map = map;
        this.spaceshipCoordinate = spaceshipCoordinate;
    }

    public Coordinate generateCoordinateForRover() {
        Coordinate roverCoordinate = findEmptyNeighboringCoordinate();

        while (roverCoordinate == null) {
            roverCoordinate = findEmptyNeighboringCoordinate();
        }

        return roverCoordinate;
    }

    private Coordinate findEmptyNeighboringCoordinate() {
        int spaceshipX = spaceshipCoordinate.X();
        int spaceshipY = spaceshipCoordinate.Y();

        Coordinate[] neighboringCoordinates = {
                new Coordinate(spaceshipX, spaceshipY + 1),
                new Coordinate(spaceshipX, spaceshipY - 1),
                new Coordinate(spaceshipX + 1, spaceshipY),
                new Coordinate(spaceshipX - 1, spaceshipY)
        };

        for (Coordinate coordinate : neighboringCoordinates) {
            if (map.isEmpty(coordinate)) {
                return coordinate;
            }
        }

        return null;
    }

}
