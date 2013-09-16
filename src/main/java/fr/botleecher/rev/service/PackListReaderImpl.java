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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author francisdb
 */
@Singleton
public class PackListReaderImpl implements PackListReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);
    private static final Pattern PATTERN = Pattern.compile("#([0-9]+) *([0-9]+)x \\[ *<? *([0-9\\.]*)(.)\\] (.+)");
    private Settings settings;

    @Inject
    public PackListReaderImpl(Settings settings) {
        this.settings = settings;
    }

    @Override
    public PackList readPacks(File listFile) throws Exception {
        List<Pack> packs = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        List<String> files = Arrays.asList(new File(settings.getSaveFolder()).list());

        try (BufferedReader in = new BufferedReader(new FileReader(listFile))) {
            String str;
            while ((str = in.readLine()) != null) {
                final Matcher matcher = PATTERN.matcher(str);
                if (matcher.find()) {
                    final Pack pack = readPackLine(matcher);
                    checkExists(pack, files);
                    packs.add(pack);
                } else {
                    messages.add(str);
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Could not read packet file!", ex);
        }

        return new PackList(packs, messages);
    }

    private void checkExists(Pack pack, List<String> files) throws Exception {
        if (files.contains(pack.getName())) {
            pack.setStatus(PackStatus.DOWNLOADED);
        }
    }

    private Pack readPackLine(Matcher matcher) {
        final Pack pack = new Pack();

        pack.setId(Integer.parseInt(matcher.group(1)));
        pack.setStatus(PackStatus.AVAILABLE);
        pack.setName(matcher.group(5));
        pack.setDownloads(Integer.parseInt(matcher.group(2)));
        pack.setSize(calcSize(matcher.group(3), matcher.group(4)));

        return pack;
    }

    private int calcSize(final String size, final String unit) {
        final int multiplier;
        switch (unit) {
            case "M":
                multiplier = 1024;
                break;
            case "K":
                multiplier = 1;
                break;
            case "G":
                multiplier = 1024 * 1024;
                break;
            default:
                multiplier = 1;
                break;
        }
        return (int) (Double.parseDouble(size) * multiplier);
    }

}
