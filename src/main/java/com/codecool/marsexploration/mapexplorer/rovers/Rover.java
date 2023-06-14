package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rover {
    String id;
    Coordinate position;
    int sightRange;
    Map<String, List<Coordinate>> resourcesPoints;
    com.codecool.marsexploration.mapexplorer.maploader.model.Map map;
    private  final List<Coordinate> previousPositions;



    public Rover(String id, Coordinate position, int sightRange, Map<String, List<Coordinate>> resourcesPoints, Map map) {

        this.id = id;
        this.position = position;
        this.sightRange = sightRange;
        this.resourcesPoints = resourcesPoints;
        this.map = (com.codecool.marsexploration.mapexplorer.maploader.model.Map) map;
        previousPositions = new ArrayList<>();
    }
  
  
    public List<Coordinate> getResourcesPoints() {
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
        resourcesPoints.put(resource, coordinateList);;
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
