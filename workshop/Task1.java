package workshop;

import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.vecmath.*;

import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *  Workshop for matrices computation.
 */

public class Task1 extends PjWorkshop {

    /** Mesh to compute matrices. */
    PgElementSet m_geom;

    /** Constructor */
    public Task1() {
        super("Differential Coordinates");
        init();
    }

    /** Initialization */
    public void init() {
        super.init();
        m_geom = (PgElementSet)super.m_geom;
    }


    /** Set two Geometries. */
    public void setGeometries(PgElementSet geom) {
        super.setGeometry(geom);
        m_geom = (PgElementSet)super.m_geom;
    }

    /** Compute gradient matrix G. */
    public PnSparseMatrix matrixG() {

        m_geom = (PgElementSet)super.m_geom;
        m_geom.assureVertexNormals();
        m_geom.assureElementNormals();
        m_geom.makeElementNormals();

        PsDebug.message("Starting matrix G computation...");


        /*OUT OF THE FUNCTION?*/
        //Number of faces
//        PsDebug.message(m_geom.toString());
        int m = m_geom.getNumElements();

        //Number of vertices
        int n = m_geom.getNumVertices();

        PnSparseMatrix G = new PnSparseMatrix(3*m,n,3);

        /*---------------*/


        // for over all the faces of the mesh
        for(int i = 0; i < m; i++) {

           // method that retrieves local matrix
           PdMatrix G_local = calcLocalGradientMatrix(i);

           //local vertices
           PiVector loc_vertices = m_geom.getElement(i); //esto devuelve vertices index en orden
//            if(i == 0)
//                printPiVector("loc_vertices", loc_vertices);

           //add local matrix to global matrix
           for (int j = 0; j < 3; j++) {
               for (int k = 0; k < 3; k++) {
                   double entry = G_local.getEntry(j, k);
//                   PsDebug.message("Entry: " + entry);
                   G.addEntry(3 * i + j, loc_vertices.getEntry(k), entry);
               }
           }
        }

        PsDebug.message("Finished matrix G computation.");

        PsDebug.message("Testing matrix G...");

        int[] facesToTest = {1373, 3057};

        for(int face : facesToTest){

            PiVector vertices = m_geom.getElement(face);

            ArrayList<Integer> v = new ArrayList();
            for(int vertex : vertices.getEntries())
                v.add(vertex);

            PsDebug.message("Face " + face + ": " + v);

            for(int row = 3 * face; row < 3 * face + 3; row++){
                for(int col = 0; col < G.getNumCols(); col++)
                    if(G.getEntry(row, col) != 0.0)
                        PsDebug.message("(" + row + ", " + col + ")");
            }

        }


//        printSparseMatrix("G matrix", G);

        PsDebug.message("COMPLETED MATRIX G CALCULATION AND TESTING");

        return G;
    }

    public static void printSparseMatrix(String name, PnSparseMatrix matrix) {

        int counter = 0;

        PsDebug.message(name + ":");
        for(int row = 0; row < matrix.getNumRows(); row++){
            counter += matrix.getNumEntries(row);
        }

        PsDebug.message("NUMBER OF ROWS: " + matrix.getNumRows());
        PsDebug.message("NUMBER OF COLUMNS: " + matrix.getNumCols());
        PsDebug.message("There are " + counter + " non zero elements in the sparse matrix");
    }

    private void printMatrix(String name, PdMatrix matrix) {
        PsDebug.message(name + ":");
        for(int row = 0; row < 3; row++){
            String s = "";
            for(int col = 0; col < 3; col++) {
                s += "" + matrix.getEntry(row, col) + " ";
            }
            PsDebug.message(s);
        }
    }


    private PdMatrix calcLocalGradientMatrix(int num_triangle) {

        PiVector vertices = m_geom.getElement(num_triangle);

        PdVector e_1 = PdVector.subNew(m_geom.getVertex(vertices.getEntry(2)), m_geom.getVertex(vertices.getEntry(1)));
        PdVector e_2 = PdVector.subNew(m_geom.getVertex(vertices.getEntry(0)), m_geom.getVertex(vertices.getEntry(2)));
        PdVector e_3 = PdVector.subNew(m_geom.getVertex(vertices.getEntry(1)), m_geom.getVertex(vertices.getEntry(0)));

        PdVector N = m_geom.getElementNormal(num_triangle);

        PdVector N_x_e1 = PdVector.crossNew(N,e_1);
        PdVector N_x_e2 = PdVector.crossNew(N,e_2);
        PdVector N_x_e3 = PdVector.crossNew(N,e_3);

//        printPdVector("e_1", e_1);
//        printPdVector("N", N);
//        printPdVector("N_x_e1", N_x_e1);

        double A = m_geom.getAreaOfElement(num_triangle);
        double factor = 1/(2*A);

        double[][] local_matrix = new double[3][3];
        for (int i = 0; i < 3; i++) {
            local_matrix[i][0] = N_x_e1.getEntry(i);
            local_matrix[i][1] = N_x_e2.getEntry(i);
            local_matrix[i][2] = N_x_e3.getEntry(i);
        }

        PdMatrix b = new PdMatrix(local_matrix);
        b.multScalar(factor);

//        printMatrix("b", b);

        return b;
    }

