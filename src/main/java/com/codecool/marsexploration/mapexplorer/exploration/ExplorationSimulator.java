package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.rovers.RoverPlacement;

public class ExplorationSimulator {

  private ConfigurationParameters configurationParameters;
  private MapLoader mapLoader;

  // dodać Configuration validator
  private RoverPlacement roverPlacement;


  public ExplorationSimulator(ConfigurationParameters configurationParameters, MapLoader mapLoader, RoverPlacement roverPlacement) {
    this.configurationParameters = configurationParameters;
    this.mapLoader = mapLoader;
    this.roverPlacement = roverPlacement;
  }


public void runSimulation(){

    // IN LOOP

  //Movement. The rover needs to determine an adjacent empty spot of the chart to move

 // Scanning. The rover needs to scan the area for resources based on how far it can see (its sight).

  // Analysis. After the information is gathered, you need to determine whether an outcome is reached.

  // Log. Write the current state of events in the simulation to the log file.

  // Step increment. Increment the context step variable by one.


}


}
