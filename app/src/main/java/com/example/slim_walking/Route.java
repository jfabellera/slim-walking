package com.example.slim_walking;

import java.util.ArrayList;

public class Route {
    ArrayList<String> path;
    ArrayList<Double> distances;
    ArrayList<Double> angles;
    double totalDistance;


    public Route() {
        path = new ArrayList<>();
        distances = new ArrayList<>();
        angles = new ArrayList<>();
        totalDistance = 0;
    }

    public void addNode(String nodeName, double dist, double ang) {
        path.add(nodeName);
        distances.add(dist);
        angles.add(ang);
        totalDistance += dist;
    }

    public String toString() {
        return path.toString() + "\n" + distances.toString() + "\n" + angles.toString();
    }

    public double[] next() {
        if(!path.isEmpty()) {
            path.remove(0);
            distances.remove(0);
            angles.remove(0);
        }
        if(path.isEmpty())
            return new double[] {-1, 360};
        return new double[] {distances.get(0), angles.get(0)};
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

}
