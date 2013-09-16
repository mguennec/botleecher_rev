/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import java.util.List;

/**
 * @author fdb
 */
public interface Settings {

    String getSaveFolder() throws Exception;

    void setSaveFolder(final String dir) throws Exception;

    List<String> getServers() throws Exception;

    void addServer(String server) throws Exception;

    List<String> getChannels() throws Exception;

    void addChannel(String channel) throws Exception;

    void addNick(String nick) throws Exception;

    List<String> getNicks() throws Exception;

    void setNicks(List<String> nicks) throws Exception;

    List<String> getKeywords() throws Exception;

    void setKeywords(List<String> keywords) throws Exception;

}
