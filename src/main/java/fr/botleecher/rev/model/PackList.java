/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable class
 *
 * @author fdb
 */
public class PackList implements Serializable {

    private final List<Pack> packs;
    private final List<String> messages;

    private transient final Map<Integer, Pack> packByNumber = Collections.synchronizedMap(new HashMap<Integer, Pack>());
    private transient final Map<String, Pack> packByName = Collections.synchronizedMap(new HashMap<String, Pack>());


    public PackList(final List<Pack> packs, final List<String> messages) {
        this.packs = packs;
        this.messages = messages;
        for (Pack pack : packs) {
            packByName.put(pack.getName(), pack);
            packByNumber.put(pack.getId(), pack);
        }

    }

    public Pack getByName(final String name) {
        return packByName.get(StringUtils.removeStart(StringUtils.removeEnd(name, "\""), "\""));
    }

    public Pack getByNumber(final int number) {
        return packByNumber.get(number);
    }

    public List<Pack> getPacks() {
        return Collections.unmodifiableList(packs);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }


}
