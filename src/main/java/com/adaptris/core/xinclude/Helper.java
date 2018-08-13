package com.adaptris.core.xinclude;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.adaptris.util.XmlUtils;

class Helper {
  private static final String XINCLUDE_FIXUP_BASE_URI_FEATURE = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";

  static Document toDocument(InputSource source) throws Exception {
    Document doc = null;
    try (DevNullConsole devnull = new DevNullConsole().open()) {
      devnull.open();
      doc = newBuilderFactory().newDocumentBuilder().parse(source);
    }
    return doc;
  }

  static Document toDocument(InputStream source) throws Exception {
    Document doc = null;
    try (DevNullConsole devnull = new DevNullConsole().open()) {
      doc = newBuilderFactory().newDocumentBuilder().parse(source);
    }
    return doc;
  }

  static String toString(Document d) throws Exception {
    StringWriter writer = new StringWriter();
    try {
      new XmlUtils().writeDocument(d, writer, "UTF-8");
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return writer.toString();
  }

  static DocumentBuilderFactory newBuilderFactory() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setXIncludeAware(true);
    dbf.setNamespaceAware(true);
    dbf.setFeature(XINCLUDE_FEATURE, true);
    dbf.setFeature(XINCLUDE_FIXUP_BASE_URI_FEATURE, true);
    return dbf;
  }

  private static final class DevNullConsole implements Closeable {

    private PrintStream stderr, stdout, divert;
    private ByteArrayOutputStream out;

    DevNullConsole() {
      stderr = System.err;
      stdout = System.out;
      out = new ByteArrayOutputStream();
      divert = new PrintStream(out, true);
    }

    DevNullConsole open() {
      try {
        System.setErr(divert);
        System.setOut(divert);
      } catch (SecurityException ignored) {
        ;
      }
      return this;
    }

    public void close() {
      try {
        System.setErr(stderr);
        System.setOut(stdout);
      } catch (SecurityException ignored) {
        ;
      }
      divert.close();
    }
  }
}
