package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.List;

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

        List<Coordinate> adjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(spaceshipCoordinate, map.getDimension());

        for (Coordinate coordinate : adjacentCoordinates) {
            if (map.isEmpty(coordinate)) {
                return coordinate;
            }
        }

        return null;
    }

}
