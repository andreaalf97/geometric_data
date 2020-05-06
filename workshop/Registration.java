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

public class Registration extends PjWorkshop {
	
	/** First surface to be registered. */	
	PgElementSet	m_surfP;	
	/** Second surface to be registered. */
	PgElementSet	m_surfQ;	
	
	
	/** Constructor */
	public Registration() {
		super("Surface Registration");
		if (getClass() == Registration.class) {
			init();
		}
	}
	
	/** Initialization */
	public void init() {
		super.init();
	}
	
	
	/** Set two Geometries. */
	public void setGeometries(PgElementSet surfP, PgElementSet surfQ) {

		// Amount of random numbers required
		int numRandomPoints = (int)(.20 * surfP.getNumVertices());
		int k = 2;

		// List of all integers from 0 to max
		Integer[] numbers = new Integer[surfP.getNumVertices()];

		for(int i = 0; i < numbers.length; i++)
			numbers[i] = i;

		// Create a list object to be able to shuffle it
		List<Integer> list = Arrays.asList(numbers);
		Collections.shuffle(list);

		// Retrieve only the top numbers that are required (numRandomPoints)
		int[] randomNumbers = new int[numRandomPoints];
		for(int i = 0; i < numRandomPoints; i++)
			randomNumbers[i] = list.get(i);

		// Get the list of vertices from the indices
		ArrayList<PdVector> vertexList = new ArrayList();
		for(int i = 0; i < numRandomPoints; i++)
			vertexList.add(surfP.getVertex(randomNumbers[i]));


		ArrayList<Double> distances = new ArrayList();
		ArrayList<PdVector> closestPoints = new ArrayList<>();
		for(PdVector elementP : vertexList){

			double minDistance = 10000000.0;
			PdVector closestPoint = null;
			for(int i = 0; i < surfQ.getNumVertices(); i++){

				PdVector elementQ = surfQ.getVertex(i);
				double dist = elementP.dist(elementQ);
				if(dist < minDistance){
					minDistance = dist;
					closestPoint = elementQ;
				}
			}

			distances.add(minDistance);
			closestPoints.add(closestPoint);

		}

		ArrayList<Double> orderedDistances = (ArrayList<Double>)distances.clone();
		Collections.sort(orderedDistances);

		double median = orderedDistances.get(
				(int)(orderedDistances.size() / 2)
		);

		ArrayList<PdVector> pointsP = new ArrayList<>();
		ArrayList<PdVector> pointsQ = new ArrayList<>();
		int drops = 0;
		for (int i = 0; i < distances.size(); i++){
			if(distances.get(i) < k * median){
				pointsP.add(vertexList.get(i));
				pointsQ.add(closestPoints.get(i));
			}
			else{
				drops++;
			}
		}


		PsDebug.message("Dropping " + drops + " points out of " + vertexList.size() );

		m_surfP = surfP;
		m_surfQ = surfQ;
	}
	
	
}
