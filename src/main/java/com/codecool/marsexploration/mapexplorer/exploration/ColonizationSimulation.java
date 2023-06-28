package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Symbol;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverStatus;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.List;
import java.util.Random;

import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.*;
import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.GO_TO_RESOURCE;

public class ColonizationSimulation {

    private final ExplorationResultDisplay explorationResultDisplay;
    private final Simulation simulation;
    private final ConfigurationParameters configurationParameters;
    private final Random random;

    public ColonizationSimulation(ExplorationResultDisplay explorationResultDisplay, Simulation simulation, ConfigurationParameters configurationParameters) {
        this.explorationResultDisplay = explorationResultDisplay;
        this.simulation = simulation;
        this.configurationParameters = configurationParameters;
        this.random = new Random();
    }

    private void prepareFirstRover(Rover rover) {
        rover.setRoverStatus(RoverStatus.GO_TO_RESOURCE);
        rover.createMineralPoints();
        Coordinate randomMineralPoint = rover.getMineralPoints().get(random.nextInt(rover.getMineralPoints().size() - 1));
        rover.setDestination(randomMineralPoint);
    }

    public void runColonization() {
        boolean isRunning = true;
        List<Rover> rovers = simulation.getRovers();
        prepareFirstRover(rovers.get(0));
        while (isRunning) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rovers.forEach(rover -> {
                switch (rover.getRoverStatus()) {
                    case GO_TO_RESOURCE:

                        List<Coordinate> randomMineralAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(rover.getDestination(), simulation.getMap().getDimension());

                        if (randomMineralAdjacentCoordinates.contains(rover.getPosition())) {
                            rover.setRoverStatus(EXTRACT);
                        } else {
                            moveToCoordinate(rover.getDestination(), rover);
                            configurationParameters.symbols().forEach(rover::checkForObjectsAround);
                            rover.addScannedCoordinates();
                        }

                        simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);
                        explorationResultDisplay.displayExploredMap(simulation);
                        break;

                    case EXTRACT:

                        extractMineral(rover, rover.getDestination());
                        if (simulation.getCommandCenter() == null) {
                            rover.setRoverStatus(BUILD_BASE);
                        } else {
                            rover.setRoverStatus(GO_TO_BASE);
                        }
                        explorationResultDisplay.displayExploredMap(simulation);
                        break;

                    case BUILD_BASE:

                        rover.clearInventory();
                        List<Coordinate> roverAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(rover.getPosition(), simulation.getMap().getDimension());
                        List<Coordinate> freeRoverAdjacentCoordinates = roverAdjacentCoordinates.stream().
                                filter(simulation.getMap()::isEmpty)
                                .toList();

                        CommandCenter commandCenter = new CommandCenter(freeRoverAdjacentCoordinates.get(new Random().nextInt(freeRoverAdjacentCoordinates.size())), rover.getMineralPoints(), rover.getObjectsPoints(), rover.getScannedCoordinates());
                        simulation.setCommandCenter(commandCenter);
                        rover.saveObjectPoint(simulation.getCommandCenter().getCommandCenterPosition(), Symbol.BASE.getSymbol());
                        rover.setRoverStatus(GO_TO_RESOURCE);
                        explorationResultDisplay.displayExploredMap(simulation);
                        break;

                    case GO_TO_BASE:
                        List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());
                        configurationParameters.symbols().forEach(rover::checkForObjectsAround);
                        rover.addScannedCoordinates();
                        if (baseAdjacentCoordinates.contains(rover.getPosition())) {
                            rover.setRoverStatus(DEPOSIT_RESOURCE);
                        } else {
                            moveToCoordinate(simulation.getCommandCenter().getCommandCenterPosition(), rover);
                        }
                        explorationResultDisplay.displayExploredMap(simulation);
                        break;

                    case DEPOSIT_RESOURCE:
                        rover.clearInventory();
                        simulation.getCommandCenter().addMineral();
                        rover.setRoverStatus(GO_TO_RESOURCE);
                        Coordinate randomMineralPoint = rover.getMineralPoints().get(new Random().nextInt(rover.getMineralPoints().size() - 1));
                        rover.setDestination(randomMineralPoint);
                        explorationResultDisplay.displayExploredMap(simulation);
                        break;
                }
            });
            if (simulation.getCommandCenter() != null && simulation.getCommandCenter().getMineralsOnStock() >= 3) {
                List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());
                List<Coordinate> baseFreeAdjacentCoordinates = baseAdjacentCoordinates.stream()
                        .filter(coordinate -> simulation.getMap().isEmpty(coordinate))
                        .toList();

                Rover newRover = new Rover(baseFreeAdjacentCoordinates.get(0), 2, simulation.getMap());
                simulation.getCommandCenter().decreaseMineralStock(3);
                newRover.setRoverStatus(GO_TO_RESOURCE);
                newRover.setMineralPoints(simulation.getCommandCenter().getMineralPoints());
                newRover.setScannedCoordinates(simulation.getCommandCenter().getScannedCoordinates());
                newRover.setObjectsPoints(simulation.getCommandCenter().getObjectsPoints());
                Coordinate randomMineralPoint = newRover.getMineralPoints().get(new Random().nextInt(newRover.getMineralPoints().size() - 1));
                newRover.setDestination(randomMineralPoint);
                simulation.addRover(newRover);
            }


            if (simulation.getCommandCenter() != null && rovers.size() >= 5) {
                isRunning = false;
            }
        }
    }

    private void extractMineral(Rover rover, Coordinate randomMineralPoint) {
        rover.addToResourceInventory(randomMineralPoint);
    }

    private void moveToCoordinate(Coordinate destination, Rover rover) {
        Coordinate roverPosition = rover.getPosition();
        int X = roverPosition.X();
        int Y = roverPosition.Y();
        if (roverPosition.X() > destination.X()) {
            X -= 1;
        } else if (roverPosition.X() < destination.X()) {
            X += 1;
        } else if (roverPosition.Y() > destination.Y()) {
            Y -= 1;
        } else if (roverPosition.Y() < destination.Y()) {
            Y += 1;
        }
        Coordinate newPosition = new Coordinate(X, Y);
        rover.setPosition(newPosition);
    }
}
