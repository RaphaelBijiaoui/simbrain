package org.simbrain.workspace;

import javax.swing.JMenuItem;

/**
 * Packages an object with a jmenu item to make it easy to pass them along
 * through action events.
 *
 */
public class CouplingMenuItem extends JMenuItem {

    /** Reference to producing attribute. */
    private ProducingAttribute producingAttribute = null;

    /** Reference to consuming attribute. */
    private ConsumingAttribute consumingAttribute = null;

    /** Reference to a coupling container. */
    private CouplingContainer couplingContainer = null;
    
    /**
     * The type of menu item being created.  These items can be used to draw
     * information from a single producer or consumer, or lists of either.
     */
    public enum EventType { SINGLE_PRODUCER, SINGLE_CONSUMER, PRODUCER_LIST, CONSUMER_LIST}

    /** The event type for this event. */
    private final EventType eventType; 
    
    /**
     * @param container
     */
    public CouplingMenuItem(final CouplingContainer container, final EventType type) {
        this.couplingContainer = container;
        this.eventType = type; 
    }

    /**
     * @param consumingAttribute
     */
    public CouplingMenuItem(final ConsumingAttribute consumingAttribute) {
        super(consumingAttribute.getAttributeDescription());
        this.eventType = EventType.SINGLE_CONSUMER;
        this.consumingAttribute = consumingAttribute;
    }

    /**
     * @param producingAttribute
     */
    public CouplingMenuItem(final ProducingAttribute producingAttribute) {
        super(producingAttribute.getAttributeDescription());
        this.eventType = EventType.SINGLE_PRODUCER;
        this.producingAttribute = producingAttribute;
    }

    /**
     * @return the consumingAttribute
     */
    public ConsumingAttribute getConsumingAttribute() {
        return consumingAttribute;
    }

    /**
     * @param consumingAttribute the consumingAttribute to set
     */
    public void setConsumingAttribute(final ConsumingAttribute consumingAttribute) {
        this.consumingAttribute = consumingAttribute;
    }

    /**
     * @return the producingAttribute
     */
    public ProducingAttribute getProducingAttribute() {
        return producingAttribute;
    }

    /**
     * @param producingAttribute the producingAttribute to set
     */
    public void setProducingAttribute(final ProducingAttribute producingAttribute) {
        this.producingAttribute = producingAttribute;
    }

    /**
     * @return the container
     */
    public CouplingContainer getCouplingContainer() {
        return couplingContainer;
    }

    /**
     * @param container the container to set
     */
    public void setCouplingContainer(CouplingContainer container) {
        this.couplingContainer = container;
    }

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

}
