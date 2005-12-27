/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005 Jeff Yoshimi <www.jeffyoshimi.net>
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
package org.simbrain.network.dialog.neuron;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.simbrain.network.NetworkUtils;
import org.simbrain.network.nodes.*;
import org.simbrain.util.LabelledItemPanel;
import org.simbrain.util.StandardDialog;
import org.simnet.interfaces.Network;
import org.simnet.interfaces.Neuron;
import org.simnet.neurons.AdditiveNeuron;
import org.simnet.neurons.BinaryNeuron;
import org.simnet.neurons.ClampedNeuron;
import org.simnet.neurons.DecayNeuron;
import org.simnet.neurons.IACNeuron;
import org.simnet.neurons.IntegrateAndFireNeuron;
import org.simnet.neurons.IzhikevichNeuron;
import org.simnet.neurons.LinearNeuron;
import org.simnet.neurons.LogisticNeuron;
import org.simnet.neurons.NakaRushtonNeuron;
import org.simnet.neurons.RandomNeuron;
import org.simnet.neurons.SigmoidalNeuron;
import org.simnet.neurons.SinusoidalNeuron;
import org.simnet.neurons.StochasticNeuron;


/**
 * <b>DialogNetwork</b> is a dialog box for setting the properties of the  Network GUI.
 */
public class NeuronDialog extends StandardDialog implements ActionListener {
    public static final String NULL_STRING = "...";
    private Box mainPanel = Box.createVerticalBox();
    private LabelledItemPanel topPanel = new LabelledItemPanel();
    private AbstractNeuronPanel neuronPanel;
    private JComboBox cbNeuronType = new JComboBox(Neuron.getTypeList());
    private JTextField tfActivation = new JTextField();
    private JTextField tfIncrement = new JTextField();
    private JTextField tfUpBound = new JTextField();
    private JTextField tfLowBound = new JTextField();
    private JLabel upperLabel = new JLabel("Upper bound");
    private JLabel lowerLabel = new JLabel("Lower bound");
    private ArrayList neuron_list = new ArrayList(); // The neurons being modified
    private ArrayList selection_list; // The pnodes which refer to them
    private boolean neuronsHaveChanged = false;

    /**
     * @param selectedNeurons the pnode_neurons being adjusted
     */
    public NeuronDialog(final ArrayList selectedNeurons) {
        selection_list = selectedNeurons;
        setNeuronList();
        init();
    }

    /**
     * Get the logical neurons from the NeuronNodes
     */
    private void setNeuronList() {
        neuron_list.clear();

        Iterator i = selection_list.iterator();

        while (i.hasNext()) {
            NeuronNode n = (NeuronNode) i.next();
            neuron_list.add(n.getNeuron());
        }
    }

    /**
     * Initialises the components on the panel.
     */
    private void init() {
        setTitle("Neuron Dialog");
        setLocation(500, 0);

        initNeuronType();
        fillFieldValues();
        cbNeuronType.addActionListener(this);

        topPanel.addItem("Activation", tfActivation);
        topPanel.addItem("Increment", tfIncrement);
        topPanel.addItemLabel(upperLabel, tfUpBound);
        topPanel.addItemLabel(lowerLabel, tfLowBound);
        topPanel.addItem("Neuron type", cbNeuronType);

        mainPanel.add(topPanel);
        mainPanel.add(neuronPanel);
        setContentPane(mainPanel);

    }

    /** @see StandardDialog */
    protected void closeDialogOk() {
        super.closeDialogOk();
        commitChanges();
    }

