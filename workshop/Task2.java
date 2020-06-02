package workshop;

import java.awt.Color;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.object.PsObject;
import jv.vecmath.PiVector;
import jv.vecmath.*;
import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop;
import javax.lang.model.element.Element;

/**
 *  Workshop for surface registration
 */

public class Task2 extends PjWorkshop {
    PgElementSet m_geom;
    Task1        m_task1;

    /**
     * Constructor
     */
    public Task2() {
        super("Shape editing");
        if (getClass() == Task2.class) {
            init();
        }
    }

    /**
     * Initialization
     */
    public void init() {
        super.init();
        m_geom = (PgElementSet)super.m_geom;
    }

    /**
     * Set Geometry.
     */
    public void setGeometries(PgElementSet geom) {
        super.setGeometry(geom);
        m_geom = (PgElementSet)super.m_geom;
    }

    /**
     * This function is called when the RUN button is pressed
     */
    public void run(double[][] A) {

    }
}