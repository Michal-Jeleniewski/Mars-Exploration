package com.codecool.marsexploration.mapexplorer.colonization;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationResultDisplay;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Symbol;
import com.codecool.marsexploration.mapexplorer.repository.*;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverStatus;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.*;

import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.*;
import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.GO_TO_RESOURCE;

public class ColonizationSimulation {

    public static final int MINERALS_NEEDED_FOR_NEW_ROVER = 3;
    public static final int ROVERS_REQUIRED = 5;
    private final ExplorationResultDisplay explorationResultDisplay;
    private final Simulation simulation;
    private final ConfigurationParameters configurationParameters;
    private final Random random;
    private final MoveToCoordinateService moveToCoordinateService;
    private final ColonizationRepository colonizationRepository;
    private final CommandCenterRepository commandCenterRepository;
    private final RoversRepository roversRepository;
    private final UUID id;

    public ColonizationSimulation(ExplorationResultDisplay explorationResultDisplay, Simulation simulation, ConfigurationParameters configurationParameters, MoveToCoordinateService moveToCoordinateService, ColonizationRepository colonizationRepository, CommandCenterRepository commandCenterRepository, RoversRepository roversRepository) {
        this.explorationResultDisplay = explorationResultDisplay;
        this.simulation = simulation;
        this.configurationParameters = configurationParameters;
        this.moveToCoordinateService = moveToCoordinateService;
        this.colonizationRepository = colonizationRepository;
        this.commandCenterRepository = commandCenterRepository;
        this.roversRepository = roversRepository;
        this.id = UUID.randomUUID();
        this.random = new Random();
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
                    case GO_TO_RESOURCE -> {
                        List<Coordinate> randomMineralAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(rover.getDestination(), simulation.getMap().getDimension());
                        if (randomMineralAdjacentCoordinates.contains(rover.getPosition())) {
                            rover.setRoverStatus(EXTRACT);
                        } else {
                            moveToCoordinateService.moveToCoordinate(rover.getDestination(), rover);
                            configurationParameters.symbols().forEach(rover::checkForObjectsAround);
                            rover.addScannedCoordinates();
                        }
                    }
                    case EXTRACT -> {
                        extractMineral(rover, rover.getDestination());
                        if (simulation.getCommandCenter() == null) {
                            rover.setRoverStatus(BUILD_BASE);
                        } else {
                            rover.setRoverStatus(GO_TO_BASE);
                        }
                    }
                    case BUILD_BASE -> {
                        simulation.setCommandCenter(new CommandCenter(getNewCommandCenterCoordinate(rover), rover.getMineralPoints(), rover.getObjectsPoints(), rover.getScannedCoordinates()));
                        rover.clearInventory();
                        rover.saveObjectPoint(simulation.getCommandCenter().getCommandCenterPosition(), Symbol.BASE.getSymbol());
                        rover.setRoverStatus(GO_TO_RESOURCE);
                    }
                    case GO_TO_BASE -> {
                        List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());
                        configurationParameters.symbols().forEach(rover::checkForObjectsAround);
                        rover.addScannedCoordinates();
                        if (baseAdjacentCoordinates.contains(rover.getPosition())) {
                            rover.setRoverStatus(DEPOSIT_RESOURCE);
                        } else {
                            moveToCoordinateService.moveToCoordinate(simulation.getCommandCenter().getCommandCenterPosition(), rover);
                        }
                    }
                    case DEPOSIT_RESOURCE -> {
                        simulation.getCommandCenter().addMineral();
                        setupRoverToExtract(rover);
                    }
                }

            });

            explorationResultDisplay.displayExploredMap(simulation);
            simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);

            if (isPossibleToBuildNewRover()) {
                Rover newRover = createNewRover();
                simulation.getCommandCenter().decreaseMineralStock(MINERALS_NEEDED_FOR_NEW_ROVER);
                setupNewRower(newRover);
                simulation.addRover(newRover);
            }

            if (colonizationEndCondition(rovers)) {
                isRunning = false;
                manageDatabase();
            }
        }
    }

    private static void setupRoverToExtract(Rover rover) {
        rover.clearInventory();
        rover.setRoverStatus(GO_TO_RESOURCE);
        Coordinate randomMineralPoint = rover.getMineralPoints().get(new Random().nextInt(rover.getMineralPoints().size() - 1));
        rover.setDestination(randomMineralPoint);
    }

    private boolean colonizationEndCondition(List<Rover> rovers) {
        return simulation.getCommandCenter() != null && rovers.size() >= ROVERS_REQUIRED;
    }

    public void manageDatabase() {
        roversRepository.setRoversDto(new RoversDto(id, simulation.getRovers()));
        roversRepository.saveInDatabase();
        colonizationRepository.setColonizationDto(new ColonizationDto(id, List.of(simulation.getCommandCenter())));
        colonizationRepository.saveInDatabase();
        commandCenterRepository.setCommandCenterDto(new CommandCenterDto(id, List.of(simulation.getCommandCenter()), MINERALS_NEEDED_FOR_NEW_ROVER));
        commandCenterRepository.saveInDatabase();

    }

    private boolean isPossibleToBuildNewRover() {
        return simulation.getCommandCenter() != null && simulation.getCommandCenter().getMineralsOnStock() >= MINERALS_NEEDED_FOR_NEW_ROVER;
    }

    private void setupNewRower(Rover newRover) {
        newRover.setRoverStatus(GO_TO_RESOURCE);
        newRover.setMineralPoints(simulation.getCommandCenter().getMineralPoints());
        newRover.setScannedCoordinates(simulation.getCommandCenter().getScannedCoordinates());
        newRover.setObjectsPoints(simulation.getCommandCenter().getObjectsPoints());
        Coordinate randomMineralPoint = newRover.getMineralPoints().get(new Random().nextInt(newRover.getMineralPoints().size() - 1));
        newRover.setDestination(randomMineralPoint);
    }

    private Rover createNewRover() {
        Coordinate newRoverCoordinate = getNewRoverCoordinate();
        simulation.getCommandCenter().updateCreatedRovers();
        return new Rover(newRoverCoordinate, 2, simulation.getMap());
    }

    private Coordinate getNewRoverCoordinate() {
        List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());
        List<Coordinate> baseFreeAdjacentCoordinates = baseAdjacentCoordinates.stream()
                .filter(coordinate -> simulation.getMap().isEmpty(coordinate))
                .toList();
        return baseFreeAdjacentCoordinates.get(random.nextInt(baseFreeAdjacentCoordinates.size()));
    }

    private Coordinate getNewCommandCenterCoordinate(Rover rover) {
        List<Coordinate> roverAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(rover.getPosition(), simulation.getMap().getDimension());
        List<Coordinate> freeRoverAdjacentCoordinates = roverAdjacentCoordinates.stream().
                filter(simulation.getMap()::isEmpty)
                .toList();
        return freeRoverAdjacentCoordinates.get(new Random().nextInt(freeRoverAdjacentCoordinates.size()));
    }

    private void prepareFirstRover(Rover rover) {
        rover.setRoverStatus(RoverStatus.GO_TO_RESOURCE);
        rover.createMineralPoints();
        Coordinate randomMineralPoint = rover.getMineralPoints().get(random.nextInt(rover.getMineralPoints().size() - 1));
        rover.setDestination(randomMineralPoint);
    }

    private void extractMineral(Rover rover, Coordinate randomMineralPoint) {
        rover.addToResourceInventory(randomMineralPoint);
    }
}
