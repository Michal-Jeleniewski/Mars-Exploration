package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class CommandCenter {
private static int numberOfBases = 1;
private final String id;

private final Coordinate commandCenterCoordinates;

private BaseStatus baseStatus;

    private Map<String , Integer> resourcesOnStock;

    public CommandCenter(Coordinate commandCenterCoordinates) {
        this.id = String.valueOf(numberOfBases);
        this.commandCenterCoordinates = commandCenterCoordinates;
        this.baseStatus = null;
        this.resourcesOnStock = new HashMap<>();
        numberOfBases++;
    }



}
