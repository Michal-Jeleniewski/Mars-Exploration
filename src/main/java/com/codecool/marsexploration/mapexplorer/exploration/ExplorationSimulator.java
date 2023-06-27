package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.repository.ExplorationsDto;
import com.codecool.marsexploration.mapexplorer.repository.ExplorationsRepository;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverStatus;
import com.codecool.marsexploration.mapexplorer.service.CoordinateCalculatorService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

//            configurationParameters.symbols().forEach(rover::checkForObjectsAround);
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

        explorationResultDisplay.displayExploredMap(rovers.get(0));

        if (simulation.explorationOutcome() == ExplorationOutcome.COLONIZABLE) {
            simulation.getRovers().get(0).setRoverStatus(RoverStatus.GO_TO_RESOURCE);
            runColonization(configurationParameters, simulation);
        }

    }

    private void runColonization(ConfigurationParameters configurationParameters, Simulation simulation) {
        simulation.getRovers().get(0).createMineralPoints();

        Random random = new Random();

        Coordinate randomMineralPoint = simulation.getRovers().get(0).getMineralPoints().get(random.nextInt(simulation.getRovers().get(0).getMineralPoints().size() - 1));
        List<Coordinate> randomMineralAdjacentCoordinates = CoordinateCalculatorService.getAdjacentCoordinates(randomMineralPoint, simulation.map().getDimension());

        while (!randomMineralAdjacentCoordinates.contains(simulation.getRovers().get(0).getPosition())) {
            moveToCoordinate(randomMineralPoint, simulation.getRovers().get(0));
            configurationParameters.symbols().forEach(simulation.getRovers().get(0)::checkForObjectsAround);
            simulation.getRovers().get(0).addScannedCoordinates();
            explorationResultDisplay.displayExploredMap(simulation.getRovers().get(0));
            simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);
        }

        simulation.getRovers().get(0).setRoverStatus(RoverStatus.EXTRACT);
        simulation.setNumberOfSteps(simulation.numberOfSteps() + 1);

        extractMineral(simulation.getRovers().get(0), randomMineralPoint);

        System.out.println(Arrays.toString(simulation.getRovers().get(0).getResourceInventory()));

        simulation.getRovers().get(0).setRoverStatus(RoverStatus.BUILD_BASE);

        // pewnie osobna metoda
        CommandCenter commandCenter = new CommandCenter("1", simulation.getRovers().get(0).getPosition());
        // podmienić symbol na mapie na symbol bazy

        //
//        System.out.println(randomMineralPoint);
//        System.out.println(simulation.getRover().getPosition());

    }

    private void extractMineral(Rover rover, Coordinate randomMineralPoint) {
        rover.addToResourceInventory(randomMineralPoint);
    }

    private void moveToCoordinate(Coordinate randomMineralPoint, Rover rover) {
        Coordinate roverPosition = rover.getPosition();
        int X = roverPosition.X();
        int Y = roverPosition.Y();
        if (roverPosition.X() > randomMineralPoint.X()) {
            X -= 1;
        } else if (roverPosition.X() < randomMineralPoint.X()) {
            X += 1;
        } else if (roverPosition.Y() > randomMineralPoint.Y()) {
            Y -= 1;
        } else if (roverPosition.Y() < randomMineralPoint.Y()) {
            Y += 1;
        }
        Coordinate newPosition = new Coordinate(X, Y);
        rover.setPosition(newPosition);
    }
}
