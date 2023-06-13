package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Rover {
    String id;
    Coordinate position;
    int sightRange;
    List<Coordinate> resourcesPoints;

    public List<Coordinate> getResourcesPoints() {
        return resourcesPoints;
    }

    public Rover(String id, Coordinate position, int sightRange, List<Coordinate> resourcesPoints) {
        this.id = id;
        this.position = position;
        this.sightRange = sightRange;
        this.resourcesPoints = resourcesPoints;
    }

    public void saveResourcePoint(Coordinate coordinate) {
        resourcesPoints.add(coordinate);
    }

//    public void checkForResourcesAround(Resource resource) {
//        // brakuje metody czytającej, co znajduje się na danym koordynacie, więc metoda jest niekompletna
//        for (int linearSight = 0; linearSight < sightRange; linearSight++) {
//            int diagonalSight = sightRange - linearSight;
//            List<Coordinate> coordinatesToCheck = new ArrayList<>();
//            coordinatesToCheck.add(new Coordinate(position.X() + linearSight, position.Y()));
//            coordinatesToCheck.add(new Coordinate(position.X(), position.Y() + linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() - linearSight, position.Y()));
//            coordinatesToCheck.add(new Coordinate(position.X(), position.Y() - linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() + diagonalSight, position.Y() + linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() - diagonalSight, position.Y() + linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() + diagonalSight, position.Y() - linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() - diagonalSight, position.Y() - linearSight));
//            coordinatesToCheck.add(new Coordinate(position.X() + linearSight, position.Y() + diagonalSight));
//            coordinatesToCheck.add(new Coordinate(position.X() - linearSight, position.Y() + diagonalSight));
//            coordinatesToCheck.add(new Coordinate(position.X() + linearSight, position.Y() - diagonalSight));
//            coordinatesToCheck.add(new Coordinate(position.X() - linearSight, position.Y() - diagonalSight));
//
//            coordinatesToCheck.forEach(coordinate -> {
//                if (metodaSprawdzajaca(coordinate) == resource) {
//                    resourcesPoints.add(coordinate);
//                }
//            });
//        }
//    }
}
