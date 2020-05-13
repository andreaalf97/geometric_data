package workshop;

import java.awt.*;
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

public class Analysis_IP extends PjWorkshop_IP implements ActionListener{

    protected List      m_list;
    protected List      m_listPassive;
    protected Vector	m_geomList;
    protected Button    m_bChooseCalculation;
    protected Analysis  m_task1;

    /** Constructor */
    public Analysis_IP () {
        super();
        if (getClass() == Analysis_IP.class)
            init();
    }

    /**
     * Informational text on the usage of the dialog.
     * This notice will be displayed if this info panel is shown in a dialog.
     * The text is split at line breaks into individual lines on the dialog.
     */
    public String getNotice() {
        return "This workshop calculates some properties of the surface.";
    }

    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task1 = (Analysis)parent;

        addSubTitle("Select calculation to be executed");
        //Panel for the two lists
        Panel pGeometries = new Panel();
        pGeometries.setLayout(new GridLayout(1, 2));

        Panel Passive = new Panel();
        Passive.setLayout(new BorderLayout());
        Label PassiveLabel = new Label("Surface Q");
        Passive.add(PassiveLabel, BorderLayout.NORTH);
        m_listPassive = new PsList(5, true);
        Passive.add(m_listPassive, BorderLayout.CENTER);
        pGeometries.add(Passive);
        Panel Active = new Panel();
        Active.setLayout(new BorderLayout());
        Label ActiveLabel = new Label("Options");
        Active.add(ActiveLabel, BorderLayout.NORTH);
        m_list = new PsList(3, true);
        m_list.add("Genus");
        m_list.add("Volume");
        m_list.add("Components");
        Active.add(m_list, BorderLayout.CENTER);
        pGeometries.add(Active);
        add(pGeometries);

        Panel pSetSurfaces = new Panel(new BorderLayout());
        m_bChooseCalculation = new Button("Run");
        m_bChooseCalculation.addActionListener(this);
        pSetSurfaces.add(m_bChooseCalculation, BorderLayout.CENTER);
        add(pSetSurfaces);

        updateGeomList();
        validate();
    }

    /** Set the list of geometries in the lists to the current state of the display. */
    public void updateGeomList() {
        Vector displays = m_task1.m_geom.getDisplayList();

        int numDisplays = displays.size();
        m_geomList = new Vector();
        for (int i=0; i<numDisplays; i++) {
            PvDisplay disp = ((PvDisplay)displays.elementAt(i));
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
        m_listPassive.removeAll();
        for (int i=0; i<nog; i++) {
            String name = ((PgGeometryIf)m_geomList.elementAt(i)).getName();
            m_listPassive.add(name);
        }
    }

    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bChooseCalculation)
            m_task1.setGeometry((PgElementSet)m_geomList.elementAt(m_listPassive.getSelectedIndex()));
        m_task1.calculations(m_list.getSelectedIndex());
    }

    /**
     * Get information which bottom buttons a dialog should create
     * when showing this info panel.
     */
    protected int getDialogButtons() {
        return PsDialog.BUTTON_OK;
    }
}