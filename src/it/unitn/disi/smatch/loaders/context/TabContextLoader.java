package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.components.Configurable;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Loads context from a tab-separated file. Expects a single-rooted hierarchy, otherwise adds an artificial "Top" node.
 * Each line of the file should contain one label, indented with a number of tabs equal to the level of the node.
 * For example:
 * <p/>
 * Courses
 * \tCollege of Arts and Sciences
 * \t\tEarth and Atmospheric Sciences
 * \t\tHistory
 * \t\t\tLatin America History
 * \t\t\tAmerica History
 * \t\t\tAncient European History
 * \t\tComputer Science
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author Aliaksandr Autayeu avtaev@gmail.com
 * @author Juan Pane pane@disi.unitn.it
 */
public class TabContextLoader extends Configurable implements IContextLoader {

    private static final Logger log = Logger.getLogger(TabContextLoader.class);

    protected int nodesParsed = 0;

    public IContext loadContext(String fileName) throws ContextLoaderException {
        IContext result = null;
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            try {
                result = process(input);
                log.info("Parsed nodes: " + nodesParsed);
                createIds(result);
            } catch (IOException e) {
                final String errMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
                log.error(errMessage, e);
                throw new ContextLoaderException(errMessage, e);
            } finally {
                input.close();
            }
        } catch (IOException e) {
            final String errMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.error(errMessage, e);
            throw new ContextLoaderException(errMessage, e);
        }
        return result;
    }

    protected void createIds(IContext result) {
        nodesParsed = 0;
        for (INode node : result.getNodesList()) {
            node.getNodeData().setId("n" + Integer.toString(nodesParsed));
            nodesParsed++;
        }
    }

    protected IContext process(BufferedReader input) throws IOException {
        IContext result = new Context();
        ArrayList<INode> rootPath = new ArrayList<INode>();

        nodesParsed = 0;
        //loads the root node
        final String rootName = input.readLine();
        if (null != rootName) {
            INode rootNode = result.createRoot(rootName);
            nodesParsed++;
            rootPath.add(rootNode);

            int artificialLevel = 0;//flags that we added Top and need an increment in level
            INode parent;
            int old_depth = 0;
            String line;
            while ((line = input.readLine()) != null &&
                    !line.startsWith("#") &&
                    !line.equals("")) {

                int int_depth = numOfTabs(line);
                String name = line.substring(int_depth);
                int_depth = int_depth + artificialLevel;
                if (int_depth == old_depth) {
                    parent = rootPath.get(old_depth - 1);
                    INode node = parent.createChild(name);
                    setArrayNodeID(int_depth, rootPath, node);
                } else if (int_depth > old_depth) {
                    parent = rootPath.get(old_depth);
                    INode node = parent.createChild(name);
                    setArrayNodeID(int_depth, rootPath, node);
                    old_depth = int_depth;
                } else if (int_depth < old_depth) {
                    if (0 == int_depth) {//looks like we got multiple roots in the input
                        artificialLevel = 1;
                        INode oldRoot = result.getRoot();
                        INode newRoot = result.createRoot("Top");
                        newRoot.addChild(oldRoot);
                        rootPath.add(0, newRoot);
                        int_depth = 1;
                    }
                    parent = rootPath.get(int_depth - 1);
                    INode node = parent.createChild(name);
                    setArrayNodeID(int_depth, rootPath, node);
                    old_depth = int_depth;
                }

                nodesParsed++;
            }
        }
        return result;
    }

    /**
     * Counts the number of tabs in the line.
     *
     * @param line the string of the each line of file
     * @return the number of tabs at the beginning of the line
     */
    private int numOfTabs(String line) {
        int i = 0;
        while (i < line.length() && '\t' == line.charAt(i)) {
            i++;
        }
        return i;
    }

    /**
     * Sets the node at a given position of the array.
     * Changes the current value if there is one, if there is no value, adds a new one.
     *
     * @param index position to be filled
     * @param array array to be modified
     * @param node  value to be set
     */
    private void setArrayNodeID(int index, ArrayList<INode> array, INode node) {
        if (index < array.size()) {
            array.set(index, node);
        } else {
            array.add(index, node);
        }
    }
}