    public static void printPdVector(String name, PdVector vect) {
        String s = "";
        for(int i = 0; i < 5; i++)
            s += "" + vect.getEntry(i) + " ";
        PsDebug.message(name + ":");
        PsDebug.message(s);
    }

    private void printPiVector(String name, PiVector vect) {
        String s = "";
        for(int i = 0; i < vect.getSize(); i++)
            s += "" + vect.getEntry(i) + " ";
        PsDebug.message(name + ":");
        PsDebug.message(s);
    }

    /** Compute matrix L. */
    public PnSparseMatrix matrixL(PnSparseMatrix M, PnSparseMatrix S) {

        PsDebug.message("Starting matrix L computation...");


        PnSparseMatrix M_inverse = new PnSparseMatrix(M.getNumRows(), M.getNumCols());

        for(int i = 0; i < M.getNumRows(); i++)
            M_inverse.addEntry(i, i, 1 / (M.getEntry(i, i) + 0.0001));

        PnSparseMatrix L = null;
        L = PnSparseMatrix.multMatrices(M_inverse, S, L);

        PsDebug.message("Finished matrix L computation.");

        PdVector row50 = L.getEntries(50);
        int counter = 0;
        for(double value : row50.getEntries()){
            if(value != 0.0){
                counter++;
                PsDebug.message("Row 50: " + value);
            }
        }
        PsDebug.message("Counter row 50: " +  counter);

        PdVector row60 = L.getEntries(60);
        counter = 0;
        for(double value : row60.getEntries()){
            if(value != 0.0){
                counter++;
                PsDebug.message("Row 60: " + value);
            }
        }
        PsDebug.message("Counter row 60: " +  counter);



        printSparseMatrix("L", L);

        PsDebug.message("COMPLETED MATRIX L CALCULATION AND TESTING");

        return L;
    }

    /**
     * Compute combinatorial L matrix
     * @return
     */
    public PnSparseMatrix matrixL_combinatorial(){
        PsDebug.message("Starting matrix L computation...");

        m_geom = (PgElementSet)super.m_geom;
        m_geom.assureVertexNormals();
        m_geom.assureElementNormals();
        m_geom.makeElementNormals();

        int n = m_geom.getNumVertices();
        int m = m_geom.getNumElements();

        PnSparseMatrix L = new PnSparseMatrix(n, n);

        for(int i = 0; i < n; i++) {

            if(i % 10000 == 0)
                PsDebug.message("Vertex " + i + " of " + n);


            ArrayList<Integer> neighbours = new ArrayList<>();

            for(int j = 0; j < m; j++)
                if(m_geom.getElement(j).contains(i)){
                    PiVector face = m_geom.getElement(j);
                    for(int vertex_index : face.getEntries())
                        if(!neighbours.contains(vertex_index))
                            neighbours.add(vertex_index);
                }

            int degree = neighbours.size();

            double diag_element = 0;
            for(int vertex_index : neighbours){
                if(vertex_index != i) {
                    L.addEntry(i, vertex_index, -(1.0 / degree));
                    diag_element += -(1.0 / degree);
                }
            }
            L.addEntry(i, i, -diag_element);
        }

        PsDebug.message("Finished matrix L computation.");

        for(int col = 0; col < L.getNumCols(); col++)
            if(L.getEntry(1, col) != 0.0)
                PsDebug.message("(1, " + col + "): " + L.getEntry(1, col));

        for(int col = 0; col < L.getNumCols(); col++)
            if(L.getEntry(60, col) != 0.0)
                PsDebug.message("(60, " + col + "): " + L.getEntry(60, col));

//        PdVector row1 = L.getEntries(1);
//        for(double value : row1.getEntries())
//            PsDebug.message("Row 1: " + value);
//
//        PdVector row50 = L.getEntries(50);
//        for(double value : row50.getEntries())
//            PsDebug.message("Row 50: " + value);
//
//        PdVector row2000 = L.getEntries(2000);
//        for(double value : row2000.getEntries())
//            PsDebug.message("Row 2000: " + value);
        PsDebug.message("Testing matrix L...");

        int[] rows_to_check = {10, 20, 30, 50, 1000, 6000};
        PsDebug.message("**********************************************");
        for(int row : rows_to_check){

            ArrayList<Integer> non_zeros = new ArrayList<>();
            for(int col = 0; col < L.getNumCols(); col++)
                if(L.getEntry(row, col) != 0.0)
                    non_zeros.add(col);

            PsDebug.message("Row " + row + ": " + non_zeros.toString());
        }
        PsDebug.message("**********************************************");

        printSparseMatrix("M", L);

        PsDebug.message("COMPLETED MATRIX L CALCULATION AND TESTING");

        return L;
    }

