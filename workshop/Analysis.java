package workshop;

import jv.geom.PgElementSet;
import jv.object.PsDebug;

import jvx.project.PjWorkshop;

public class Analysis extends PjWorkshop {

    PgElementSet m_geom;
    int removedVertices;
    int[] vertices ;

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
            // Num of Faces is equal to two thirds of the number of edges, every triangle has 3 edges and every edge is in 2 triangles
            int numOfFaces = 2*(numOfEdges/3);
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
            int comp = 1;
            String volume = "\n\n\nThe Volume enclosed by the surface is: " + comp;
            PsDebug.message(volume);
        }
    }
}