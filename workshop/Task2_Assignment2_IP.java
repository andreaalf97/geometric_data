package workshop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import jv.number.PuDouble;
import jv.object.PsDebug;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class Task2_Assignment2_IP extends PjWorkshop_IP implements ActionListener {

    protected	List			m_listActive;
    protected	List			m_listPassive;
    protected   Vector          m_geomList;
    protected   PuDouble        m_pSlider;
    protected   Button          m_setVertexButton;
    protected   Button          m_setLaplaceButton;
    protected   Button          m_runButton;
    protected   TextField[]     m_textFields = new TextField[9];

    protected boolean           useLaplace = false;
    protected Task2_Assignment2 m_task2_assignment2;

    /** Constructor */
    public Task2_Assignment2_IP() {
        super();
        if (getClass() == Task2_Assignment2_IP.class)
            init();
    }

    public void init() {
        super.init();
        setTitle("Task2_Assignment2");
    }

    public String getNotice() {
        return "This workshop computes the matrices.";
    }

    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task2_assignment2 = (Task2_Assignment2)parent;

        addSubTitle("Insert values of table A: ");

        /** Panel*/
        Panel pGeometries = new Panel();
        pGeometries.setLayout(new GridLayout(2, 1));

        Panel pTextFields = new Panel(new BorderLayout());
        pTextFields.setLayout(new GridLayout(3, 3));
        for(int i=0; i<9; i++) {
            m_textFields[i] = new TextField("0");
            pTextFields.add(m_textFields[i], BorderLayout.CENTER);
        }
        pGeometries.add(pTextFields);

        Panel pButtons = new Panel(new BorderLayout());
        pButtons.setLayout(new GridLayout(3, 1));

        Panel pVertexPanel = new Panel(new BorderLayout());
        m_setVertexButton = new Button("Vertex Coordinates");
        m_setVertexButton.addActionListener(this);
        pVertexPanel.add(m_setVertexButton, BorderLayout.CENTER);

        Panel pLaplacePanel = new Panel(new BorderLayout());
        m_setLaplaceButton = new Button("Laplace Coordinates");
        m_setLaplaceButton.addActionListener(this);
        pLaplacePanel.add(m_setLaplaceButton, BorderLayout.CENTER);

        Panel runPanel = new Panel(new BorderLayout());
        m_runButton = new Button("RUN");
        m_runButton.addActionListener(this);
        runPanel.add(m_runButton, BorderLayout.CENTER);

        pButtons.add(pVertexPanel);
        pButtons.add(pLaplacePanel);
        pButtons.add(runPanel);

        pGeometries.add(pButtons);
        add(pGeometries);

        validate();
    }

    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_runButton) {
            try {
                double[][] A = new double[3][3];
                for(int i = 0; i < 9; i++) {
                    A[i / 3][i % 3] = Double.parseDouble(m_textFields[i].getText());
                }
                m_task2_assignment2.run(useLaplace, A);
                return;
            }
            catch (Exception e){
                PsDebug.message("EXCEPTION!");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                PsDebug.message(sw.toString());
            }
        }
        else if(source == m_setLaplaceButton){
            this.useLaplace = true;
            PsDebug.message("Using Laplace Coords");
            return;
        }
        else if(source == m_setVertexButton){
            this.useLaplace = false;
            PsDebug.message("Using Vertex Coords");
            return;
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
