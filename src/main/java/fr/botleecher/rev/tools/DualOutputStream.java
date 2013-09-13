/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.tools;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author fdb
 */
public class DualOutputStream extends FilterOutputStream {

    private final TextWriter writer;

    private final boolean errorStream;

    /**
     * Filters an output stream and splits output to a TextWriter
     *
     * @param oldStream
     * @param textWriter
     */
    public DualOutputStream(PrintStream oldStream, TextWriter textWriter) {
        this(oldStream, textWriter, false);
    }

    /**
     * Filters an output stream and splits output to a TextWriter
     *
     * @param oldStream
     * @param textWriter
     * @param errorStream <code>true</code> if it is an error stream
     */
    public DualOutputStream(PrintStream oldStream, TextWriter textWriter, boolean errorStream) {
        super(oldStream);
        this.writer = textWriter;
        this.errorStream = errorStream;
    }

    @Override
    public void write(byte[] b) throws IOException {
        String aString = new String(b);
        if (errorStream) {
            writer.writeError(aString);
        } else {
            writer.writeText(aString);
        }
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String aString = new String(b, off, len);
        if (errorStream) {
            writer.writeError(aString);
        } else {
            writer.writeText(aString);
        }
        out.write(b, off, len);
    }
}
