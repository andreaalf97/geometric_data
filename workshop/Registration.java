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

		// Compute the centroids
		PdVector p_centroid = compute_centroid(pointsP);
		PdVector q_centroid = compute_centroid(pointsQ);

		Matrix M = compute_M(pointsP, pointsQ, p_centroid, q_centroid);

		for(int i = 0; i < 3; i++) {
			String s = "";
			for (int j = 0; j < 3; j++)
				s += M.get(i, j) + " ";

			PsDebug.message(s);

		}

		SingularValueDecomposition svd = M.svd();

		double[][] middle_temp = {
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, (svd.getV().times(svd.getU().transpose())).det()}
		};
		Matrix middle = new Matrix(middle_temp);

		// This is a 3x3 matrix
		Matrix R_opt = (svd.getV().times(middle)).times(svd.getU().transpose());

		PdMatrix R_opt_pd = new PdMatrix(R_opt.getArrayCopy());

		PdVector temp_opt = new PdVector();
		R_opt_pd.leftMultMatrix(temp_opt, p_centroid);

		temp_opt = PdVector.subNew(q_centroid, temp_opt);

		double[] data = temp_opt.m_data;
		PsDebug.message("temp_opt:");
		for(int i = 0; i < data.length; i++)
			PsDebug.message("" + data[i]);
		m_surfP = surfP;
		m_surfQ = surfQ;
	}

	private PdVector compute_centroid(ArrayList<PdVector> list){
		double[] temp = {0, 0, 0};
		PdVector sum = new PdVector(temp);

		for(PdVector p : list)
			sum.add(p);

		double[] avg = {
				sum.getEntry(0)/list.size(),
				sum.getEntry(1)/list.size(),
				sum.getEntry(2)/list.size()
		};

		return new PdVector(avg);
	}

	private Matrix compute_M(ArrayList<PdVector> p_list, ArrayList<PdVector> q_list, PdVector p_centroid, PdVector q_centroid){

		double[][] temp = {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		};
		PdMatrix sum = new PdMatrix(temp);

		for(int i = 0; i < p_list.size(); i++){

			PdVector p = p_list.get(i);
			p.sub(p_centroid);

			PdVector q = q_list.get(i);
			q.sub(q_centroid);

			double[][] temp_sum = new double[3][3];
			for(int j = 0; j < 3; j++)
				for(int k = 0; k < 3; k++)
					temp_sum[j][k] = p.getEntry(j) * q.getEntry(k);

			sum.add(new PdMatrix(temp_sum));
		}


		temp = new double[3][3];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				temp[i][j] = sum.getEntry(i, j) / p_list.size();

		return new Matrix(temp);
	}
	
	
}
