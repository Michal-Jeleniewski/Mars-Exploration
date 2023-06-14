package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;

import java.util.Arrays;
import java.util.Set;

public class ExplorationResultDisplay {
    private final int mapDimension;

    public ExplorationResultDisplay(int mapDimension) {
        this.mapDimension = mapDimension;
    }

    public void displayExploredMap(Rover rover) {
        String[][] representation = new String[mapDimension][mapDimension];

        fillWithX(representation);

        revealScannedCoordinates(rover, representation);

        markDiscoveredObjects(rover, representation);

        markRover(rover, representation);

        Map map = new Map(representation, true);

        System.out.println(map);
    }

    private void fillWithX(String[][] representation) {
        for (int i = 0; i < mapDimension; i++) {
            Arrays.fill(representation[i], " X ");
        }
    }

    private void revealScannedCoordinates(Rover rover, String[][] representation) {
        Set<Coordinate> scannedCoordinates = rover.getScannedCoordinates();
        for (Coordinate coordinate : scannedCoordinates) {
            representation[coordinate.X()][coordinate.Y()] = "   ";
        }
    }

    private void markDiscoveredObjects(Rover rover, String[][] representation) {
        java.util.Map<String, Set<Coordinate>> objectsPoints = rover.getObjectsPoints();
        for (String objectString : objectsPoints.keySet()) {
            Set<Coordinate> objectCoordinates = objectsPoints.get(objectString);
            for (Coordinate coordinate : objectCoordinates) {
                representation[coordinate.X()][coordinate.Y()] = " " + objectString + " ";
            }
        }
    }

    private void markRover(Rover rover, String[][] representation) {
        Coordinate roverPosition = rover.getPosition();
        representation[roverPosition.X()][roverPosition.Y()] = " O ";
    }
}
