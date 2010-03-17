package it.unitn.disi.smatch.data.matrices;

import it.unitn.disi.smatch.MatchManager;
import org.apache.log4j.Logger;

/**
 * A factory class for matrices.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class MatrixFactory {

    private static final Logger log = Logger.getLogger(MatrixFactory.class);

    //also loaded via MatchManager
    public static String MATRIX_CLASS_NAME = "it.unitn.disi.smatch.data.matrices.MatchMatrix";
//    public static String MATRIX_CLASS_NAME = "it.unitn.disi.smatch.data.matrices.JavaSparseArray";

    public static IMatchMatrix getInstance(int x, int y) {
        IMatchMatrix result = (IMatchMatrix) MatchManager.getClassForName(MATRIX_CLASS_NAME);
        log.debug("Created matrix instance: " + MATRIX_CLASS_NAME);
        result.init(x, y);
        return result;
    }
}