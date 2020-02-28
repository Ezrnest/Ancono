/**
 * 2017-11-03
 */
package cn.ancono.utilities.swingTools;

import javax.swing.*;
import javax.swing.text.StyledDocument;

/**
 * @author liyicheng
 * 2017-11-03 17:02
 */
public class InputTextPane extends JTextPane {

    /**
     *
     */
    private static final long serialVersionUID = 2646930857984731990L;

    /**
     *
     */
    public InputTextPane() {
    }

    /**
     * @param doc
     */
    public InputTextPane(StyledDocument doc) {
        super(doc);
    }

}
