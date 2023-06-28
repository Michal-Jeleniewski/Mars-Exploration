package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.*;

import static com.codecool.marsexploration.mapexplorer.maploader.model.Symbol.MINERAL;

public class Rover {
    private final List<Coordinate> previousPositions;
    private static int numberOfRovers = 1;
    private final String id;
    private java.util.Map<String, Set<Coordinate>> objectsPoints;
    private final Map map;
    private final int sightRange;
    private Set<Coordinate> scannedCoordinates;
    private Coordinate position;
    private List<Coordinate> mineralPoints;
    private RoverStatus roverStatus;
    private String[] resourceInventory = new String[1];
    private Coordinate destination;


    public Rover(Coordinate position, int sightRange, Map map) {
        this.id = "rover-" + numberOfRovers;
        this.position = position;
        this.sightRange = sightRange;
        this.objectsPoints = new HashMap<>();
        this.map = map;
        previousPositions = new ArrayList<>();
        scannedCoordinates = new HashSet<>();
        mineralPoints = null;
        roverStatus = RoverStatus.EXPLORE;
        numberOfRovers++;
    }



    public List<Coordinate> getPreviousPositions() {
        return previousPositions;
    }

    public void setRoverStatus(RoverStatus roverStatus) {
        this.roverStatus = roverStatus;
    }

    public RoverStatus getRoverStatus() {
        return roverStatus;
    }

    public Set<Coordinate> getScannedCoordinates() {
        return scannedCoordinates;
    }

    public void setScannedCoordinates(Set<Coordinate> scannedCoordinates) {
        this.scannedCoordinates = scannedCoordinates;
    }

    public java.util.Map<String, Set<Coordinate>> getObjectsPoints() {
        return objectsPoints;
    }

    public void setObjectsPoints(java.util.Map<String, Set<Coordinate>> objectsPoints) {
        this.objectsPoints = objectsPoints;
    }

    public void addScannedCoordinates() {
        List<Coordinate> coordinatesToAdd = CoordinateCalculatorService.getCoordinatesAround(position, sightRange, map.getDimension());
        scannedCoordinates.addAll(coordinatesToAdd);
    }

    public void checkForObjectsAround(String resource) {
        List<Coordinate> coordinatesToCheck = CoordinateCalculatorService.getCoordinatesAround(position, sightRange, map.getDimension());
        scannedCoordinates.addAll(coordinatesToCheck);
        coordinatesToCheck.forEach(coordinate -> {
            if (map.getByCoordinate(coordinate).equals(resource)) {
                saveObjectPoint(coordinate, resource);
            }
        });
    }

    public void saveObjectPoint(Coordinate coordinate, String resource) {
        Set<Coordinate> coordinateList;
        if (objectsPoints.containsKey(resource)) {
            coordinateList = objectsPoints.get(resource);
        } else {
            coordinateList = new HashSet<>() {
            };
        }
        coordinateList.add(coordinate);
        objectsPoints.put(resource, coordinateList);
    }

    public void addToPreviousPositionsList(Coordinate coordinate) {
        previousPositions.add(coordinate);
    }

    public void createMineralPoints() {
        mineralPoints = objectsPoints.get(MINERAL.getSymbol()).stream().toList();
    }

    public List<Coordinate> getMineralPoints() {
        return mineralPoints;
    }

    public void setMineralPoints(List<Coordinate> mineralPoints) {
        this.mineralPoints = mineralPoints;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void addToResourceInventory(Coordinate randomMineralPoint) {
        resourceInventory[0] = map.getByCoordinate(randomMineralPoint);
    }

    public String[] getResourceInventory() {
        return resourceInventory;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

    public void clearInventory() {
        resourceInventory = new String[1];
    }
}
