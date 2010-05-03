package it.unitn.disi.smatch.loaders;

import it.unitn.disi.smatch.MatchManager;
import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.data.IContext;
import it.unitn.disi.smatch.data.INode;
import it.unitn.disi.smatch.data.mappings.IMapping;
import it.unitn.disi.smatch.data.mappings.Mapping;
import it.unitn.disi.smatch.data.mappings.MappingElement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * Loads the mapping as written by PlainRenderer.java.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class PlainMappingLoader implements IMappingLoader {

    private static final Logger log = Logger.getLogger(PlainMappingLoader.class);

    public IMapping loadMapping(IContext ctxSource, IContext ctxTarget, String fileName) throws SMatchException {
        if (log.isEnabledFor(Level.INFO)) {
            log.info("Loading mapping: " + fileName);
        }

        Vector<INode> sourceNodes = ctxSource.getAllNodes();
        Vector<INode> targetNodes = ctxTarget.getAllNodes();

        if (log.isEnabledFor(Level.INFO)) {
            log.info(sourceNodes.size() + " x " + targetNodes.size() + " nodes");
        }

        IMapping mapping = new Mapping(ctxSource, ctxTarget);

        HashMap<String, Integer> sNodes = createHash(sourceNodes);
        HashMap<String, Integer> tNodes = createHash(targetNodes);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String line;
            int cnt = 0;
            int cntLoaded = 0;
            int lg = 0;
            int mg = 0;
            int eq = 0;
            int dj = 0;

            while ((line = reader.readLine()) != null &&
                    !line.startsWith("#") &&
                    !line.equals("")) {

                int sourceIdx;
                int targetIdx;
                char rel;

                String[] tokens = line.split("\t");
                if (3 != tokens.length) {
                    if (log.isEnabledFor(Level.WARN)) {
                        log.warn("Unrecognized mapping format: " + line);
                    }
                } else {
                    //tokens = left \t relation \t right
                    rel = tokens[1].toCharArray()[0];
                    switch (rel) {
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

                    sourceIdx = -1;
                    if (!sNodes.containsKey(tokens[0])) {
                        if (log.isEnabledFor(Level.WARN)) {
                            log.warn("Could not find source node: " + tokens[0]);
                        }
                    } else {
                        sourceIdx = sNodes.get(tokens[0]);
                    }

                    targetIdx = -1;
                    if (!tNodes.containsKey(tokens[2])) {
                        if (log.isEnabledFor(Level.WARN)) {
                            log.warn("Could not find target node: " + tokens[2]);
                        }
                    } else {
                        targetIdx = tNodes.get(tokens[2]);
                    }

                    if ((-1 != sourceIdx) && (-1 != targetIdx)) {
                        mapping.add(new MappingElement(sourceNodes.get(sourceIdx), targetNodes.get(targetIdx), rel));
                        cntLoaded++;
                    } else {
                        if (log.isEnabledFor(Level.WARN)) {
                            log.warn("Could not find mapping: " + line);
                        }
                    }
                }
                cnt++;
                if (0 == (cnt % 1000)) {
                    if (log.isEnabledFor(Level.INFO)) {
                        log.info("Loaded links: " + cnt);
                    }
                }
            }

            if (log.isEnabledFor(Level.INFO)) {
                log.info(cnt);
                log.info("Loading mapping finished. Loaded " + cntLoaded + " relations");
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

        return mapping;
    }

    /**
     * Gets the path of a node from root for hash mapping.
     *
     * @param node the interface of data structure of input node
     * @return the string of the path from root to node
     */
    //TODO move this method to the INode interface
    protected String getNodePathToRoot(INode node) {
        StringBuilder sb = new StringBuilder();
        INode parent = node;
        while (null != parent) {
            if (parent.getNodeName().contains("\\")) {
                log.debug("source: replacing \\ in: " + parent.getNodeName());
                sb.insert(0, "\\" + parent.getNodeName().replaceAll("\\\\", "/"));
            } else {
                sb.insert(0, "\\" + parent.getNodeName());
            }
            parent = parent.getParent();
        }
        return sb.toString();
    }

    /**
     * Creates hash map for nodes which contains path from root to node for each node.
     *
     * @param nodes list of interfaces of all nodes of source or target tree
     * @return a hash table which contains path from root to node for each node
     */
    public HashMap<String, Integer> createHash(Vector<INode> nodes) {
        if (log.isEnabledFor(Level.INFO)) {
            log.info("Creating hash for " + nodes.size() + " nodes...");
        }

        HashMap<String, Integer> result = new HashMap<String, Integer>(nodes.size());

        for (int i = 0; i < nodes.size(); i++) {
            result.put(getNodePathToRoot(nodes.get(i)), i);
        }

        return result;
    }
}
