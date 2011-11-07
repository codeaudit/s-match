package it.unitn.disi.annotation.renderers.context;

import it.unitn.disi.annotation.data.INLPContext;
import it.unitn.disi.annotation.data.INLPNode;
import it.unitn.disi.nlptools.data.IToken;
import it.unitn.disi.smatch.renderers.context.BaseFileContextRenderer;
import it.unitn.disi.smatch.renderers.context.ContextRendererException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Renders context for OpenNLP POS tag trainer.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class POSTrainContextRenderer extends BaseFileContextRenderer<INLPContext> implements INLPContextRenderer {

    public static final String TRAIN_FILES = "Train files (*.train)";

    @Override
    protected void process(INLPContext context, BufferedWriter out) throws IOException, ContextRendererException {
        INLPNode curNode = context.getRoot();
        processNode(curNode, out);
    }

    private void processNode(INLPNode curNode, BufferedWriter out) throws IOException {
        String toWrite = getTrainSample(curNode);
        if (null != toWrite) {
            out.write(toWrite);
            out.write("\n");
        }
        Iterator<INLPNode> i = curNode.getChildren();
        while (i.hasNext()) {
            processNode(i.next(), out);
        }
    }

    private String getTrainSample(INLPNode curNode) {
        if (null != curNode.getNodeData().getLabel()) {
            StringBuilder result = new StringBuilder();
            for (IToken token : curNode.getNodeData().getLabel().getTokens()) {
                if (null != token.getPOSTag()) {
                    result.append(token.getText().replace(' ', '_')).append("_").append(token.getPOSTag()).append(" ");
                }
            }
            return result.substring(0, result.length() - 1);
        }
        return null;
    }

    public String getDescription() {
        return TRAIN_FILES;
    }
}