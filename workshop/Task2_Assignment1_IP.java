package workshop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import jv.geom.PgElementSet;
import jv.number.PuDouble;
import jv.object.PsDebug;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jv.objectGui.PsList;
import jv.project.PgGeometryIf;
import jv.project.PvGeometryIf;
import jv.viewer.PvDisplay;
import jvx.project.PjWorkshop_IP;


/**
 * Info Panel of Workshop for surface registration
 *
 */
public class Task2_Assignment1_IP extends PjWorkshop_IP implements ActionListener{
    protected	List			m_listActive;
    protected	List			m_listPassive;
    protected	Vector			m_geomList;
    protected   Button			m_bSetSurfaces;
    protected   Button			m_bChangeDistanceMetric;
    protected   PuDouble        m_kSlider;
    protected   PuDouble        m_pSlider;
    protected   PuDouble        m_precision;
    protected   Button          m_runButton;

    protected Task2_assignment1 m_task2;

    private boolean euclideanDistance = true;

    /** Constructor */
    public Task2_Assignment1_IP() {
        super();
        if (getClass() == Task2_Assignment1_IP.class)
            init();
    }

    /**
     * Informational text on the usage of the dialog.
     * This notice will be displayed if this info panel is shown in a dialog.
     * The text is split at line breaks into individual lines on the dialog.
     */
    public String getNotice() {
        return "This workshop applies the best possible rigid transformation to the surface on the left to fit the surface on the right";
    }

    /** Assign a parent object. */
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task2 = (Task2_assignment1)parent;

        addSubTitle("Select surfaces for task 2");

        /** Panel for the two lists*/
        Panel pGeometries = new Panel();
        pGeometries.setLayout(new GridLayout(1, 2));

        Panel Passive = new Panel();
        Passive.setLayout(new BorderLayout());
        Panel Active = new Panel();
        Active.setLayout(new BorderLayout());
        Label ActiveLabel = new Label("Surface P");
        Active.add(ActiveLabel, BorderLayout.NORTH);
        m_listActive = new PsList(5, true);
        Active.add(m_listActive, BorderLayout.CENTER);
        pGeometries.add(Active);
        Label PassiveLabel = new Label("Surface Q");
        Passive.add(PassiveLabel, BorderLayout.NORTH);
        m_listPassive = new PsList(5, true);
        Passive.add(m_listPassive, BorderLayout.CENTER);
        pGeometries.add(Passive);
        add(pGeometries);

        Panel pSetSurfaces = new Panel(new BorderLayout());
        m_bSetSurfaces = new Button("Set selected surfaces");
        m_bSetSurfaces.addActionListener(this);
        pSetSurfaces.add(m_bSetSurfaces, BorderLayout.CENTER);
        add(pSetSurfaces);

        Panel pChangeDistance = new Panel(new BorderLayout());
        m_bChangeDistanceMetric = new Button("EUCLIDEAN DISTANCE");
        m_bChangeDistanceMetric.addActionListener(this);
        pChangeDistance.add(m_bChangeDistanceMetric, BorderLayout.CENTER);
        add(pChangeDistance);

        Panel pParameters = new Panel();
        pParameters.setLayout(new GridLayout(1, 2));

        Panel p_kParam = new Panel();
        p_kParam.setLayout(new BorderLayout());
        Panel p_pParam = new Panel();
        p_pParam.setLayout(new BorderLayout());

        m_kSlider = new PuDouble("K parameter");
        m_kSlider.setDefValue(1.5);
        m_kSlider.setDefBounds(1,10,0.5,1);
        m_kSlider.addUpdateListener(this);
        m_kSlider.init();
        p_kParam.add(m_kSlider.getInfoPanel());

        m_pSlider = new PuDouble("P parameter");
        m_pSlider.setDefValue(0.1);
        m_pSlider.setDefBounds(0.01,0.9,0.1,1);
        m_pSlider.addUpdateListener(this);
        m_pSlider.init();
        p_pParam.add(m_pSlider.getInfoPanel());

        pParameters.add(p_kParam);
        pParameters.add(p_pParam);

        add(pParameters);

        Panel p_precision = new Panel();
        p_precision.setLayout(new BorderLayout());

        m_precision = new PuDouble("Precision for convergence");
        m_precision.setDefValue(0.01);
        m_precision.setDefBounds(0.0001,0.5,0.001,1);
        m_precision.addUpdateListener(this);
        m_precision.init();
        p_precision.add(m_precision.getInfoPanel());
        add(p_precision);

        Panel pRunButton = new Panel(new BorderLayout());
        m_runButton = new Button("RUN");
        m_runButton.addActionListener(this);
        pRunButton.add(m_runButton, BorderLayout.CENTER);
        add(pRunButton);

        updateGeomList();
        validate();
    }

    /** Initialisation */
    public void init() {
        super.init();
        setTitle("Surface Registration");

    }

    public boolean update(Object event) {
        if (event == m_kSlider) {
            m_task2.setK(m_kSlider.getValue());
//            m_task2.m_geom.update(m_task2.m_geom);
            return true;
        }
        else if(event == m_pSlider){
            m_task2.setP(m_pSlider.getValue());
            return true;
        }
        else if(event == m_precision){
            m_task2.setPrecision(m_precision.getValue());
            return true;
        }
        else
            return super.update(event);
    }

    /** Set the list of geometries in the lists to the current state of the display. */
    public void updateGeomList() {
        Vector displays = m_task2.getGeometry().getDisplayList();
        int numDisplays = displays.size();
        m_geomList = new Vector();
        for (int i=0; i<numDisplays; i++) {
            PvDisplay disp =((PvDisplay)displays.elementAt(i));
            PgGeometryIf[] geomList = disp.getGeometries();
            int numGeom = geomList.length;
            for (int j=0; j<numGeom; j++) {
                if (!m_geomList.contains(geomList[j])) {
                    //Take just PgElementSets from the list.
                    if (geomList[j].getType() == PvGeometryIf.GEOM_ELEMENT_SET)
                        m_geomList.addElement(geomList[j]);
                }
            }
        }
        int nog = m_geomList.size();
        m_listActive.removeAll();
        m_listPassive.removeAll();
        for (int i=0; i<nog; i++) {
            String name = ((PgGeometryIf)m_geomList.elementAt(i)).getName();
            m_listPassive.add(name);
            m_listActive.add(name);
        }
    }
    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bSetSurfaces) {
            m_task2.setGeometries((PgElementSet)m_geomList.elementAt(m_listActive.getSelectedIndex()),
                    (PgElementSet)m_geomList.elementAt(m_listPassive.getSelectedIndex()));
            return;
        }
        else if (source == m_runButton) {
            try {
                m_task2.run();
            }
            catch (RuntimeException e){
                PsDebug.message("EXCEPTION!");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                PsDebug.message(sw.toString());
            }
        }
        else if(source == m_bChangeDistanceMetric){
            if(euclideanDistance){
                m_bChangeDistanceMetric.setLabel("POINT TO PLANE DISTANCE");
                euclideanDistance = false;
                m_task2.setEuclidean(false);
            }
            else {
                m_bChangeDistanceMetric.setLabel("EUCLIDEAN DISTANCE");
                euclideanDistance = true;
                m_task2.setEuclidean(true);
            }
        }
    }
    /**
     * Get information which bottom buttons a dialog should create
     * when showing this info panel.
     */
    protected int getDialogButtons()		{
        return PsDialog.BUTTON_OK;
    }
}
