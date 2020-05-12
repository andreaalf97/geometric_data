package workshop;

import jv.geom.PgElementSet;
import jv.object.PsDebug;

import jvx.project.PjWorkshop;

public class Analysis extends PjWorkshop {

    PgElementSet m_geom;
    int visitedTriangles;
    int[] unvisitedTringles ;

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

    public void genus(int index)
    {
        if(index == 0) {
            int numOfEdges = m_geom.getNumEdges();
            int numOfVertices = m_geom.getNumVertices();
            int numOfBorders = m_geom.getNumBoundaries();
            int numOfFaces = m_geom.getNumBoundaries();
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
            visitedTriangles = 0;
            unvisitedTringles = new int[m_geom.getNumElements()];

            while(visitedTriangles < m_geom.getNumElements()){
                comp++;
                for(int i = 0; i< unvisitedTringles.length; i++){
                    if(unvisitedTringles[i] != -1) {
                        unvisitedTringles[i] = -1;
                        visitedTriangles++;
                        bft(i);
                    }
                }
                PsDebug.message("\nComponents: " + comp);
            }
            PsDebug.message("");
        }
    }

    public void bft(int x){
        PsDebug.message("\nVisited: " + visitedTriangles + " / " + m_geom.getNumElements());
        PsDebug.message("Visited: " + x);
        if(m_geom.getNeighbour(x) == null)
            return;
        for (int j = 0; j < m_geom.getNeighbour(x).m_data.length; j++) {
            if (unvisitedTringles[m_geom.getNeighbour(x).m_data[j]] != -1) {
                unvisitedTringles[m_geom.getNeighbour(x).m_data[j]] = -1;
                visitedTriangles++;
                bft(m_geom.getNeighbour(x).m_data[j]);
                PsDebug.message("pp " + m_geom.getNeighbour(x).m_data[j]);
            }
        }
    }
}