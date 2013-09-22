/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import fr.botleecher.rev.entities.Setting;
import fr.botleecher.rev.enums.SettingProperty;

/**
 * @author fdb
 */
public interface Settings {

    Setting get(SettingProperty property) throws Exception;

    void save(Setting setting) throws Exception;
}
