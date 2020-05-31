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
 *  Workshop for matrices computation.
 */

public class Task1 extends PjWorkshop {

    /** Mesh to compute matrices. */
    PgElementSet m_geom;

    /** Constructor */
    public Task1() {
        super("Differential Coordinates");
        if (getClass() == Task1.class) {
            init();
        }
    }

    /** Initialization */
    public void init() {
        super.init();
    }


    /** Set two Geometries. */
    public void setGeometries(PgElementSet geom) {
        m_geom = geom;
    }

    /** Compute gradient matrix G. */
    public void matrixG() {

        /*OUT OF THE FUNCTION?*/
        //Number of faces
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

           //add local matrix to global matrix
           for (int j = 0; j < 3; j++) {
               for (int k = 0; k < 3; k++) {
                   entry = G_local.getEntry(j, k);
                   G.addEntry(3 * i + j, loc_vertices.getEntry(k), entry);
               }
           }
        }
    }

    private PdMatrix calcLocalGradientMatrix(int num_triangle) {

        PiVector vertices = m_geom.getElement(num_triangle);

        PdVector e_1 = m_geom.getVertex(vertices.getEntry(2))-m_geom.getVertex(vertices.getEntry(1));
        PdVector e_2 = m_geom.getVertex(vertices.getEntry(0))-m_geom.getVertex(vertices.getEntry(2));
        PdVector e_3 = m_geom.getVertex(vertices.getEntry(1))-m_geom.getVertex(vertices.getEntry(0));

        PdVector N = m_geom.getElementNormal(num_triangle);

        PdVector N_x_e1 = PdVector.crossNew(N,e_1);
        PdVector N_x_e2 = PdVector.crossNew(N,e_2);
        PdVector N_x_e3 = PdVector.crossNew(N,e_3);

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

        return b;
    }

    /** Compute combinatiorial matrix L. */
    public void matrixL() {

    }

    /** Compute contangent matrix S. */
    public void matrixS() {


    }

    /** Compute mass matrix M. */
    public void matrixM() {

    }
}