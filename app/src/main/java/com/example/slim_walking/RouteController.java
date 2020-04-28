package com.example.slim_walking;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class RouteController {
    String[] nodes;
    double[][] adjacencyMat;
    double[][] angleMat;
    Context context;
    static String destination;
    static String currLocation;
    Route route;
    int numNodes;


    public RouteController(Context current) {
        this.context = current;
        route = new Route();
        int i = 0;
        boolean first = true;
        InputStream is_adj = context.getResources().openRawResource(R.raw.adjacencymatrix);
        InputStream is_ang = context.getResources().openRawResource(R.raw.anglematrix);
        BufferedReader reader_adj = new BufferedReader(new InputStreamReader(is_adj, Charset.forName("UTF-8")));
        BufferedReader reader_ang = new BufferedReader(new InputStreamReader(is_ang, Charset.forName("UTF-8")));
        String line_adj, line_ang;
        try {
            while( (line_adj = reader_adj.readLine()) != null && (line_ang = reader_ang.readLine()) != null) {
                // tokenize
                ArrayList<String> tokens_adj = new ArrayList<>(Arrays.asList(line_adj.split(",")));
                ArrayList<String> tokens_ang = new ArrayList<>(Arrays.asList(line_ang.split(",")));
                tokens_adj.remove(0);
                tokens_ang.remove(0);
                if(first) {
                    nodes = tokens_adj.toArray(new String[tokens_adj.size()]);
                    numNodes = nodes.length;
                    adjacencyMat = new double[numNodes][numNodes];
                    angleMat = new double[numNodes][numNodes];
                    first = false;
                } else {
                    for(int j = 0; j < tokens_adj.size(); j++) {
                        adjacencyMat[i][j] = Double.valueOf(tokens_adj.get(j));
                        angleMat[i][j] = Double.valueOf(tokens_ang.get(j));
                    }
                    i++;
                }
            }
        } catch (IOException e) {
            Log.wtf("calculating", "Error reading csv", e);
            e.printStackTrace();
        }
    }

    public void calculateRoute() {
        double smallestTotalDistance = Double.MAX_VALUE;
        int smallestDstIndex = -1;
        boolean multi = false;
        char multiExt = 'A';
        int src, dst;
        do {
            src = getIndex(currLocation);
            dst = getIndex(destination);
            if(dst < 0) {
                // multiple points exist for this node (input validation already done in calculating.java)
                dst = getIndex(destination+multiExt);
                multiExt++;
                if(dst < 0) //(again)
                    break;
                else
                    multi = true;
            }
            dijkstra(src, dst);
            if(route.totalDistance < smallestTotalDistance) {
                smallestTotalDistance = route.totalDistance;
                smallestDstIndex = dst;
            }
        } while(multi);
        dijkstra(src, smallestDstIndex);
        System.out.println(route);
    }

    private void dijkstra(int src, int dst) {
        route = new Route();
        double dist[] = new double[numNodes];
        Boolean sptSet[] = new Boolean[numNodes];

        for(int i = 0; i < numNodes; i++) {
            dist[i] = Double.MAX_VALUE;
            sptSet[i] = false;
        }

        dist[src] = 0;
        int[] parents = new int[numNodes];
        parents[src] = -1;

        for(int i = 1; i < numNodes; i++) {
            int nearestVertex = -1;
            double shortestDistance = Double.MAX_VALUE;
            for(int j = 0; j < numNodes; j++) {
                if(!sptSet[j] && dist[j] < shortestDistance) {
                    nearestVertex = j;
                    shortestDistance = dist[j];
                }
            }
            sptSet[nearestVertex] = true;
            for (int j = 0; j < numNodes; j++) {
                double edgeDistance = adjacencyMat[nearestVertex][j];
                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < dist[j])) {
                    parents[j] = nearestVertex;
                    dist[j] = shortestDistance + edgeDistance;
                }
            }
        }
        determinePath(dst, parents);
    }

    private void determinePath(int i, int[] parents) {
        if(i == -1)
            return;
        determinePath(parents[i], parents);
        int j;
        if(parents[i] == -1)
            j = i;
        else
            j = parents[i];
        route.addNode(nodes[i], adjacencyMat[j][i], angleMat[j][i]);
    }

    public static void setDestination(String d) {
        destination = d;
    }

    public static void setCurrLocation(String c) {
        if(Character.isLetter(c.charAt(c.length()-1)))
            c = c.substring(0, c.length() - 1).toLowerCase() + Character.toUpperCase(c.charAt(c.length()-1));
        currLocation = c;
    }

    public void debug() {
        System.out.println(destination + " " + currLocation);
    }

    private int getIndex(String nodeName) {
        for(int i = 0; i < numNodes; i++) {
            if(nodes[i].equals(nodeName))
                return i;
        }
        return -1;
    }
}
