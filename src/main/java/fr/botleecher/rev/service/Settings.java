/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import java.io.File;
import java.util.List;

/**
 * @author fdb
 */
public interface Settings {

    File getSaveFolder();

    void setSaveFolder(final String dir);

    List<String> getServers();

    void addServer(String server);

    List<String> getChannels();

    void addChannel(String channel);

    void addNick(String nick);

    List<String> getNicks();

    void setNicks(String nicks);

    List<String> getKeywords();

    void setKeywords(List<String> keywords);

    void setKeywords(final String keywords);

}
