package workshop;

import java.awt.Color;
import java.util.*;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;

import jv.vecmath.PuMath;
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
    double k = 1.5;
    /** Percentage of random points to select */
    double p = 0.1;
    /** How close to optimal for convergence */
    double conv_precision = 0.001;
    /** This allows us to know if the user wants the euclidean or the ptp distance */
    boolean euclideanDistance = true;


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

        PdVector[] randomVectorsP = getRandomVectors(this.p, false);

        boolean converged = false;
        int steps = 0;
        while(!converged){

            Map<Integer[], PdVector[]> mapOfClosestVertices = findClosestVertices(randomVectorsP);

            PdVector[] closestVerticesQ = (PdVector[])(mapOfClosestVertices.values().toArray()[0]);
            Integer[] tempIndicesOfQ = (Integer[])(mapOfClosestVertices.keySet().toArray()[0]);

            //PsDebug.message("Q length: " + closestVerticesQ.length + " P length: " + randomVectorsP.length);


            int[] validIndices = getValidIndices(randomVectorsP, closestVerticesQ, this.k);
//            PsDebug.message("There are " + validIndices.length + " valid indices after checking the median");

            PdVector[] p_subset = new PdVector[validIndices.length];
            PdVector[] q_subset = new PdVector[validIndices.length];
            Integer[] indicesOfQ = new Integer[validIndices.length];
            for(int i = 0; i < validIndices.length; i++){
                p_subset[i] = randomVectorsP[validIndices[i]];
                q_subset[i] = closestVerticesQ[validIndices[i]];
                indicesOfQ[i] = tempIndicesOfQ[validIndices[i]];
            }

            Matrix R_opt;
            PdVector T_opt;

            if(this.euclideanDistance) {
                PdVector p_centroid = computeCentroid(p_subset);
                PdVector q_centroid = computeCentroid(q_subset);

                Matrix M = computeM(p_subset, p_centroid, q_subset, q_centroid);

                R_opt = computeSVDapprox(M.svd());
                T_opt = computeTopt(R_opt, q_centroid, p_centroid);
            }
            else{

                Matrix A = calcMatrixA(p_subset, indicesOfQ);
                PdVector b = calcVectorB(p_subset, q_subset, indicesOfQ);
//                double c = calcC(p_subset, q_subset, indicesOfQ);

                b.multScalar(-1);
                Matrix A_inv = A.inverse();

                PdVector solution = b.leftMultMatrix(new PdMatrix(A_inv.getArray())); //rightMultMatrix

                if(solution.getSize() != 6)
                    throw new RuntimeException("Size of (r t) is not 6");

                double[] r = new double[3];
                double[] t = new double[3];
                for(int i = 0; i < 3; i++){
                    r[i] = solution.getEntries()[i];
                    t[i] = solution.getEntries()[i+3];
                }

                T_opt = new PdVector(t);

                double[][] R_linear = {
                        {1, -r[2], r[1]},
                        {r[2], 1, -r[0]},
                        {-r[1], r[0], 1}
                };

                R_opt = computeSVDapprox((new Matrix(R_linear)).svd());

            }

            for (PdVector vertex : m_surfP.getVertices()) {
                vertex.leftMultMatrix(new PdMatrix(R_opt.getArrayCopy()));
                vertex.add(T_opt);
            }

            if(getConvergenceValue(T_opt, R_opt) < this.conv_precision)
                converged = true;

            PsDebug.message("ITERATION: " + steps);
            steps++;


            m_surfP.update(m_surfP);
            m_surfQ.update(m_surfQ);
        }

        PsDebug.message("CONVERGED IN " + steps + " STEPS");

        m_surfP.update(m_surfP);
        m_surfQ.update(m_surfQ);

    }

    private PdVector calcVectorB(PdVector[] p_subset, PdVector[] q_subset, Integer[] indicesOfQ) {

        double[] b = {0, 0, 0, 0, 0, 0};

        for(int i = 0; i < p_subset.length; i++){

            PdVector n_i = m_surfQ.getVertexNormal(indicesOfQ[i]);
            PdVector p_i = p_subset[i];
            PdVector p_iXn_i = PdVector.crossNew(p_i, n_i);

            double left = PdVector.dot(PdVector.subNew(p_i, q_subset[i]), n_i);
            double[] right_d = {
                    p_iXn_i.getEntry(0),
                    p_iXn_i.getEntry(1),
                    p_iXn_i.getEntry(2),
                    n_i.getEntry(0),
                    n_i.getEntry(1),
                    n_i.getEntry(2)
            };

            PdVector right = new PdVector(right_d);
            right.multScalar(left);

            for(int j = 0; j < 6; j++)
                b[j] += right.getEntry(j);

        }

        return new PdVector(b);
    }

    private Matrix calcMatrixA(PdVector[] p_subset, Integer[] indicesOfQ) {

        double[][] left = new double[6][p_subset.length];
        double[][] right = new double[p_subset.length][6];

        for(int k = 0; k < p_subset.length; k++) {
            PdVector n_i = m_surfQ.getVertexNormal(indicesOfQ[k]);
            PdVector p_i = p_subset[k];
            PdVector p_iXn_i = PdVector.crossNew(p_i, n_i);

            for(int i = 0; i < 3; i++){
                left[i][k] = p_iXn_i.getEntry(i);
                left[i+3][k] = n_i.getEntry(i);
            }

            for(int i = 0; i < 3; i++){
                right[k][i] = p_iXn_i.getEntry(i);
                right[k][i+3] = n_i.getEntry(i);
            }
        }

        Matrix A = new Matrix(left).times(new Matrix(right));
        return A;

    }

    private double getConvergenceValue(PdVector t_opt, Matrix r_opt) {
        if(t_opt.getSize() != 3)
            throw new RuntimeException("t_opt is not of size 3");
        if(r_opt.getRowDimension() != 3 || r_opt.getColumnDimension() != 3)
            throw new RuntimeException("r_opt is not of size 3x3");


        double[][] identity = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        double[][] R = r_opt.getArray();

        double errorSum = 0;

        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                errorSum += Math.abs(R[row][col] - identity[row][col]);

        for(int i = 0; i < 3; i++)
            errorSum += Math.abs(t_opt.getEntry(i));

        return errorSum;
    }

    private boolean conv(Matrix r_opt, double conv_precision) {

        double sum = 0;
        for(int i = 0; i < r_opt.getColumnDimension(); i++)
            sum += Math.abs(r_opt.get(i, i) - 1);

        return sum < conv_precision;

    }

    /** This function computes the T_optimal vector */
    private PdVector computeTopt(Matrix r_opt, PdVector q_centroid, PdVector p_centroid) {

        PdVector RP = new PdVector();
        RP = RP.leftMultMatrix(new PdMatrix(r_opt.getArrayCopy()), p_centroid);

        PdVector T_opt = PdVector.subNew(q_centroid, RP);

        return T_opt;
    }

    private void printMatrix(String name, Matrix matrix) {
        PsDebug.message(name + ":");
        for(int row = 0; row < 3; row++){
            String s = "";
            for(int col = 0; col < 3; col++) {
                s += "" + matrix.get(row, col) + " ";
            }
            PsDebug.message(s);
        }
    }

    private void printPdVector(String name, PdVector vect) {
        String s = "";
        for(int i = 0; i < vect.getSize(); i++)
            s += "" + vect.getEntry(i) + " ";
        PsDebug.message(name + ":");
        PsDebug.message(s);
    }

    private void printPdVector(PdVector vect) {
        String s = "";
        for(int i = 0; i < vect.getSize(); i++)
            s += "" + vect.getEntry(i) + " ";
        PsDebug.message(s);
    }

    /** This function computes the R_optimal matrix */
    private Matrix computeSVDapprox(SingularValueDecomposition svd) {

        //Matrix U_t = svd.getU().transpose();
        //Matrix VU_t = svd.getV().times(U_t);
        //double det_VU_t = VU_t.det();

        Matrix U = svd.getU();
        Matrix V_t = svd.getV().transpose();
        Matrix UV_t = U.times(V_t);
        double det_UV_t = UV_t.det();

        double[][] mid_matrix_temp = {
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, det_UV_t}
        };
        Matrix mid_matrix = new Matrix(mid_matrix_temp);

        Matrix R_opt = U.times(mid_matrix);
        R_opt = R_opt.times(V_t);

        return R_opt;
    }

    /** This function computes the matrix M of the optimal rigid tranformation algorithm */
    private Matrix computeM(PdVector[] p_subset, PdVector p_centroid, PdVector[] q_subset, PdVector q_centroid) {

        if(p_subset.length != q_subset.length)
            throw new RuntimeException("P subset and Q subset are of different length");

        double[][] M = {
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0}
        };

        for(int i = 0; i < p_subset.length; i++){

            PdVector p = PdVector.subNew(p_subset[i], p_centroid);
            PdVector q = PdVector.subNew(q_subset[i], q_centroid);

            for(int row = 0; row < 3; row++)
                for(int col = 0; col < 3; col++)
                    M[row][col] += p.getEntry(row) * q.getEntry(col);

        }

        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                M[row][col] /= p_subset.length;

        return new Matrix(M);
    }

    /** This function computes the centroid of a list of vectors */
    private PdVector computeCentroid(PdVector[] set) {

        if(set.length == 0)
            throw new RuntimeException("The set has lenght 0");

        double[] temp = {0, 0, 0};

        for(PdVector vector : set)
            for(int i = 0; i < vector.getSize(); i++)
                temp[i] += vector.getEntry(i);

        for(int i = 0; i < temp.length; i++)
            temp[i] /= set.length;

        return new PdVector(temp);

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

//        PsDebug.message("The distances go from " + sortedList.get(0) + " to " + sortedList.get(sortedList.size() - 1));

        double median = sortedList.get(sortedList.size() / 2);
//        PsDebug.message("MEDIAN: " + median);

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
    private Map<Integer[], PdVector[]> findClosestVertices(PdVector[] verticesP) {

        PdVector[] closestVertices = new PdVector[verticesP.length];
        Integer[] indexOfClosestVertices = new Integer[verticesP.length];

        for(int index = 0; index < verticesP.length; index++){
            PdVector vertexP = verticesP[index];

            double minDistance;
            minDistance = PdVector.dist(vertexP, m_surfQ.getVertices()[0]);
            PdVector closestVertex = m_surfQ.getVertices()[0];
            Integer indexOfClosestVertex = 0;

            for(int k = 0; k < m_surfQ.getNumVertices(); k++) {
                PdVector vertexQ = m_surfQ.getVertex(k);

                double dist = PdVector.dist(vertexP, vertexQ);

                if(dist < minDistance){
                    minDistance = dist;
                    closestVertex = vertexQ;
                    indexOfClosestVertex = k;
                }
            }

            closestVertices[index] = closestVertex;
            indexOfClosestVertices[index] = indexOfClosestVertex;
        }

        Map<Integer[], PdVector[]> res = new HashMap<>();
        res.put(indexOfClosestVertices, closestVertices);
        return res;
    }

    private double pointToPlaneDistance(PdVector vertexP, PdVector vertexQ, PdVector vertexNormal) {

        PdVector diff = PdVector.subNew(vertexP, vertexQ);

        return Math.abs(PuMath.dot(diff.getEntries(), vertexNormal.getEntries()));

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

    public void setPrecision(double precision) {
        this.conv_precision = precision;
    }

    public void changeDistanceMetric() {
        this.euclideanDistance = ! this.euclideanDistance;
        PsDebug.message("Euclidean Distance: " + this.euclideanDistance);
    }
}