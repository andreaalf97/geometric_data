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
import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop_IP;


/**
 * Info Panel of Workshop for surface registration
 *
 */
public class Task2_IP extends PjWorkshop_IP implements ActionListener{
    protected	List			m_listActive;
    protected	List			m_listPassive;
    protected	Vector			m_geomList;
    protected   PuDouble        m_pSlider;
    protected   Button          m_runButton;
    protected   Button          m_undoButton;
    protected   TextField[]     m_textFields = new TextField[9];

    protected Task2 m_task2;

    private boolean euclideanDistance = true;

    /** Constructor */
    public Task2_IP () {
        super();
        if (getClass() == Task2_IP.class)
            init();
    }

    /** Initialisation */
    public void init() {
        super.init();
        setTitle("Shape editing");
    }

    /**
     * Informational text on the usage of the dialog.
     * This notice will be displayed if this info panel is shown in a dialog.
     * The text is split at line breaks into individual lines on the dialog.
     */
    public String getNotice() {
        return "This workshop offers tools for shape editing using differential coordinates.";
    }


    /** Assign a parent object. */
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task2 = (Task2)parent;

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
        Panel pVertexButton = new Panel(new BorderLayout());
        m_runButton = new Button("Vertex Coordinates");
        m_runButton.addActionListener(this);
        pVertexButton.add(m_runButton, BorderLayout.CENTER);
        Panel pLaplaceButton = new Panel(new BorderLayout());
        m_runButton = new Button("Laplace Coordinates");
        m_runButton.addActionListener(this);
        pLaplaceButton.add(m_runButton, BorderLayout.CENTER);
        Panel pUndoButton = new Panel(new BorderLayout());
        m_undoButton = new Button("Undo");
        m_undoButton.addActionListener(this);
        pUndoButton.add(m_undoButton, BorderLayout.CENTER);
        pButtons.add(pVertexButton);
        pButtons.add(pLaplaceButton);
        pButtons.add(pUndoButton);
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
                for(int i = 0; i < 9; i++){
                    A[i/3][i%3] = Double.parseDouble(m_textFields[i].getText());
                }
                PsDebug.message(A.toString());
                m_task2.run(A);
                return;
            }
            catch (RuntimeException e){
                PsDebug.message("EXCEPTION!");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                PsDebug.message(sw.toString());
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
