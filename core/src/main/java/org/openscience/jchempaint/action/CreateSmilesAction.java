/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 17:26:00 +0000 (Thu, 04 Jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2008 Christoph Steinbeck, Stefan Kuhn
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
package org.openscience.jchempaint.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFrame;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.jchempaint.GT;
import org.openscience.jchempaint.dialog.TextViewDialog;


/**
 * Creates a SMILES from the current model
 *
 */
public class CreateSmilesAction extends JCPAction
{

	private static final long serialVersionUID = -4886982931009753342L;
	
	TextViewDialog dialog = null;
	JFrame frame = null;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Trying to create smile: ", type);
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "SMILES", null, false, 40, 2);
			dialog.setName("smilestextdialog");
		}
		try
		{
			String smiles = getSmiles(jcpPanel.getChemModel());
			dialog.setMessage(GT._("Generated SMILES:"),
                              "SMILES: " + smiles);
		} catch (Exception exception)
		{
			String message = GT._("Error while creating SMILES:") + " " + exception.getMessage();
			logger.error(message);
			logger.debug(exception);
			dialog.setMessage(GT._("Error"), message);
		}
		dialog.setVisible(true);
	}
	
	public static String getSmiles(IChemModel model) throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException{
		return getChiralSmiles(model);
	}

    @Deprecated
	public static String getChiralSmiles(IChemModel model) throws CDKException, ClassNotFoundException, IOException, CloneNotSupportedException {
        
        SmilesGenerator smigen = SmilesGenerator.isomeric();
        
		StringBuilder sb = new StringBuilder();
        for(IAtomContainer container : ChemModelManipulator.getAllAtomContainers(model)) {
            if (sb.length() > 0)
                sb.append('.');
            sb.append(smigen.create(container));
        }
        
        return sb.toString();
	}

}