    /** Compute contangent matrix S. */
    public PnSparseMatrix matrixS(PnSparseMatrix G) {

        PsDebug.message("Starting matrix S computation...");


        int m = m_geom.getNumElements();

        PnSparseMatrix Mv = new PnSparseMatrix(3 * m, 3 * m,1);

        for(int i = 0; i < m; i++) {
            double A = m_geom.getAreaOfElement(i);
            for(int j = 0; j < 3; j++)
                Mv.addEntry(3 * i + j, 3 * i + j, A);
        }

        PnSparseMatrix G_transpose = G.transposeNew();

        PnSparseMatrix Gtranspose_Mv = null;
        Gtranspose_Mv = PnSparseMatrix.multMatrices(G_transpose, Mv, Gtranspose_Mv);

        PnSparseMatrix S = null;
        S = PnSparseMatrix.multMatrices(Gtranspose_Mv, G, S);

        PsDebug.message("Finished matrix S computation.");

        PsDebug.message("Testing matrix S ...");

        int[] rows_to_check = {10, 20, 30, 50, 1000, 6000};
        int[] cols_to_check = {10, 20, 30, 50, 1000, 6000};
        boolean found = false;
        for(int row : rows_to_check)
            for(int col : cols_to_check)
                if(Math.abs(S.getEntry(row, col) - S.getEntry(col, row)) > 0.001) {
                    PsDebug.message("(" + row + ", " + col + ") is different than (" + row + ", " + col + ")");
                    found = true;
                }

        for(int row : rows_to_check) {
            double sum = 0;
            for (int col = 0; col < S.getNumCols(); col++)
                if(col != row && S.getEntry(row, col) != 0.0)
                    sum += S.getEntry(row, col);

            if(Math.abs((-sum) - S.getEntry(row, row)) > 0.001)
                PsDebug.message("For row " + row + " the sum is " + (-sum) + " but the diagonal is " + S.getEntry(row, row));
        }

        PsDebug.message("**********************************************");
        for(int row : rows_to_check){

            ArrayList<Integer> non_zeros = new ArrayList<>();
            for(int col = 0; col < S.getNumCols(); col++)
                if(S.getEntry(row, col) != 0.0)
                    non_zeros.add(col);

            PsDebug.message("Row " + row + ": " + non_zeros.toString());
        }
        PsDebug.message("**********************************************");



        if(!found)
            PsDebug.message("MATRIX S IS CORRECT");


        printSparseMatrix("S", S);

        PsDebug.message("COMPLETED MATRIX S CALCULATION AND TESTING");

        return S;
    }

    /** Compute mass matrix M. */
    public PnSparseMatrix matrixM() {

        PsDebug.message("Starting matrix M computation...");

        m_geom = (PgElementSet)super.m_geom;
        m_geom.assureVertexNormals();
        m_geom.assureElementNormals();
        m_geom.makeElementNormals();

        int n = m_geom.getNumVertices();
        int m = m_geom.getNumElements();

        PnSparseMatrix M = new PnSparseMatrix(n, n,1);

        for(int i = 0; i < n; i++) {

            if(i % 10000 == 0)
                PsDebug.message("Vertex " + i + " of " + n);

            int counter = 0;

            double total_area = 0;
            for(int j = 0; j < m; j++)
                if(m_geom.getElement(j).contains(i)){
                    total_area += m_geom.getAreaOfElement(j);
                    counter++;
                }

            if(i == 50 || i == 60) {
                PsDebug.message("" + i + " --> " + 1.0 / counter);
                PsDebug.message("Degree" +  " --> " + counter);
            }

            total_area /= 3;

            M.addEntry(i, i, total_area);
        }

        PsDebug.message("Finished matrix M computation.");


        printSparseMatrix("M", M);

        PsDebug.message("COMPLETED MATRIX M CALCULATION AND TESTING");

        return M;

    }
}