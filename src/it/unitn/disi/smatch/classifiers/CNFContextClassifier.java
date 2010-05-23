package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.components.Configurable;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.trees.INodeData;
import orbital.logic.imp.Formula;
import orbital.logic.sign.ParseException;
import orbital.moon.logic.ClassicalLogic;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * Converts cLabFormula into CNF before use.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class CNFContextClassifier extends Configurable implements IContextClassifier {

    private static final Logger log = Logger.getLogger(CNFContextClassifier.class);

    public void buildCNodeFormulas(IContext context) throws ContextClassifierException {
        if (context.hasRoot()) {
            for (Iterator<INode> i = context.getRoot().getSubtree(); i.hasNext();) {
                buildCNode(i.next());
            }
        }
    }

    /**
     * Constructs cNode for the concept.
     *
     * @param in node to process
     * @throws ContextClassifierException ContextClassifierException
     */
    public void buildCNode(INode in) throws ContextClassifierException {
        StringBuffer path = new StringBuffer();
        INodeData nd = in.getNodeData();
        String formula = toCNF(in, nd.getcLabFormula());
        if (formula != null && !formula.equals("") && !formula.equals(" ")) {
            if (formula.contains(" ")) {
                formula = "(" + formula + ")";
            }
            path.append(formula);
        }
        if (in.hasParent()) {
            formula = in.getParent().getNodeData().getcNodeFormula();
            if (formula != null && !formula.equals("") && !formula.equals(" ")) {
                if (2 < path.length()) {
                    path.append(" & ").append(formula);
                } else {
                    path.append(formula);
                }
            }
        }

        nd.setcNodeFormula(path.toString());
    }

    /**
     * Converts the formula into CNF.
     *
     * @param in      the owner of the formula
     * @param formula the formula to convert
     * @return formula in CNF form
     * @throws ContextClassifierException ContextClassifierException
     */
    public static String toCNF(INode in, String formula) throws ContextClassifierException {
        String result = formula;
        if ((formula.contains("&") && formula.contains("|")) || formula.contains("~")) {
            String tmpFormula = formula;
            tmpFormula = tmpFormula.trim();
            try {
                ClassicalLogic cl = new ClassicalLogic();
                if (!tmpFormula.equals("")) {
                    tmpFormula = tmpFormula.replace('.', 'P');
                    Formula f = (Formula) (cl.createExpression(tmpFormula));
                    Formula cnf = ClassicalLogic.Utilities.conjunctiveForm(f);
                    tmpFormula = cnf.toString();
                    result = tmpFormula.replace('P', '.');
                } else {
                    result = tmpFormula;
                }
            } catch (ParseException e) {
                final String errMessage = "Logic parse exception: " + e.getClass().getSimpleName() + ": " + e.getMessage();
                if (log.isEnabledFor(Level.ERROR)) {
                    log.error("Logic parse exception for: " + formula + " at node: " + in.getNodeData().getName());
                    log.error(errMessage, e);
                }
                throw new ContextClassifierException(errMessage, e);
            }
        }
        return result;
    }
}
