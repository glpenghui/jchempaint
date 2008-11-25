/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Stefan Kuhn
 *
 *  Contact: cdk-jchempaint@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jchempaint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.jchempaint.action.SaveAction;

public class JChemPaintPanel extends JPanel implements IChemObjectListener {

	RenderPanel p;
	
	private JComponent lastActionButton;

	private File currentWorkDirectory;

	private File lastOpenedFile;

	private FileFilter currentOpenFileFilter;

	private File isAlreadyAFile;
	private boolean isModified=false;

	private FileFilter currentSaveFileFilter;
	private JCPStatusBar statusBar;

	public static List<JChemPaintPanel> instances = new ArrayList<JChemPaintPanel>();
	public JComponent getActionButton() {
		return lastActionButton;
	}
	public void setActionButton(JComponent actionButton) {
		lastActionButton = actionButton;
	}
	
	/**
	 *  Gets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@return    The currentWorkDirectory value
	 */
	public File getCurrentWorkDirectory() {
		return currentWorkDirectory;
	}


	/**
	 *  Sets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@param  cwd  The new currentWorkDirectory value
	 */
	public void setCurrentWorkDirectory(File cwd) {
		this.currentWorkDirectory = cwd;
	}

	/**
	 *  Gets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@return    The lastOpenedFile value
	 */
	public File getLastOpenedFile() {
		return lastOpenedFile;
	}


	/**
	 *  Sets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@param  lof  The new lastOpenedFile value
	 */
	public void setLastOpenedFile(File lof) {
		this.lastOpenedFile = lof;
	}

	/**
	 *  Gets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentOpenFileFilter value
	 */
	public FileFilter getCurrentOpenFileFilter() {
		return currentOpenFileFilter;
	}


	/**
	 *  Sets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentOpenFileFilter value
	 */
	public void setCurrentOpenFileFilter(FileFilter ff) {
		this.currentOpenFileFilter = ff;
	}
	
	/**
	 *  Gets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentSaveFileFilter value
	 */
	public FileFilter getCurrentSaveFileFilter() {
		return currentSaveFileFilter;
	}


	/**
	 *  Sets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentSaveFileFilter value
	 */
	public void setCurrentSaveFileFilter(FileFilter ff) {
		this.currentSaveFileFilter = ff;
	}
	
	/**
	 *  Sets the file currently used for saving this Panel.
	 *
	 *@param  value  The new isAlreadyAFile value
	 */
	public void setIsAlreadyAFile(File value) {
		isAlreadyAFile = value;
	}


	/**
	 *  Returns the file currently used for saving this Panel, null if not yet
	 *  saved
	 *
	 *@return    The currently used file
	 */
	public File isAlreadyAFile() {
		return isAlreadyAFile;
	}

	public JChemPaintPanel(IChemModel ac, String gui){
		this.setLayout(new BorderLayout());
		JMenuBar menu = new JChemPaintMenuBar(this, gui);
		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.setLayout(new BorderLayout());
		this.add(topContainer,BorderLayout.NORTH);
		topContainer.add(menu,BorderLayout.NORTH);
		p = new RenderPanel(ac);
		this.add(p,BorderLayout.CENTER);
		JToolBar toolbar = JCPToolBar.getToolbar(this, 1);
		topContainer.add(toolbar,BorderLayout.CENTER);
		statusBar=new JCPStatusBar();
		this.add(statusBar,BorderLayout.SOUTH);
		instances.add(this);
		ac.addListener(this);
	}

	public Controller2DHub get2DHub() {
		return p.getHub();
	}

	public int showWarning() {
		if (isModified){ //TODO && !getIsOpenedByViewer() && !guiString.equals("applet")) {
			int answer = JOptionPane.showConfirmDialog(this, p.getChemModel().getID() + " " + JCPLocalizationHandler.getInstance().getString("warning"), JCPLocalizationHandler.getInstance().getString("warningheader"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				new SaveAction(this, false).actionPerformed(new ActionEvent(this, 12, ""));
			}
			return answer;
		/* TODO } else if(guiString.equals("applet")){
			//In case of the applet we do not ask for save but put the clear into the undo stack
			ClearAllEdit coa = null;
			try {
				coa = new ClearAllEdit(this.getChemModel(),(IMoleculeSet)this.getChemModel().getMoleculeSet().clone(),this.getChemModel().getReactionSet());
				this.jchemPaintModel.getControllerModel().getUndoSupport().postEdit(coa);
			} catch (Exception e) {
				logger.error("Clone of IMoleculeSet failed: ", e.getMessage());
            	logger.debug(e);
			}
			return JOptionPane.YES_OPTION;*/
		} else {
			return JOptionPane.YES_OPTION;
		}
	}

	/**
	 *  Class for closing jcp
	 *
	 *@author     shk3
	 *@cdk.created    November 23, 2008
	 */
	public final static class AppCloser extends WindowAdapter {

		/**
		 *  closing Event. Shows a warning if this window has unsaved data and
		 *  terminates jvm, if last window.
		 *
		 *@param  e  Description of the Parameter
		 */
		public void windowClosing(WindowEvent e) {
			JFrame rootFrame = (JFrame) e.getSource();
			/*TODO if (rootFrame.getContentPane().getComponent(0) instanceof JChemPaintEditorPanel) {
				JChemPaintEditorPanel panel = (JChemPaintEditorPanel) rootFrame.getContentPane().getComponent(0);
				panel.fireChange(JChemPaintEditorPanel.JCP_CLOSING);
			}*/
			int clear = ((JChemPaintPanel) ((JFrame) e.getSource()).getContentPane().getComponents()[0]).showWarning();
			if (JOptionPane.CANCEL_OPTION != clear) {
				for (int i = 0; i < instances.size(); i++) {
					if (((JPanel)instances.get(i)).getParent().getParent().getParent().getParent() == (JFrame)e.getSource()) {
						instances.remove(i);
						break;
					}
				}
				((JFrame) e.getSource()).setVisible(false);
				((JFrame) e.getSource()).dispose();
				if (instances.size() == 0){//TODO && !((JChemPaintPanel)rootFrame.getContentPane().getComponent(0)).isEmbedded()) {
					System.exit(0);
				}
			}
		}
	}
	
	public IChemModel getChemModel(){
		return p.getChemModel();
	}
	
	public void stateChanged(IChemObjectChangeEvent event) {
		isModified=true;
		((JFrame)this.getParent().getParent().getParent().getParent()).setTitle(p.getChemModel().getID()+"*");
        /* TODO if (this.getChemModel() != null) {
            for (int i = 0; i < 3; i++) {
              String status = p.getStatus(i);
              statusBar.setStatus(i + 1, status);
            }
        } else {
            if (statusBar != null) {
              statusBar.setStatus(1, "no model");
            }
        }*/
    }
}