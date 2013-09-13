/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.service;

import fr.botleecher.rev.model.PackList;

import java.io.File;

/**
 * @author fdb
 */
public interface PackListReader {

    PackList readPacks(File listFile);

}
