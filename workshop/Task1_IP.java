package workshop;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import jv.number.PuDouble;
import jv.object.PsConfig;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class Task1_IP extends PjWorkshop_IP implements ActionListener {

    protected Button    m_matrixG;
    protected Button    m_matrixL;
    protected Button    m_matrixS;
    protected Button    m_matrixM;


    protected Task1     m_task1;

    /** Constructor */
    public Task1_IP () {
        super();
        if (getClass() == Task1_IP.class)
            init();
    }

    public void init() {
        super.init();
        setTitle("Task1");
    }

    public String getNotice() {
        return "This workshop computes the matrices.";
    }

    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_task1 = (Task1)parent;

        addSubTitle("Select matrix to compute");

        m_matrixG = new Button("Matrix G");
        m_matrixG.addActionListener(this);
        m_matrixL = new Button("Matrix L");
        m_matrixL.addActionListener(this);
        Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel1.add(m_matrixG);
        panel1.add(m_matrixL);
        add(panel1);

        m_matrixS = new Button("Matrix S");
        m_matrixS.addActionListener(this);
        m_matrixM = new Button("Matrix M");
        m_matrixM.addActionListener(this);
        Panel panel2 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel2.add(m_matrixS);
        panel2.add(m_matrixM);
        add(panel2);

        validate();
    }


   /* public boolean update(Object event) {
        if (event == m_xOff) {
            m_ws.setXOff(m_xOff.getValue());
            m_ws.m_geom.update(m_ws.m_geom);
            return true;
        }
        if (event == m_rotateY) {
            m_ws.setRotateY(m_rotateY.getValue());
            m_ws.m_geom.update(m_ws.m_geom);
            return true;
        } else
            return super.update(event);
    }*/

    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_matrixG) {
            m_task1.matrixG();
            return;
        }
        else if (source == m_matrixL) {
            m_task1.matrixL();
            return;
        }
        else if (source == m_matrixS) {
            m_task1.matrixS();
            return;
        }
        else if (source == m_matrixM) {
            m_task1.matrixM();
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
