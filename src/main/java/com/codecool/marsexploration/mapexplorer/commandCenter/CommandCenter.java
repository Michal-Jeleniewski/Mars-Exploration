package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class CommandCenter {

private String id;

private Coordinate commandCenterCoordinates;

private BaseStatus baseStatus;

    private Map<String , Integer> resourcesOnStock;

    public CommandCenter(String id, Coordinate commandCenterCoordinates) {
        this.id = id;
        this.commandCenterCoordinates = commandCenterCoordinates;
        this.baseStatus = null;
        this.resourcesOnStock = new HashMap<>();
    }



}
