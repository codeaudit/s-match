package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.components.Configurable;
import it.unitn.disi.smatch.data.trees.IContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Base class for context renderers.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public abstract class BaseContextRenderer extends Configurable implements IContextRenderer {

    private static final Logger log = Logger.getLogger(BaseContextRenderer.class);

    protected long counter;
    protected long total;
    protected long reportInt;

    public void render(IContext context, String fileName) throws ContextRendererException {
        counter = 0;
        total = context.getNodesList().size();
        reportInt = (total / 20) + 1;//i.e. report every 5%

        process(context, fileName);

        reportStats(context);
    }

    protected abstract void process(IContext context, String fileName) throws ContextRendererException;

    protected void reportStats(IContext context) {
        if (log.isEnabledFor(Level.INFO)) {
            log.info("Rendered nodes: " + context.getNodesList().size());
        }
    }

    protected void reportProgress() {
        counter++;
        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isEnabledFor(Level.INFO)) {
            log.info(100 * counter / total + "%");
        }
    }

}
