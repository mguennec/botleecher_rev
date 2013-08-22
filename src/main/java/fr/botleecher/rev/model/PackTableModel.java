/*
 * PackTableModel.java
 *
 * Created on April 8, 2007, 11:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.botleecher.rev.model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author francisdb
 */
public class PackTableModel extends AbstractTableModel{
    
    
    
    private static final String[] COL_NAMES = {"#", "Status", "Name", "Size (K)", "Downloads"};
    private static final Class[] COL_TYPES = {Integer.class, PackStatus.class, String.class, Integer.class, Integer.class};
    
    private List<Pack> packs;
    
    /**
     * Creates a new instance of PackTableModel
     * @param packs
     */
    public PackTableModel(List<Pack> packs) {
        this.packs = packs;
    }
    
    @Override
    public int getRowCount() {
        return packs.size();
    }
    
    @Override
    public int getColumnCount() {
        return COL_NAMES.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return COL_NAMES[col];
    }
    
    @Override
    public Class<?> getColumnClass(int col) {
        return COL_TYPES[col];
    }

    
    @Override
    public Object getValueAt(int row, int col) {
        switch(col){
        case 0:
            return packs.get(row).getId();
        case 1:
            return packs.get(row).getStatus();
        case 2:
            return packs.get(row).getName();
        case 3:
            return packs.get(row).getSize();
        case 4:
            return packs.get(row).getDownloads();
        default:
            throw new AssertionError("Column "+col+" not defined");
        }
    }
    
    public Pack getPack(int row){
        return packs.get(row);
    }
    
}
