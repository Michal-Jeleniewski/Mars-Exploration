package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Symbol;
import com.codecool.marsexploration.mapexplorer.repository.ExplorationsDto;
import com.codecool.marsexploration.mapexplorer.repository.ExplorationsRepository;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverStatus;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.codecool.marsexploration.mapexplorer.rovers.RoverStatus.*;

public class ExplorationSimulator {

    private final ExplorationResultDisplay explorationResultDisplay;
    private final MapLoader mapLoader;
    private final AllOutcomeAnalyzer allOutcomeAnalyzer;
    private final Logger logger;
    private final ExplorationsRepository explorationsRepository;
    private MovementService movementService;

    public ExplorationSimulator(ExplorationResultDisplay explorationResultDisplay,
                                MapLoader mapLoader,
                                MovementService movementService,
                                AllOutcomeAnalyzer allOutcomeAnalyzer,
                                Logger logger,
                                ExplorationsRepository explorationsRepository) {
        this.explorationResultDisplay = explorationResultDisplay;
        this.mapLoader = mapLoader;
        this.movementService = movementService;
        this.allOutcomeAnalyzer = allOutcomeAnalyzer;
        this.logger = logger;
        this.explorationsRepository = explorationsRepository;
    }

    public void runSimulation(ConfigurationParameters configurationParameters, List<Rover> rovers) {
        Simulation simulation = new Simulation(
                configurationParameters.maxSteps(),
                rovers,
                configurationParameters.spaceshipLandingPoint(),
                mapLoader.load(configurationParameters.mapPath()),
                configurationParameters.symbols()
        );

        SimulationStepsLogging simulationStepsLogging = new SimulationStepsLogging(simulation, logger, allOutcomeAnalyzer);

        while (simulation.explorationOutcome() == null) {
            movementService.move();

            configurationParameters.symbols().forEach(symbol -> rovers.forEach(rover -> rover.checkForObjectsAround(symbol)));

            rovers.forEach(Rover::addScannedCoordinates);

            ExplorationOutcome explorationOutcome = allOutcomeAnalyzer.analyze(simulation);

            simulationStepsLogging.logSteps();

            if (explorationOutcome != null) {
                int numberOfResources = rovers.get(0).getObjectsPoints().values().stream().mapToInt(Set::size).sum();
                ExplorationsDto explorationsDto = new ExplorationsDto(simulation.numberOfSteps(), numberOfResources, explorationOutcome);
                explorationsRepository.saveInDatabase(explorationsDto);
                simulation.setExplorationOutcome(explorationOutcome);
            }

            simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);
        }

        explorationResultDisplay.displayExploredMap(simulation);

        if (simulation.explorationOutcome() == ExplorationOutcome.COLONIZABLE) {
            simulation.getRovers().get(0).setRoverStatus(RoverStatus.GO_TO_RESOURCE);
            rovers.get(0).createMineralPoints();
            Random random = new Random();
            Coordinate randomMineralPoint = simulation.getRovers().get(0).getMineralPoints().get(random.nextInt(simulation.getRovers().get(0).getMineralPoints().size() - 1));
            rovers.get(0).setDestination(randomMineralPoint);
            runColonization(configurationParameters, simulation);
        }

    }

    private void runColonization(ConfigurationParameters configurationParameters, Simulation simulation) {
        boolean isRunning = true;
        while (isRunning) {

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            simulation.getRovers().forEach(rover -> {
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


            if (simulation.getCommandCenter() != null && simulation.getRovers().size() >= 5) {
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
