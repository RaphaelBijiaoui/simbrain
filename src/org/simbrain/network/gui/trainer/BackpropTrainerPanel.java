/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.gui.trainer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.trainers.BackpropTrainer;
import org.simbrain.network.trainers.ErrorListener;
import org.simbrain.resource.ResourceManager;
import org.simbrain.util.genericframe.GenericFrame;

/**
 * Panel for controlling iterative trainers.  Can be reused by any GUI element that 
 * invokes an iterative trainer (i.e. any subclass of IterableTtrainer).
 * 
 * @author jeffyoshimi
 */
public class BackpropTrainerPanel extends JPanel {
	
    /** Parent frame. */
    private GenericFrame parentFrame;
    
    /** Reference to trainer object. */
    private final BackpropTrainer trainer;
    
    /** Reference to network panel. */
    private final NetworkPanel panel;

    /**
     * Construct the panel.
     *
     * @param networkPanel parent frame
     * @param trainer the trainer to control
     */
    public BackpropTrainerPanel(final NetworkPanel networkPanel,
			BackpropTrainer trainer) {
    	this.trainer = trainer;
    	this.panel = networkPanel;
        setLayout(new BorderLayout());
        
        // Use iterative controls for main panel
    	add("Center", new IterativeControlsPanel(networkPanel, trainer));
    	
    	// Put backprop specific controls at the bottom
    	JPanel southPanel = new JPanel(new BorderLayout());
    	southPanel.add("North", new JSeparator(SwingConstants.HORIZONTAL));
    	JPanel panel = new JPanel();
    	JButton propertiesButton = new JButton(TrainerGuiActions.getPropertiesDialogAction(trainer));
    	//propertiesButton.setHideActionText(true);
    	panel.add(propertiesButton);
    	panel.add(new JButton(randomizeAction));
    	southPanel.add("Center", panel);
    	add("South", southPanel);
    	
        // Add listener
        trainer.addErrorListener(new ErrorListener() {

            public void errorUpdated() {
                parentFrame.pack();
            }
            
        });

	}
    
    /**
     * Action for randomizing the underlying network.
     */
	Action randomizeAction = new AbstractAction() {

		// Initialize
		{
			putValue(SMALL_ICON, ResourceManager.getImageIcon("Rand.png"));
			putValue(NAME, "Randomize");
			putValue(SHORT_DESCRIPTION, "Randomize network");
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent arg0) {
			if (trainer != null) {
				trainer.randomize();
				panel.getRootNetwork().fireNetworkChanged();
			}
		}
	};

    /**
     * @param parentFrame the parentFrame to set
     */
    public void setFrame(GenericFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
}