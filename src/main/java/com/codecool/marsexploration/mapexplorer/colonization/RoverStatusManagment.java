package com.codecool.marsexploration.mapexplorer.colonization;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Symbol;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.List;
import java.util.Random;

import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.*;

public class RoverStatusManagment {

    private final Rover rover;
    private final MoveToCoordinateService moveToCoordinateService;
    private final ConfigurationParameters configurationParameters;
    private final Simulation simulation;

    public RoverStatusManagment(Rover rover,
                                MoveToCoordinateService moveToCoordinateService,
                                ConfigurationParameters configurationParameters,
                                Simulation simulation) {
        this.rover = rover;
        this.moveToCoordinateService = moveToCoordinateService;
        this.configurationParameters = configurationParameters;
        this.simulation = simulation;
    }

    private static void setupRoverToExtract(Rover rover) {
        rover.clearInventory();
        rover.setRoverStatus(GO_TO_RESOURCE);
    }

    public void goToResource() {
        List<Coordinate> randomMineralAdjacentCoordinates = CoordinateCalculatorService
                .getAdjacentCoordinates(rover.getDestination(), simulation.getMap().getDimension());

        if (randomMineralAdjacentCoordinates.contains(rover.getPosition())) {
            rover.setRoverStatus(EXTRACT);
        } else {
            moveToCoordinateService.moveToCoordinate(rover.getDestination(), rover);
            configurationParameters.symbols().forEach(rover::checkForObjectsAround);
            rover.addScannedCoordinates();
        }
    }

    public void extract() {
        extractMineral(rover, rover.getDestination());
        if (simulation.getCommandCenter() == null) {
            rover.setRoverStatus(BUILD_BASE);
        } else {
            rover.setRoverStatus(GO_TO_BASE);
        }
    }

    public void buildBase() {
        Coordinate newBaseCoordinate = rover.findBestPositionForCommandCenter();
        System.out.println(newBaseCoordinate);
        rover.setDestination(newBaseCoordinate);
        if (CoordinateCalculatorService.getAdjacentCoordinates(newBaseCoordinate, simulation.getMap().getDimension()).contains(rover.getPosition())) {
            simulation.setCommandCenter(new CommandCenter(newBaseCoordinate, rover.getMineralPoints(), rover.getObjectsPoints(), rover.getScannedCoordinates()));
            rover.clearInventory();
            rover.saveObjectPoint(simulation.getCommandCenter().getCommandCenterPosition(), Symbol.BASE.getSymbol());
            Coordinate randomMineralPoint = rover.getMineralPoints().get(new Random().nextInt(rover.getMineralPoints().size()));
            simulation.getCommandCenter().getMineralPoints().remove(randomMineralPoint);
            rover.setDestination(randomMineralPoint);
            rover.setRoverStatus(GO_TO_RESOURCE);
        }
        else {
            moveToCoordinateService.moveToCoordinate(newBaseCoordinate, rover);
        }

    }

    public void goToBase() {
        List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService
                .getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());

        configurationParameters.symbols().forEach(rover::checkForObjectsAround);
        rover.addScannedCoordinates();
        if (baseAdjacentCoordinates.contains(rover.getPosition())) {
            rover.setRoverStatus(DEPOSIT_RESOURCE);
        } else {
            moveToCoordinateService.moveToCoordinate(simulation.getCommandCenter().getCommandCenterPosition(), rover);
        }
    }

    public void depositResource() {
        simulation.getCommandCenter().addMineral();
        setupRoverToExtract(rover);
    }

    private void extractMineral(Rover rover, Coordinate randomMineralPoint) {
        rover.addToResourceInventory(randomMineralPoint);
    }

    private Coordinate getNewCommandCenterCoordinate(Rover rover) {
        List<Coordinate> roverAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(rover.getPosition(), simulation.getMap().getDimension());
        List<Coordinate> freeRoverAdjacentCoordinates = roverAdjacentCoordinates.stream().
                filter(simulation.getMap()::isEmpty)
                .toList();
        return freeRoverAdjacentCoordinates.get(new Random().nextInt(freeRoverAdjacentCoordinates.size()));
    }
}
