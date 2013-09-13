/*
 * BotListener.java
 *
 * Created on April 8, 2007, 11:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.botleecher.rev;

import fr.botleecher.rev.model.Pack;

import java.util.List;

/**
 * @author francisdb
 */
public interface BotListener {

    /**
     * Triggered when the pack list has been loaded
     *
     * @param packList
     */
    void packListLoaded(String botName, List<Pack> packList);
}
