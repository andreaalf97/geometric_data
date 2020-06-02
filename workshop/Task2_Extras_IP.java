package workshop;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
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

        addSubTitle("RUN EXTRAS");

        m_runButton = new Button("RUN");
        m_runButton.addActionListener(this);
        m_resetButton = new Button("Reset");
        m_resetButton.addActionListener(this);
        Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel1.add(m_runButton);
        panel1.add(m_resetButton);
        add(panel1);

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
        if (source == m_runButton) {
            try {
                m_task2Extras.run(true);
            }
            catch (RuntimeException e){
                PsDebug.message("RUNTIME EXCEPTION!");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                PsDebug.message(sw.toString());
            }
            catch (Exception e){
                PsDebug.message("EXCEPTION!");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                PsDebug.message(sw.toString());
            }

        }
        else if (source == m_resetButton) {
            try {

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
