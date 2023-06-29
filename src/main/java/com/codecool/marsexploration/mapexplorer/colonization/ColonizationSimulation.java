package com.codecool.marsexploration.mapexplorer.colonization;


import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationResultDisplay;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;
import com.codecool.marsexploration.mapexplorer.exploration.SimulationStepsLogging;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverStatus;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.List;
import java.util.Random;

import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.GO_TO_RESOURCE;

public class ColonizationSimulation {

    public static final int MINERALS_NEEDED_FOR_NEW_ROVER = 5;
    public static final int ROVERS_REQUIRED = 5;
    private final ExplorationResultDisplay explorationResultDisplay;
    private final Simulation simulation;
    private final ConfigurationParameters configurationParameters;
    private final Random random;
    private final MoveToCoordinateService moveToCoordinateService;
    private final Logger logger;
    private final AllOutcomeAnalyzer allOutcomeAnalyzer;

    public ColonizationSimulation(ExplorationResultDisplay explorationResultDisplay, Simulation simulation, ConfigurationParameters configurationParameters, MoveToCoordinateService moveToCoordinateService, Logger logger, AllOutcomeAnalyzer allOutcomeAnalyzer) {
        this.explorationResultDisplay = explorationResultDisplay;
        this.simulation = simulation;
        this.configurationParameters = configurationParameters;
        this.moveToCoordinateService = moveToCoordinateService;
        this.logger = logger;
        this.allOutcomeAnalyzer = allOutcomeAnalyzer;
        this.random = new Random();
    }

    public void runColonization() {
        boolean isRunning = true;
        List<Rover> rovers = simulation.getRovers();
        SimulationStepsLogging simulationStepsLogging = new SimulationStepsLogging(simulation, logger, allOutcomeAnalyzer);
        prepareFirstRover(rovers.get(0));
        while (isRunning) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rovers.forEach(rover -> {
                RoverStatusManagment roverStatusManagment = new RoverStatusManagment(rover, moveToCoordinateService, configurationParameters, simulation);
                switch (rover.getRoverStatus()) {
                    case GO_TO_RESOURCE -> roverStatusManagment.goToResource();
                    case EXTRACT -> roverStatusManagment.extract();
                    case BUILD_BASE -> roverStatusManagment.buildBase();
                    case GO_TO_BASE -> roverStatusManagment.goToBase();
                    case DEPOSIT_RESOURCE -> roverStatusManagment.depositResource();
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
            simulationStepsLogging.logSteps();

            if (colonizationEndCondition(rovers)) {
                isRunning = false;
            }
        }
    }



    private boolean colonizationEndCondition(List<Rover> rovers) {
        return simulation.getCommandCenter() != null && rovers.size() >= ROVERS_REQUIRED;
    }

    private boolean isPossibleToBuildNewRover() {
        return simulation.getCommandCenter() != null && simulation.getCommandCenter().getMineralsOnStock() >= MINERALS_NEEDED_FOR_NEW_ROVER;
    }

    private void setupNewRower(Rover newRover) {
        newRover.setRoverStatus(GO_TO_RESOURCE);
        newRover.setMineralPoints(simulation.getCommandCenter().getMineralPoints());
        newRover.setScannedCoordinates(simulation.getCommandCenter().getScannedCoordinates());
        newRover.setObjectsPoints(simulation.getCommandCenter().getObjectsPoints());
        Coordinate randomMineralPoint = newRover.getMineralPoints().get(new Random().nextInt(newRover.getMineralPoints().size()));
        simulation.getCommandCenter().getMineralPoints().remove(randomMineralPoint);
        newRover.setDestination(randomMineralPoint);
    }

    private Rover createNewRover() {
        Coordinate newRoverCoordinate = getNewRoverCoordinate();
        return new Rover(newRoverCoordinate, 2, simulation.getMap());
    }

    private Coordinate getNewRoverCoordinate() {
        List<Coordinate> baseAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(simulation.getCommandCenter().getCommandCenterPosition(), simulation.getMap().getDimension());
        List<Coordinate> baseFreeAdjacentCoordinates = baseAdjacentCoordinates.stream()
                .filter(coordinate -> simulation.getMap().isEmpty(coordinate))
                .toList();
        return baseFreeAdjacentCoordinates.get(random.nextInt(baseFreeAdjacentCoordinates.size()));
    }



    private void prepareFirstRover(Rover rover) {
        rover.setRoverStatus(RoverStatus.GO_TO_RESOURCE);
        rover.createMineralPoints();
        Coordinate randomMineralPoint = rover.getMineralPoints().get(random.nextInt(rover.getMineralPoints().size()));
        rover.setDestination(randomMineralPoint);
    }


}
