package workshop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import jv.object.PsDebug;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class Task2_Extras_IP extends PjWorkshop_IP implements ActionListener {

    protected Button m_runButton;
    protected Button m_resetButton;

    protected Task2_Extras m_task2Extras;

    /** Constructor */
    public Task2_Extras_IP () {
        super();
        if (getClass() == Task2_Extras_IP.class)
            init();
    }

    public void init() {
        super.init();
        setTitle("Task2_Extras");
    }

    public String getNotice() {
        return "This workshop computes the matrices.";
    }

    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task2Extras = (Task2_Extras)parent;

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
                for(int i = 0; i < 9; i++) {
                    A[i / 3][i % 3] = Double.parseDouble(m_textFields[i].getText());
                }
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
