/*
 * PackListReaderImpl.java
 *
 * Created on April 8, 2007, 10:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.botleecher.rev.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.model.PackList;
import fr.botleecher.rev.model.PackStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author francisdb
 */
@Singleton
public class PackListReaderImpl implements PackListReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);
    
    private Settings settings;

    @Inject
    public PackListReaderImpl(Settings settings) {
        this.settings = settings;
    }

    @Override
    public PackList readPacks(File listFile) {
        List<Pack> packs = new ArrayList<Pack>();
        List<String> messages = new ArrayList<String>();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(listFile));
            String str;
            Pack pack;
            while ((str = in.readLine()) != null) {
                if (str.trim().startsWith("#")) {
                    pack = readPackLine(str);
                    checkExists(pack);
                    packs.add(pack);
                } else {
                    messages.add(str);
                }
            }
            in.close();
        } catch (IOException ex) {
            LOGGER.error("Could not read packet file!", ex);
        } finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    LOGGER.error("Closing buffered file reader failed", ex);
                }
            }
        }

        return new PackList(packs, messages);
    }
    
    private void checkExists(Pack pack){
        File saveFolder = settings.getSaveFolder();
        File packFile = new File(saveFolder, pack.getName());
        if(packFile.exists()){
            pack.setStatus(PackStatus.DOWNLOADED);
        }
    }

    private Pack readPackLine(String line) {
        Pack pack = new Pack();
        try {
        String trimmed = line.trim();
        int startIndex = trimmed.indexOf("] ") + 2;
        pack.setStatus(PackStatus.AVAILABLE);
        pack.setName(trimmed.substring(startIndex));
        int endIndex = startIndex - 1;
        startIndex = trimmed.indexOf(" [");
        pack.setSize(calcSize(trimmed.substring(startIndex, endIndex)));
        endIndex = startIndex - 1;
        startIndex = trimmed.indexOf(" ");
        pack.setDownloads(calcDownloads(trimmed.substring(startIndex, endIndex)));
        endIndex = startIndex;
        pack.setId(calcPackId(trimmed.substring(0, endIndex)));
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).info(line, e);
        }
        return pack;
    }

    private int calcPackId(String packIdPart) {
        String clean = packIdPart.trim().replaceAll("#", "");
        return Integer.parseInt(clean);
    }

    private int calcDownloads(String downloadsPart) {
        String clean = downloadsPart.trim().replaceAll("x", "");
        return Integer.parseInt(clean);
    }

    private int calcSize(String sizePart) {
        String clean = sizePart.replaceAll("\\[", "").replaceAll("\\]", "").trim();
        String suffix = clean.substring(clean.length() - 1);
        int multiplier = 1;
        if (suffix.equals("M")) {
            multiplier = 1024;
        } else if (suffix.equals("K")) {
            multiplier = 1;
        } else if (suffix.equals("G")) {
            multiplier = 1024 * 1024;
        } else {
            LOGGER.warn("Unknown size suffix: " + suffix + " in " + sizePart);
        }
        String size = clean.substring(0, clean.length() - 1);
        int calculatedSIze = (int) (Double.parseDouble(size) * multiplier);
        return calculatedSIze;
    }

}
