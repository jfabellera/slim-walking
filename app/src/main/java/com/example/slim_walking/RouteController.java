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
    String destination;
    String currLocation;
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

    public void dijkstra(int src) {
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
        determinePath(30, parents);
        System.out.println(route);
    }

    private void determinePath(int i, int[] parents) {
        if(i == -1)
            return;
        determinePath(parents[i], parents);
        route.addNode(nodes[i]);
    }

}
