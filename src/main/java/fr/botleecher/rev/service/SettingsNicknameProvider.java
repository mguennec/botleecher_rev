/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class SettingsNicknameProvider implements NicknameProvider {

    @Inject
    private Settings settings;
    
    @Override
    public String getNickName() {
        final List<String> nicks = settings.getNicks();
        final String nick;
        if (nicks != null && !nicks.isEmpty()) {
            nick = nicks.get((int) (Math.random() * nicks.size()));
        } else {
            nick = "";
        }
        return nick;
    }

}
