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

    protected List m_list;
    protected Button m_bChooseCalculation;
    Analysis m_task1;

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

        Panel pGeometries = new Panel();
        pGeometries.setLayout(new GridLayout(1, 2));

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
        m_bChooseCalculation = new Button("Choose");
        m_bChooseCalculation.addActionListener(this);
        pSetSurfaces.add(m_bChooseCalculation, BorderLayout.CENTER);
        add(pSetSurfaces);
        validate();
    }

    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bChooseCalculation)
            m_task1.genus(m_list.getSelectedIndex());


    }















}