    /**
     * Initialize the main neuron panel based on the type of the selected neurons.
     */
    public void initNeuronType() {
        Neuron neuron_ref = (Neuron) neuron_list.get(0);

        if (!NetworkUtils.isConsistent(neuron_list, Neuron.class, "getType")) {
            cbNeuronType.addItem(AbstractNeuronPanel.NULL_STRING);
            cbNeuronType.setSelectedIndex(Neuron.getTypeList().length);
            neuronPanel = new ClampedNeuronPanel(); // Simply to serve as an empty panel
        } else if (neuron_ref instanceof BinaryNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(BinaryNeuron.getName()));
            neuronPanel = new BinaryNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof AdditiveNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(AdditiveNeuron.getName()));
            neuronPanel = new AdditiveNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof LinearNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(LinearNeuron.getName()));
            neuronPanel = new LinearNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof SigmoidalNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(SigmoidalNeuron.getName()));
            neuronPanel = new SigmoidalNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof RandomNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(RandomNeuron.getName()));
            neuronPanel = new RandomNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof ClampedNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(ClampedNeuron.getName()));
            neuronPanel = new ClampedNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof StochasticNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(StochasticNeuron.getName()));
            neuronPanel = new StochasticNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof LogisticNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(LogisticNeuron.getName()));
            neuronPanel = new LogisticNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof IntegrateAndFireNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(IntegrateAndFireNeuron.getName()));
            neuronPanel = new IntegrateAndFireNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof SinusoidalNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(SinusoidalNeuron.getName()));
            neuronPanel = new SinusoidalNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof IzhikevichNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(IzhikevichNeuron.getName()));
            neuronPanel = new IzhikevichNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof NakaRushtonNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(NakaRushtonNeuron.getName()));
            neuronPanel = new NakaRushtonNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof DecayNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(DecayNeuron.getName()));
            neuronPanel = new DecayNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        } else if (neuron_ref instanceof IACNeuron) {
            cbNeuronType.setSelectedIndex(Neuron.getNeuronTypeIndex(IACNeuron.getName()));
            neuronPanel = new IACNeuronPanel();
            neuronPanel.setNeuron_list(neuron_list);
            neuronPanel.fillFieldValues();
        }
    }

    /**
     * Change all the neurons from their current type to the new selected type.
     */
    public void changeNeurons() {
        if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(BinaryNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                BinaryNeuron newNeuron = new BinaryNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(AdditiveNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                AdditiveNeuron newNeuron = new AdditiveNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(LinearNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                LinearNeuron newNeuron = new LinearNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(SigmoidalNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                SigmoidalNeuron newNeuron = new SigmoidalNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(RandomNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                RandomNeuron newNeuron = new RandomNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(ClampedNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                ClampedNeuron newNeuron = new ClampedNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(StochasticNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                StochasticNeuron newNeuron = new StochasticNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(LogisticNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                LogisticNeuron newNeuron = new LogisticNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(IntegrateAndFireNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                IntegrateAndFireNeuron newNeuron = new IntegrateAndFireNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(SinusoidalNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                SinusoidalNeuron newNeuron = new SinusoidalNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(IzhikevichNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                IzhikevichNeuron newNeuron = new IzhikevichNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(NakaRushtonNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                NakaRushtonNeuron newNeuron = new NakaRushtonNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(DecayNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                DecayNeuron newNeuron = new DecayNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        } else if (cbNeuronType.getSelectedItem().toString().equalsIgnoreCase(IACNeuron.getName())) {
            for (int i = 0; i < neuron_list.size(); i++) {
                Neuron oldNeuron = (Neuron) neuron_list.get(i);
                IACNeuron newNeuron = new IACNeuron(oldNeuron);
                newNeuron.getParentNetwork().changeNeuron(oldNeuron, newNeuron);
            }
        }
    }

    /**
     * Respond to neuron type changes.
     */
    public void actionPerformed(final ActionEvent e) {
        neuronsHaveChanged = true;

        Neuron neuron_ref = (Neuron) neuron_list.get(0);

        if (cbNeuronType.getSelectedItem().equals(BinaryNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new BinaryNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(AdditiveNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new AdditiveNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(LinearNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new LinearNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(SigmoidalNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new SigmoidalNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(RandomNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new RandomNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(ClampedNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new ClampedNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(StochasticNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new StochasticNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(LogisticNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new LogisticNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(IntegrateAndFireNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new IntegrateAndFireNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(SinusoidalNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new SinusoidalNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(IzhikevichNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new IzhikevichNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(NakaRushtonNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new NakaRushtonNeuronPanel(neuron_ref.getParentNetwork());
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(DecayNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new DecayNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        } else if (cbNeuronType.getSelectedItem().equals(IACNeuron.getName())) {
            mainPanel.remove(neuronPanel);
            neuronPanel = new IACNeuronPanel();
            neuronPanel.fillDefaultValues();
            mainPanel.add(neuronPanel);
        }

        pack();
    }

    /**
     * Set the initial values of dialog components
     */
    private void fillFieldValues() {
        Neuron neuron_ref = (Neuron) neuron_list.get(0);
        tfActivation.setText(Double.toString(neuron_ref.getActivation()));
        tfIncrement.setText(Double.toString(neuron_ref.getIncrement()));
        tfLowBound.setText(Double.toString(neuron_ref.getLowerBound()));
        tfUpBound.setText(Double.toString(neuron_ref.getUpperBound()));

        neuronPanel.fillFieldValues();

        //Handle consistency of multiple selections
        if (!NetworkUtils.isConsistent(neuron_list, Neuron.class, "getActivation")) {
            tfActivation.setText(NULL_STRING);
        }

        if (!NetworkUtils.isConsistent(neuron_list, Neuron.class, "getIncrement")) {
            tfIncrement.setText(NULL_STRING);
        }

        if (!NetworkUtils.isConsistent(neuron_list, Neuron.class, "getLowerBound")) {
            tfLowBound.setText(NULL_STRING);
        }

        if (!NetworkUtils.isConsistent(neuron_list, Neuron.class, "getUpperBound")) {
            tfUpBound.setText(NULL_STRING);
        }
    }

    /**
     * Called externally when the dialog is closed, to commit any changes made
     */
    public void commitChanges() {

        for (int i = 0; i < neuron_list.size(); i++) {
            Neuron neuron_ref = (Neuron) neuron_list.get(i);

            if (tfActivation.getText().equals(NULL_STRING) == false) {
                neuron_ref.setActivation(Double.parseDouble(tfActivation.getText()));
            }

            if (tfIncrement.getText().equals(NULL_STRING) == false) {
                neuron_ref.setIncrement(Double.parseDouble(tfIncrement.getText()));
            }

            if (tfUpBound.getText().equals(NULL_STRING) == false) {
                neuron_ref.setUpperBound(Double.parseDouble(tfUpBound.getText()));
            }

            if (tfLowBound.getText().equals(NULL_STRING) == false) {
                neuron_ref.setLowerBound(Double.parseDouble(tfLowBound.getText()));
            }
        }

        if (neuronsHaveChanged) {
            changeNeurons();
        }

        setNeuronList();
        neuronPanel.setNeuron_list(neuron_list);
        neuronPanel.commitChanges();
    }
}
