package com.example.slim_walking;

import java.util.ArrayList;

public class Route {
    ArrayList<String> path;

    public Route() {
        path = new ArrayList<>();
    }

    public void addNode(String node) {
        path.add(node);
    }

    public String toString() {
        return path.toString();
    }

}
