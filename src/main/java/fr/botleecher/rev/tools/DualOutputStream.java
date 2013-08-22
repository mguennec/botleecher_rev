/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.tools;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author fdb
 */
public class DualOutputStream extends FilterOutputStream{

    private final TextWriter writer;

    /**
     * Filters an output stream and splits output to a TextWriter
     * @param oldStream
     * @param textWriter
     */
    public DualOutputStream(PrintStream oldStream, TextWriter textWriter) {
        super(oldStream);
        this.writer = textWriter ;   
    }

    @Override
    public void write(byte[] b) throws IOException {
        String aString = new String(b);
        writer.writeText(aString);
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String aString = new String(b, off, len);
        writer.writeText(aString);
        out.write(b, off, len);
    }
}
