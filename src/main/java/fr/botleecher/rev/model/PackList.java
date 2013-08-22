/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Immutable class
 * @author fdb
 */
public class PackList implements Serializable {
    
    private final List<Pack> packs;
    private final List<String> messages;

    public PackList(List packs, List messages) {
        this.packs = packs;
        this.messages = messages;
    }

    public List<Pack> getPacks() {
        return Collections.unmodifiableList(packs);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    
    

}
