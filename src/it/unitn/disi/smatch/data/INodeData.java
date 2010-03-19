package it.unitn.disi.smatch.data;

import java.util.Vector;

/**
 * Data part of the node.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public interface INodeData {
    void setNodeName(String nodeName);

    Vector<String> getSynonyms();

    Vector<String> getAlternativeLabels();

    double getWeight();

    String getcLabFormula();

    void setcLabFormula(String cLabFormula);

    void setcNodeFormula(String cNodeFormula);

    /**
     * Sets cLab formula to CNF and store in the concept.
     */
    void setcLabFormulaToConjunciveForm(String formula);

    String getNodeUniqueName();

    void setParent(INode parent);

    /**
     * Returns atomic concepts of labels associated with the given node.
     *
     * @return atomic concepts of labels
     */
    Vector<IAtomicConceptOfLabel> getACoLs();

    /**
     * Returns vector of atomic concepts for the node matching task.
     * Basically, all concepts including parent concepts with some filtering.
     * Same filter is used in getAllContextACols, therefore it is needed here.
     *
     * @return vector of atomic concepts for the node matching task
     */
    Vector<IAtomicConceptOfLabel> getNodeMatchingTaskACols();

    /**
     * Gets node matching task acol by Id.
     *
     * @param tokenUID token id
     * @return acol
     */
    IAtomicConceptOfLabel getNMTAColById(String tokenUID);

    /**
     * Gets node acol by Id.
     *
     * @param tokenUID token id
     * @return acol
     */
    IAtomicConceptOfLabel getAColById(String tokenUID);

    void setNodeUniqueName(String uniqueName);

    void setNodeUniqueName();

    void setNodeId(String nodeId);

    public String getParentRelationType();

    /**
     * Adds atomic concepts of label to the node.
     *
     * @param sense
     */
    // TODO need comments
    public void addAtomicConceptOfLabel(IAtomicConceptOfLabel sense);

    String getCNodeFormula();

    /**
     * clears cLab formula.
     */
    void resetLogicalFormula();

    /**
     * clears sets of senses.
     */
    void resetSetOfSenses();

    /**
     * returns path to root string for the given node.
     *
     * @return path to root string
     */
    String getPathToRootString();

    /**
     * returns depth of the node in the context.
     */
    int getDepth();

    /**
     * Gets index in a Vector. This is to avoid hash tables.
     */
    int getIndex();

    /**
     * Sets index in a Vector. This is to avoid hash tables.
     *
     * @param index
     */
    void setIndex(int index);

    /**
     * Indicates whether this node belongs to the source context.
     * This is needed for new algorithms which sometimes swap order.
     *
     * @return whether this node belongs to the source context
     */
    public boolean getSource();

    public void setSource(boolean source);

    void sort();
}
