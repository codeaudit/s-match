package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.MatchManager;
import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.data.mappings.IMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Writes the mapping eXcluding Top node mappings.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class PlainXTopRenderer extends PlainRenderer {

    private static final Logger log = Logger.getLogger(PlainXTopRenderer.class);

    public void render(IMapping mapping, String outputFile) throws SMatchException {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

            int lg = 0;
            int mg = 0;
            int eq = 0;
            int dj = 0;

            long counter = 0;
            long total = mapping.size();
            long reportInt = (total / 20) + 1;//i.e. report every 5%

            for (IMappingElement mappingElement : mapping) {
                if (!(mappingElement.getSourceNode().isRoot() || mappingElement.getTargetNode().isRoot())) {
                    String sourceConceptName = getNodePathToRoot(mappingElement.getSourceNode());
                    String targetConceptName = getNodePathToRoot(mappingElement.getTargetNode());
                    char relation = mappingElement.getRelation();

                    out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");
                    switch (relation) {
                        case MatchManager.LESS_GENERAL_THAN: {
                            lg++;
                            break;
                        }
                        case MatchManager.MORE_GENERAL_THAN: {
                            mg++;
                            break;
                        }
                        case MatchManager.SYNOMYM: {
                            eq++;
                            break;
                        }
                        case MatchManager.OPPOSITE_MEANING: {
                            dj++;
                            break;
                        }
                        default:
                            break;
                    }
                }

                counter++;
                if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isEnabledFor(Level.INFO)) {
                    log.info(100 * counter / total + "%");
                }
            }
            out.close();
            if (log.isEnabledFor(Level.INFO)) {
                log.info("rendered links: " + mapping.size());
                log.info("LG: " + lg);
                log.info("MG: " + mg);
                log.info("EQ: " + eq);
                log.info("DJ: " + dj);
            }

        } catch (IOException e) {
            final String errMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.error(errMessage, e);
            throw new SMatchException(errMessage, e);
        }
    }
}
