package workshop;

import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.object.PsObject;
import jv.vecmath.*;

import jvx.numeric.PnBiconjugateGradient;
import jvx.numeric.PnConjugateGradientMatrix;
import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *  Workshop for matrices computation.
 */

public class Task2_Assignment2 extends PjWorkshop {

    /** Mesh to compute matrices. */
    PgElementSet m_geom;

    /** Constructor */
    public Task2_Assignment2() {
        super("Task 2 Extras");
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

    private PdVector[] getLaplaceCoords(PgElementSet geom, PnSparseMatrix l){

        PsDebug.message("Calculating Laplace coordinates");

        int n = geom.getNumVertices();
        int m = geom.getNumElements();

        PdVector[] laplace = new PdVector[n];
        for(int i = 0; i < n; i++){
            //For each vertex

            if(i % 10000 == 0)
                PsDebug.message("Vertex " + i + " of " + n);

            PdVector v_i = geom.getVertex(i);

            ArrayList<Integer> neighbours = new ArrayList<>();

            for(int j = 0; j < m; j++)
                if(geom.getElement(j).contains(i)){
                    PiVector face = m_geom.getElement(j);
                    for(int vertex_index : face.getEntries())
                        if(!neighbours.contains(vertex_index))
                            neighbours.add(vertex_index);
                }

            int degree = neighbours.size();
            double[] temp = {0, 0, 0};
            PdVector sum = new PdVector(temp);

            for(int neighbour_index : neighbours){
                PdVector v_j = geom.getVertex(neighbour_index);
                PdVector diff = PdVector.subNew(v_i, v_j);
                sum = PdVector.addNew(sum, diff);
            }

            sum.multScalar(1.0 / degree);

            laplace[i] = sum;
        }

        return laplace;

    }

    private void printPdVector(String name, PdVector vect) {
        String s = "";
        for(int i = 0; i < vect.getSize(); i++)
            s += "" + vect.getEntry(i) + " ";
        PsDebug.message(name + ":");
        PsDebug.message(s);
    }

    /**
     * Compute combinatorial L matrix
     * @return
     */
    public PnSparseMatrix matrixL_combinatorial(PgElementSet geometry){
        PsDebug.message("Starting matrix L computation...");

        int n = geometry.getNumVertices();
        int m = geometry.getNumElements();

        PnSparseMatrix L = new PnSparseMatrix(n, n);

        for(int i = 0; i < n; i++) {

            if(i % 10000 == 0)
                PsDebug.message("Vertex " + i + " of " + n);


            ArrayList<Integer> neighbours = new ArrayList<>();

            for(int j = 0; j < m; j++)
                if(geometry.getElement(j).contains(i)){
                    PiVector face = geometry.getElement(j);
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

        return L;
    }

    public void run(boolean useLaplace, double[][] A) throws Exception{
        PsDebug.message("RUNNING TASK 2 EXTRAS");

        m_geom = (PgElementSet)super.m_geom;
        m_geom.assureVertexNormals();
        m_geom.assureElementNormals();
        m_geom.makeElementNormals();

        int n_vertices = m_geom.getNumVertices();
        int n_faces = m_geom.getNumElements();


//        PdVector[] laplace_coords = getLaplaceCoords(m_geom, L);
//        PsDebug.message("COMPLETED LAPLACE COORDS");


        double[] x_coords_temp = new double[n_vertices];
        double[] y_coords_temp = new double[n_vertices];
        double[] z_coords_temp = new double[n_vertices];

        for(int i = 0; i < n_vertices; i++){
            x_coords_temp[i] = m_geom.getVertex(i).getEntry(0);
            y_coords_temp[i] = m_geom.getVertex(i).getEntry(1);
            z_coords_temp[i] = m_geom.getVertex(i).getEntry(2);
        }

        PdVector x_coords = new PdVector(x_coords_temp);
        PdVector y_coords = new PdVector(y_coords_temp);
        PdVector z_coords = new PdVector(z_coords_temp);

        PnSparseMatrix L = matrixL_combinatorial(m_geom);

        PdVector x_lapl_coords = null;
        x_lapl_coords = L.leftMultMatrix(x_lapl_coords, x_coords);
        PdVector y_lapl_coords = null;
        y_lapl_coords = L.leftMultMatrix(y_lapl_coords, y_coords);
        PdVector z_lapl_coords = null;
        z_lapl_coords = L.leftMultMatrix(z_lapl_coords, z_coords);

        Task1 task1 = new Task1();
        task1.setGeometries(m_geom);
        PnSparseMatrix G = task1.matrixG();



        PdVector g_x = null;
        PdVector g_y = null;
        PdVector g_z = null;


        if(!useLaplace) {
            g_x = G.leftMultMatrix(g_x, x_coords);
            g_y = G.leftMultMatrix(g_y, y_coords);
            g_z = G.leftMultMatrix(g_z, z_coords);
        }
        else {
            g_x = G.leftMultMatrix(g_x, x_lapl_coords);
            g_y = G.leftMultMatrix(g_y, y_lapl_coords);
            g_z = G.leftMultMatrix(g_z, z_lapl_coords);
        }



        PdVector g_x_tilde = PdVector.copyNew(g_x);
        PdVector g_y_tilde = PdVector.copyNew(g_y);
        PdVector g_z_tilde = PdVector.copyNew(g_z);


        PdMatrix deform = new PdMatrix(A);

        PsDebug.message("CHECKING SELECTED FACES");


        for(int i = 0; i < n_faces; i++){

            if(i % 2000 == 0)
                PsDebug.message("Checked " + i + " faces of " + n_faces);



            PiVector face = m_geom.getElement(i);
            if(face.hasTag(PsObject.IS_SELECTED)){

                PdVector selected_gs_x = new PdVector(3);
                PdVector selected_gs_y = new PdVector(3);
                PdVector selected_gs_z = new PdVector(3);

                for(int j = 0; j < 3; j++) {
                    selected_gs_x.setEntry(j, g_x.getEntry(3 * i + j));
                    selected_gs_y.setEntry(j, g_y.getEntry(3 * i + j));
                    selected_gs_z.setEntry(j, g_z.getEntry(3 * i + j));
                }

                PdVector new_x_tilde = null;
                PdVector new_y_tilde = null;
                PdVector new_z_tilde = null;

                new_x_tilde = deform.leftMultMatrix(new_x_tilde, selected_gs_x);
                new_y_tilde = deform.leftMultMatrix(new_y_tilde, selected_gs_y);
                new_z_tilde = deform.leftMultMatrix(new_z_tilde, selected_gs_z);

                for(int j = 0; j < 3; j++){
                    g_x_tilde.setEntry(3*i + j, new_x_tilde.getEntry(j));
                    g_y_tilde.setEntry(3*i + j, new_y_tilde.getEntry(j));
                    g_z_tilde.setEntry(3*i + j, new_z_tilde.getEntry(j));
                }

            }


        }
        ArrayList<Integer> differentIndices = new ArrayList();
        for(int i = 0; i < g_x_tilde.getSize(); i++){
            if( Math.abs(g_x_tilde.getEntry(i) - g_x.getEntry(i)) > 0.0001 )
                differentIndices.add(i);
        }

        Task1.printSparseMatrix("G", G);
        PsDebug.message("There are " +  differentIndices.size() + " different elements out of " + g_x_tilde.getSize());



        PsDebug.message("STARTING SYSTEM SOLVER");
        PsDebug.message("***************************************");

        solveSystem(G, g_x_tilde, g_y_tilde, g_z_tilde, L, useLaplace);

        PsDebug.message("***************************************");
        PsDebug.message("COMPLETED SYSTEM SOLVER");

    }

    private void solveSystem(PnSparseMatrix G, PdVector g_x_tilde, PdVector g_y_tilde, PdVector g_z_tilde, PnSparseMatrix L, boolean useLaplace) throws Exception{

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

//        Task1.printSparseMatrix("Mv", Mv);
//        Task1.printSparseMatrix("G_transpose", G_transpose);
//        Task1.printSparseMatrix("Gtranspose_Mv", Gtranspose_Mv);


        PsDebug.message("Computed Gtranspose_Mv");

        PnSparseMatrix A = null;
        A = PnSparseMatrix.multMatrices(Gtranspose_Mv, G, A);

        if(useLaplace){
            PnSparseMatrix copy_A = null;
            copy_A = PnSparseMatrix.multMatrices(A, L, copy_A);
            A = copy_A;
        }

        PdVector b_x = null;
        b_x = Gtranspose_Mv.leftMultMatrix(b_x, g_x_tilde);

        PdVector b_y = null;
        b_y = Gtranspose_Mv.leftMultMatrix(b_y, g_y_tilde);

        PdVector b_z = null;
        b_z = Gtranspose_Mv.leftMultMatrix(b_z, g_z_tilde);

        int counter = 0;
        for(int i = 0; i < A.getNumCols() && i < A.getNumRows(); i++)
            if(A.getEntry(i, i) == 0.0){
                A.setEntry(i, i, A.getEntry(i, i) + 0.01);
                counter++;
            }
        PsDebug.message("There were " + counter + " zero elements in the diagonal");


//        long factorization = PnMumpsSolver.factor(A, PnMumpsSolver.Type.GENERAL_SYMMETRIC);
//        PsDebug.message("Completed factorization");
        PdVector x_x = new PdVector();
        PdVector x_y = new PdVector();
        PdVector x_z = new PdVector();

        if(useLaplace) {
            PnBiconjugateGradient solver = new PnBiconjugateGradient();
            solver.solve(A, x_x, b_x);
            solver.solve(A, x_y, b_y);
            solver.solve(A, x_z, b_z);
        }
        else {
            PnConjugateGradientMatrix solver = new PnConjugateGradientMatrix();
            solver.solve(A, x_x, b_x);
            solver.solve(A, x_y, b_y);
            solver.solve(A, x_z, b_z);
        }

        PsDebug.message("Solved all systems");

//        if(useLaplace){
//
//            PnBiconjugateGradient solver_biconjugate = new PnBiconjugateGradient();
//
//            PdVector sol_x = new PdVector();
//            PdVector sol_y = new PdVector();
//            PdVector sol_z = new PdVector();
//
//            solver_biconjugate.solve(L, sol_x, x_x);
//            solver_biconjugate.solve(L, sol_y, x_y);
//            solver_biconjugate.solve(L, sol_z, x_z);
//
//            x_x = sol_x;
//            x_y = sol_y;
//            x_z = sol_z;
//        }


        PsDebug.message("Reached end");
        PsDebug.message("x_x[0] = " + x_x.getEntry(0));
        PsDebug.message("x_x dimension = " + x_x.getSize());

        for(int i = 0; i < m_geom.getNumVertices(); i++){
            m_geom.setVertex(i, x_x.getEntry(i), x_y.getEntry(i), x_z.getEntry(i));
        }

        m_geom.update(m_geom);

        PsDebug.message("DONE");

    }
}