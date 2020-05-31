package menu;

import jv.geom.PgPointSet_Menu;
import jv.geom.PgElementSet;
import jv.geom.PgPointSet;
import jv.object.PsDebug;
import jv.object.PsDialog;
import jv.objectGui.PsMethodMenu;
import jv.object.PsObject;
import jv.project.PgGeometryIf;
import jv.project.PvDisplayIf;
import jv.project.PvViewerIf;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop_Dialog;
import workshop.*;

public class PgElementSet_Menu extends PgPointSet_Menu {
	
	private enum MenuEntry{
		MyWorkshop			("MyWorkshop..."),
		Registration		("Surface Registration..."),
		Task2				("Task 2..."),
		Task1				("Task 1...")
		// Additional entries...
		;
		protected final String name;
		MenuEntry(String name) { this.name  = name; }
		
		public static MenuEntry fromName(String name){
			for (MenuEntry entry : MenuEntry.values()) {
				if(entry.name.equals(name)) return entry;
			}
			return null;
		}
	}	
	
	protected PgElementSet m_elementSet;
	
	protected PvViewerIf m_viewer;

	public void init(PsObject anObject) {
		super.init(anObject);
		m_elementSet = (PgElementSet)anObject;
		
		String menuDev = "My Workshops";
		addMenu(menuDev);
		for (MenuEntry entry : MenuEntry.values()) {
			addMenuItem(menuDev, entry.name);
		}
	}
	
	public boolean applyMethod(String aMethod) {
		if (super.applyMethod(aMethod))
			return true;

		if (PsDebug.NOTIFY) PsDebug.notify("trying method = "+aMethod);

		PvDisplayIf currDisp = null;
		if (getViewer() == null) {
			if (PsDebug.WARNING) PsDebug.warning("missing viewer");
		} else {
			currDisp = getViewer().getDisplay();
			if (currDisp == null) PsDebug.warning("missing display.");
		}

		PsDialog dialog;
		MenuEntry entry = MenuEntry.fromName(aMethod);
		if(entry == null) return false;
		switch (entry) {
		case MyWorkshop:
			MyWorkshop ws = new MyWorkshop();
			ws.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				ws.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(ws);
			dialog.update(ws);
			dialog.setVisible(true);
			break;
		case Registration:
			Registration reg = new Registration();
			reg.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				reg.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(reg);
			dialog.update(reg);
			dialog.setVisible(true);
			break;
		case Task2:
			Task2 task2 = new Task2();
			task2.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				task2.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(task2);
			dialog.update(task2);
			dialog.setSize(700, 400);
			dialog.setVisible(true);
			break;
			
		case Task1:
			Task1 task1 = new Task1();
			task1.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				task1.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(task1);
			dialog.update(task1);
			dialog.setVisible(true);
			break;
		}
		
		return true;
	}
	
	public PvViewerIf getViewer() { return m_viewer; }

	public void setViewer(PvViewerIf viewer) { m_viewer = viewer; }		
	
}	