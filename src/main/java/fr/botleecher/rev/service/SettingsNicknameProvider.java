/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.botleecher.rev.enums.SettingProperty;

import java.util.List;

@Singleton
public class SettingsNicknameProvider implements NicknameProvider {

    @Inject
    private Settings settings;

    @Override
    public String getNickName() throws Exception {
        final List<String> nicks = settings.get(SettingProperty.PROP_NICKS).getValue();
        final String nick;
        if (nicks != null && !nicks.isEmpty()) {
            nick = nicks.get((int) (Math.random() * nicks.size()));
        } else {
            nick = "";
        }
        return nick;
    }

}
