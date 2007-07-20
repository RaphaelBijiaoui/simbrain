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
package org.simbrain.gauge.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.simbrain.gauge.GaugePreferences;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Coupling;
import org.simbrain.workspace.CouplingContainer;
import org.simbrain.workspace.Producer;

/**
 * <b>Gauge</b> is the main class of the high dimensional visualizer, which  provides methods for changing and
 * initializing various projection algorithms.
 */
public class Gauge implements CouplingContainer {

    /** Log4j logger. */
    private static final Logger logger = Logger.getLogger(Gauge.class);

    /** Reference to object containing projection settings. */
    private Settings projectorSettings = new Settings();

    /** References to projection objects.*/
    private Projector currentProjector;

    /** How the datasets will be displayed. */
    private String defaultProjector = GaugePreferences.getDefaultProjector();

    /** Application parameters. */
    private double error = 0;

    /** Determines if gauge needs to be updated. */
    private boolean isOn = true;

    /** Current data point.  */
    double[] currentState = null;

    /** Consumer list. */
    private ArrayList<Consumer> consumers= new ArrayList<Consumer>();

    /** Coupling list. */
    private ArrayList<Coupling> couplings = new ArrayList<Coupling>();

    // TO ADD A NEW PROJECTION ALGORITHM:
    // Create a projection class modeled on any of the Project_ classes,
    // which implements Projector, and make appropriate places in locations
    // ONE, TWO, and THREE below.  You must also change the updateProjectionMenu() method
    // in gaugePanel.
    // If there is a dialog box associated with this projector, then changes will have
    // to be made to org.hisee.graphics.GaugePanel.handlePreferenceDialogs() as well

    /** List of available projection algorithms. */
    public static final String[] PROJECTOR_LIST = {
    //ONE: Add name of new projection algorithm
            "Sammon", "PCA", "Coordinate" };

    /**
     * Default constructor for gauge.
     */
    public Gauge() {
        currentProjector = this.getProjectorByName(defaultProjector);
    }

    /**
     * Adds the current states and resets the state vector.
     * The curent state is set by couplings to other workspace components.
     */
    public void updateCurrentState() {
        logger.trace("updateCurrentState() called");
        if ((currentProjector == null) || (getUpstairs() == null)) {
            logger.debug("could not update current state");
            return;
        }
        addDatapoint(currentState);
        currentProjector.project();
        // reset the current state
        currentState = org.simbrain.util.SimbrainMath.zeroVector(getUpstairs().getDimensions());
    }

    /**
     * Initialize gauge to accept data of a specified dimension.
     *
     * @param dims dimensionality of the high dimensional dataset
     */
    public void init(final int dims) {
        currentProjector.init(dims);
        currentState = org.simbrain.util.SimbrainMath.zeroVector(dims);
    }

    /**
     * Update couplings.
     *
     * @param dims dimensions to update
     */
    public void resetCouplings(final int dims) {
        couplings.clear();
        consumers.clear();
        for (int i = 0; i < dims; i++) {
            consumers.add(new Variable(this, i));
        }
    }

    /**
     * Fill in current data point.
     *
     * @param dimension dimension of the dataset to set value of
     * @param value value to add
     */
    public void setValue(final int dimension, final double value) {
    	//logger.trace("dimension = " + dimension + "    value = " + value );
        if (currentState == null) {
            currentState = org.simbrain.util.SimbrainMath.zeroVector(getUpstairs().getDimensions());
        }
        currentState[dimension] = value;
    }

    /**
     * Add a new point to the dataset, using the currently selected add method.
     *
     * @param point the point to add
     */
    public void addDatapoint(final double[] point) {

        logger.debug("addDatapoint called");
        if ((currentProjector == null) || (getUpstairs() == null)) {
            return;
        }

        logger.debug("guage: isOn " + isOn());

        if (isOn()) {
            currentProjector.addDatapoint(point);

            //This is needed to invoke the current projector's init function
            if (currentProjector.isIterable()) {
                currentProjector.init(getUpstairs(), getDownstairs());
            }

            error = 0;
        }
    }

