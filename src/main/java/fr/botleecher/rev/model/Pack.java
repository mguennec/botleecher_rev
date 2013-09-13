/*
 * Pack.java
 *
 * Created on April 8, 2007, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.botleecher.rev.model;

import java.io.Serializable;

/**
 * @author francisdb
 */
public class Pack implements Serializable {

    private int id;
    private int downloads;
    private int size;
    private String name;

    private PackStatus status;


    /**
     * Creates a new instance of Pack
     */
    public Pack() {
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param downloads
     */
    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    /**
     * @return
     */
    public int getDownloads() {
        return downloads;
    }

    /**
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    public int getId() {
        return id;
    }

    public void setStatus(PackStatus status) {
        this.status = status;
    }

    public PackStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Pack #" + id + ", " + getSize() + "K, " + downloads + " downloads -> " + name;
    }

}
