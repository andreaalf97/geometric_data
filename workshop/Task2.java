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
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.vecmath.PuMath;
import jv.viewer.PvDisplay;
import jv.project.PvGeometryIf;

import jvx.project.PjWorkshop;

/**
 *  Workshop for surface registration
 */

public class Task2 extends PjWorkshop {

    /** First surface to be registered. */
    PgElementSet	m_surfP;
    /** Second surface to be registered. */
    PgElementSet	m_surfQ;
    /** K parameter for the median algorithm */
    double k = 3.0;
    /** Percentage of random points to select */
    double p = 0.3;


    /** Constructor */
    public Task2() {
        super("Surface Registration");
        if (getClass() == Task2.class) {
            init();
        }
    }

    /** Initialization */
    public void init() {
        super.init();
    }


    /** Set two Geometries. */
    public void setGeometries(PgElementSet surfP, PgElementSet surfQ) {
        PsDebug.message("Geometries set");
        m_surfP = surfP;
        m_surfQ = surfQ;
    }

    /** Set the k parameter */
    public void setK(double k){
        PsDebug.message("Updating K to " + k);
        this.k = k;
    }

    /** Set the p parameter */
    public void setP(double p){
        PsDebug.message("Updating P to " + p);
        this.p = p;
    }

    /** This function is called when the RUN button is pressed */
    public void run() {
        PsDebug.message("RUN pressed");

        PdVector[] randomVectorsP = getRandomVectors(this.p, true);

        boolean converged = false;
        while(!converged){

            PdVector[] closestVerticesQ = findClosestVertices(randomVectorsP);

            int[] validIndices = getValidIndices(randomVectorsP, closestVerticesQ, this.k);
            PsDebug.message("There are " + validIndices.length + " valid indices after checking the median");

            PdVector[] validIndicesP = new PdVector[validIndices.length];
            PdVector[] validIndicesQ = new PdVector[validIndices.length];
            for(int i = 0; i < validIndices.length; i++){
                validIndicesP[i] = randomVectorsP[validIndices[i]];
                validIndicesQ[i] = closestVerticesQ[validIndices[i]];
            }



            converged = true;
        }




        m_surfP.update(m_surfP);
        m_surfQ.update(m_surfQ);
    }

    /**
     * This function returns the list of valid indices, which are the ones where the distance is < k*median
     */
    private int[] getValidIndices(PdVector[] vertices_P, PdVector[] vertices_Q, double k_param) {

        if(vertices_P.length != vertices_Q.length)
            throw new RuntimeException("The two lists are of different sizes!");

        Double[] distances = new Double[vertices_P.length];
        for(int i = 0; i < vertices_P.length; i++)
            distances[i] = PdVector.dist(vertices_P[i], vertices_Q[i]);

        List<Double> sortedList = Arrays.asList(distances);
        Collections.sort(sortedList);

        PsDebug.message("The distances go from " + sortedList.get(0) + " to " + sortedList.get(sortedList.size() - 1));

        double median = sortedList.get(sortedList.size() / 2);
        PsDebug.message("MEDIAN: " + median);

        List<Integer> validIndices = new ArrayList<>();
        for(int i = 0; i < distances.length; i++)
            if(distances[i] < k_param * median)
                validIndices.add(i);

        int[] v = new int[validIndices.size()];
        for(int i = 0; i < validIndices.size(); i++)
            v[i] = validIndices.get(i);

        return v;
    }

    /**
     * This function returns two lists: the first one is the list of closes vertices, and the second one is the list of distances to those vertices
     * @param verticesP the list of vertices of surface P
     * @return A map which contains
     */
    private PdVector[] findClosestVertices(PdVector[] verticesP) {

        PdVector[] closestVertices = new PdVector[verticesP.length];

        for(int index = 0; index < verticesP.length; index++){
            PdVector vertexP = verticesP[index];

            double minDistance = PdVector.dist(vertexP, m_surfQ.getVertices()[0]);
            PdVector closestVertex = m_surfQ.getVertices()[0];
            for(PdVector vertexQ : m_surfQ.getVertices()){
                double dist = PdVector.dist(vertexP, vertexQ);
                if(dist < minDistance){
                    minDistance = dist;
                    closestVertex = vertexQ;
                }
            }

            closestVertices[index] = closestVertex;
        }

        return closestVertices;
    }

    /**
     * This function extracts a random set of vertices
     * @param prob
     * @return
     */
    private PdVector[] getRandomVectors(double prob, boolean showColors) {

        // This makes sure that the color array is allocated
        m_surfP.assureVertexColors();

        int numOfVerticesP = m_surfP.getNumVertices();
        PsDebug.message("There are " +  numOfVerticesP + " vertices in the surface P");

        List<Integer> listOfNumbers = new ArrayList<>();
        for(int i = 0; i < numOfVerticesP; i++)
            listOfNumbers.add(i);

        Collections.shuffle(listOfNumbers);

        Random random = new Random();

        int numOfVerticesToRead = (int)Math.floor(numOfVerticesP * prob);
        PsDebug.message("Extracting " + numOfVerticesToRead + " random vertices");
        PdVector[] vertices = new PdVector[numOfVerticesToRead];
        for(int i = 0; i < numOfVerticesToRead; i++) {
            vertices[i] = m_surfP.getVertex(listOfNumbers.get(i));

            if(showColors) {
                Color randomColor = Color.getHSBColor(random.nextFloat(), 1.0f, 1.0f);
                m_surfP.setVertexColor(listOfNumbers.get(i), randomColor);
            }
        }

        if(showColors) {
            m_surfP.showElementColors(true);
            m_surfP.showVertexColors(true);
            m_surfP.showElementColorFromVertices(true);
            m_surfP.showSmoothElementColors(true);
        }

        return vertices;
    }
}
