package fr.botleecher.rev.service;

import fr.botleecher.rev.model.Pack;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 02/09/13
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public interface EventMediatorService {

    void sendMessage(final String message, final MessageType type);

    void sendPack(final String botName, final List<Pack> packList);

    void sendUserList(final List<String> users);

    void sendTransferStatus(final String botName, final String fileName, final int completion);

    public enum MessageType {
        DOWNLOAD, INFO, ERROR, REQUEST;
    }
}
