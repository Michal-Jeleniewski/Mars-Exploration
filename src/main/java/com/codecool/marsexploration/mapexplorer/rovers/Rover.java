package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rover {
    private final List<Coordinate> previousPositions;
    private final String id;
    private final java.util.Map<String, List<Coordinate>> resourcesPoints;
    private final Map map;
    private final int sightRange;
    private Coordinate position;


    public Rover(String id, Coordinate position, int sightRange, Map map) {
        this.id = id;
        this.position = position;
        this.sightRange = sightRange;
        this.resourcesPoints = new HashMap<>();
        this.map = map;
        previousPositions = new ArrayList<>();
    }

    public java.util.Map<String, List<Coordinate>> getResourcesPoints() {
        return resourcesPoints;
    }

    public void checkForResourcesAround(String resource) {
        List<Coordinate> coordinatesToCheck = CoordinateCalculatorService.getCoordinatesAround(position, sightRange);

        coordinatesToCheck.forEach(coordinate -> {
            if (map.getByCoordinate(coordinate).equals(resource)) {
                saveResourcePoint(coordinate, resource);
            }
        });
    }

    public void saveResourcePoint(Coordinate coordinate, String resource) {
        List<Coordinate> coordinateList = resourcesPoints.get(resource);
        coordinateList.add(coordinate);
        resourcesPoints.put(resource, coordinateList);
    }

    public void addToPreviousPositionsList(Coordinate coordinate) {
        previousPositions.add(coordinate);
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }
}
