package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;

public class Analysis {

    PgElementSet m_geom;

    public String genus()
    {
        int numOfEdges = m_geom.getNumEdges();
        int numOfVertices = m_geom.getNumVertices();
        int numOfBorders = m_geom.getNumBoundaries();
        // Num of Faces is equal to two thirds of the number of edges, every triangle has 3 edges and every edge is in 2 triangles
        int numOfFaces = 2*(numOfEdges/3);
        double vol = m_geom.getVolume();


        String nums = "\n\n\nNumber of Edges: " + numOfEdges + "\nNumber of Vertices: " + numOfVertices + "\nNumber of Borders: " + numOfBorders + "\nNumber of Faces: " + numOfFaces;
        int genusNumValue = (int) (1 - (float)(numOfVertices + numOfBorders + numOfFaces - numOfEdges)/2);
        String genus = "\nGenus =  " + genusNumValue;

        String volume = "\n\nThe Volume enclosed by the surface is: " + vol;

        return nums + genus + volume;
    }
}
