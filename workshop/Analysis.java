package workshop;

import java.awt.Color;
import java.util.*;

import jv.geom.PgBndPolygon;
import jv.geom.PgElementSet;
import jv.geom.PgPolygonSet;
import jv.geom.PgVectorField;
import jv.geom.PuCleanMesh;
import jv.number.PdColor;
import jv.object.PsConfig;
import jv.object.PsDebug;
import jv.object.PsObject;
import jv.project.PgGeometry;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.vecmath.PuMath;
import jv.viewer.PvDisplay;
import jv.project.PvGeometryIf;

import Jama.SingularValueDecomposition;
import Jama.Matrix;

import jvx.project.PjWorkshop;

public class Analysis extends PjWorkshop {

    PgElementSet m_geom;

    /** Constructor */
    public Analysis() {
        super("Surface Registration");
        if (getClass() == Analysis.class) {
            init();
        }
    }

    public void setGeometry(PgElementSet surfP) {
        PsDebug.message("Geometries set");
        m_geom = surfP;
    }

    public void genus(int index)
    {
        int numOfEdges = m_geom.getNumEdges();
        int numOfVertices = m_geom.getNumVertices();
        int numOfBorders = m_geom.getNumBoundaries();
        // Num of Faces is equal to two thirds of the number of edges, every triangle has 3 edges and every edge is in 2 triangles
        int numOfFaces = 2*(numOfEdges/3);
        double vol = m_geom.getVolume();
        int comp = m_geom.getNumElements();


        String nums = "\n\n\nNumber of Edges: " + numOfEdges + "\nNumber of Vertices: " + numOfVertices + "\nNumber of Borders: " + numOfBorders + "\nNumber of Faces: " + numOfFaces;
        int genusNumValue = (int) (1 - (float)(numOfVertices + numOfBorders + numOfFaces - numOfEdges)/2);

        String genus = "\nGenus =  " + genusNumValue;

        String volume = "\n\n\nThe Volume enclosed by the surface is: " + vol;

        String components = "\n\n\nThe Components of the surface are: " + comp;

        if(index == 0)
            PsDebug.message(nums + genus);
        else if(index == 1)
            PsDebug.message(volume);
        else
            PsDebug.message("Choose one!!!");
    }
}
