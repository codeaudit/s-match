package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.smatch.data.matrices.IMatchMatrix;
import it.unitn.disi.smatch.data.INode;
import it.unitn.disi.smatch.SMatchException;

/**
 * An interface for node matchers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public interface INodeMatcher {

    /**
     * Matches two nodes and returns a relation between them.
     *
     * @param cLabMatrix a matrix of relations between atomic concepts of labels
     * @param sourceNode interface of source node
     * @param targetNode interface of target node
     * @return relation between source and target nodes.
     * @throws SMatchException
     */
    public char nodeMatch(IMatchMatrix cLabMatrix, INode sourceNode, INode targetNode) throws SMatchException;
}
