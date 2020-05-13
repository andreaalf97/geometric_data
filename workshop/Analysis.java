package workshop;

import jv.geom.PgElementSet;
import jv.object.PsDebug;
import java.io.*;
import java.util.*;
import jvx.project.PjWorkshop;

public class Analysis extends PjWorkshop {

    PgElementSet m_geom;
    int numVisitedTriangles;
    boolean[] visitedTriangles;

    /** Constructor */
    public Analysis() {
        super("Surface Registration");
        if (getClass() == Analysis.class) {
            init();
        }
    }

    public void setGeometry(PgElementSet surfP) {
        PsDebug.disposeConsole();
        PsDebug.message("Geometry set");
        m_geom = surfP;
    }

    public void calculations(int index)
    {
        if(index == 0) {
            int numOfEdges = m_geom.getNumEdges();
            int numOfVertices = m_geom.getNumVertices();
            int numOfBorders = m_geom.getNumBoundaries();
            int numOfFaces = m_geom.getNumElements();
            String nums = "\n\n\nNumber of Edges: " + numOfEdges + "\nNumber of Vertices: " + numOfVertices + "\nNumber of Borders: " + numOfBorders + "\nNumber of Faces: " + numOfFaces;

            int genusNumValue = (2 - numOfVertices - numOfBorders - numOfFaces + numOfEdges)/2;
            String genus = "\nGenus =  " + genusNumValue;
            PsDebug.message(nums + genus);
        }
        else if(index == 1) {
            double vol = m_geom.getVolume();
            String volume = "\n\n\nThe Volume enclosed by the surface is: " + vol;
            PsDebug.message(volume);
        }
        else if(index == 2) {
            int comp = 0;
            numVisitedTriangles = 0;
            visitedTriangles = new boolean[m_geom.getNumElements()];

            while(numVisitedTriangles < m_geom.getNumElements()){
                for(int i = 0; i< visitedTriangles.length; i++){
                    if(!visitedTriangles[i]) {
                        comp++;
                        breadthFirstTraversal(i);
                    }
                }
            }
            PsDebug.message("\nComponents: " + comp);
        }
    }

    public void breadthFirstTraversal(int x){
        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // Mark the current node as visited and enqueue it
        visitedTriangles[x] = true;
        numVisitedTriangles++;
        queue.add(x);

        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue and print it
            x = queue.poll();


            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            for (int j = 0; j < m_geom.getNeighbour(x).m_data.length; j++){
                int neighbour = m_geom.getNeighbour(x).m_data[j];
                if(!visitedTriangles[neighbour]) {
                    numVisitedTriangles++;
                    visitedTriangles[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }

    }
}