    /**
     * Iterate the dataset some fixed number of times.
     *
     * @param numTimes Number of times to iterate the gauge
     */
    public void iterate(final int numTimes) {
        if (!currentProjector.isIterable()) {
            return;
        }

        int iterations = 0;

        while (iterations < numTimes) {
            error = currentProjector.iterate();
            iterations++;
        }
    }

    /**
     * @return list of projector types, by name
     */
    public static String[] getProjectorList() {
        return PROJECTOR_LIST;
    }

    /**
     * @param projName the name of the projection algorithm to switch to
     */
    public void setCurrentProjector(final String projName) {
        if (projName == null) {
            return;
        }
        Projector newProjector = getProjectorByName(projName);
        newProjector.init(currentProjector.getUpstairs(), currentProjector.getDownstairs());
        setCurrentProjector(newProjector);
    }

    /**
     * @param name name of projector
     * @return Projector type by name.
     */
    public Projector getProjectorByName(final String name) {
        //THREE: Add code below to associate a projector with its name
        Projector ret = null;

        if (name.equalsIgnoreCase("Sammon")) {
            ret = new ProjectSammon(projectorSettings);
        } else if (name.equalsIgnoreCase("Coordinate")) {
            ret = new ProjectCoordinate(projectorSettings);
        } else if (name.equalsIgnoreCase("PCA")) {
            ret = new ProjectPCA(projectorSettings);
        }

        return ret;
    }

    /**
     * Number of dimensions of the underlying data.
     *
     * @return dimensions of the underlying data
     */
    public int getDimensions() {
        if (currentProjector == null) {
            return 0;
        } else if (currentProjector.getUpstairs() == null) {
            return 0;
        }
        return currentProjector.getUpstairs().getDimensions();
    }

    /**
     * @param proj the new projection algorithm
     */
    public void setCurrentProjector(final Projector newProj) {
        currentProjector = newProj;
        currentProjector.project();
    }

    /**
     * @return the current projection algorithm
     */
    public Projector getCurrentProjector() {
        return currentProjector;
    }

    /**
     * Convenience method to get upstairs dataset.
     *
     * @return hi-dimensional dataset associated with current projector
     */
    public Dataset getUpstairs() {
        if (currentProjector == null) {
            return null;
        }

        return currentProjector.getUpstairs();
    }

    /**
     * Convenience method to get downstairs dataset.
     *
     * @return low-dimensional dataset associated with current projector
     */
    public Dataset getDownstairs() {
        if (currentProjector == null) {
            return null;
        }

        return currentProjector.getDownstairs();
    }

    /**
     * Returns error, which is only set by some projection functions.
     *
     * @return current error
     */
    public double getError() {
        return error;
    }

    /**
     * If the gauge is on it should actively represent changing states of the network.
     *
     * @return true if the gauge is on
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * Turn the gauge on and off; i.e., allow new data or not.  Used mainly when the Gauge is a component in another
     * application.
     *
     * @param b boolean value to update gauge
     */
    public void setOn(final boolean b) {
        isOn = b;
    }

    /**
     * @return Returns the defaultProjector.
     */
    public String getDefaultProjector() {
        return defaultProjector;
    }

    /**
     * @param defaultProjector The defaultProjector to set.
     */
    public void setDefaultProjector(final String defaultProjector) {
        this.defaultProjector = defaultProjector;
    }


    /**
     * {@inheritDoc}
     */
    public List<Consumer> getConsumers() {
        return consumers;
    }

    /**
     * {@inheritDoc}
     */
    public List<Coupling> getCouplings() {
        return couplings;
    }

    /**
     * No producers.
     */
    public List<Producer> getProducers() {
        return null;
    }

    /**
     * @return the currentState
     */
    public double[] getCurrentState() {
        return currentState;
    }

    /**
     * @param currentState the currentState to set
     */
    public void setCurrentState(double[] currentState) {
        this.currentState = currentState;
    }
}
