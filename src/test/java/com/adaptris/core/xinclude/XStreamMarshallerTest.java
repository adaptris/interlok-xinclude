package com.adaptris.core.xinclude;

import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML;
import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML_BAD;
import static com.adaptris.core.xinclude.JunitHelper.doAssertions;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import com.adaptris.core.Adapter;
import com.adaptris.core.CoreException;
import com.adaptris.core.MarshallingBaseCase;
import com.adaptris.util.URLString;

public class XStreamMarshallerTest extends MarshallingBaseCase {

  @Override
  public boolean isAnnotatedForJunit4() {
    return true;
  }
  @Override
  protected XStreamMarshaller createMarshaller() throws Exception {
    return new XStreamMarshaller();
  }

  @Override
  protected String getClasspathXmlFilename() {
    return null;
  }

  // Override a test that uses getClasspathXmlFilename
  @Override
  @Test
  public void testUnmarshalFromUrlStringClasspath() throws Exception {
  }

  @Test
  public void testUnmarshal_Reader_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try (Reader fr = new FileReader(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(fr);
      doAssertions(adapter);
    }
  }

  @Test
  public void testUnmarshal_Reader_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try (Reader fr = new FileReader(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(fr);
      fail();
    }
    catch (CoreException expected) {

    }
  }

  @Test
  public void testUnmarshal_String_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try (Reader fr = new FileReader(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(IOUtils.toString(fr));
      doAssertions(adapter);
    }
  }

  @Test
  public void testUnmarshal_String_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try (Reader fr = new FileReader(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(IOUtils.toString(fr));
      fail();
    }
    catch (CoreException expected) {

    }
  }

  @Test
  public void testUnmarshal_InputStream_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try (InputStream fr = new FileInputStream(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(fr);
      doAssertions(adapter);
    }
  }

  @Test
  public void testUnmarshal_InputStream_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try (InputStream fr = new FileInputStream(filename)) {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(fr);
      fail();
    }
    catch (CoreException expected) {

    }
  }

  @Test
  public void testUnmarshal_File_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    Adapter adapter = (Adapter) createMarshaller().unmarshal(filename);
    doAssertions(adapter);
  }

  @Test
  public void testUnmarshal_File_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(filename);
      fail();
    }
    catch (CoreException expected) {

    }
  }

  @Test
  public void testUnmarshal_URL_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    Adapter adapter = (Adapter) createMarshaller().unmarshal(filename.toURI().toURL());
    doAssertions(adapter);
  }

  @Test
  public void testUnmarshal_URL_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(filename.toURI().toURL());
      fail();
    }
    catch (CoreException expected) {

    }
  }

  @Test
  public void testUnmarshal_URLString_xinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    Adapter adapter = (Adapter) createMarshaller().unmarshal(new URLString(filename.toURI().toURL()));
    doAssertions(adapter);
  }

  @Test
  public void testUnmarshal_URLString_xinclude_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try {
      Adapter adapter = (Adapter) createMarshaller().unmarshal(new URLString(filename.toURI().toURL()));
      fail();
    }
    catch (CoreException expected) {

    }
  }

}