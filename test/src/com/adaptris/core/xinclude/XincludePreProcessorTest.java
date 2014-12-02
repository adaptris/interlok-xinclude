package com.adaptris.core.xinclude;

import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML;
import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML_BAD;
import static com.adaptris.core.xinclude.JunitHelper.doAssertions;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.adaptris.core.Adapter;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.runtime.ComponentManagerCase;
import com.adaptris.core.stubs.JunitBootstrapProperties;

public class XincludePreProcessorTest extends ComponentManagerCase {
  
  private XincludePreProcessor preProcessor;

  public XincludePreProcessorTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    preProcessor = new XincludePreProcessor(new JunitBootstrapProperties(new Properties()));
  }
  
  public void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testCreateAdapter_BadURL() throws Exception {
    Object marker = new Object();
    File filename = deleteLater(marker);
    try {
      preProcessor.process(filename.toURI().toURL());
      fail();
    }
    catch (CoreException expected) {}
  }

  public void testCreateAdapter_URL_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try {
      preProcessor.process(filename.toURI().toURL());
      fail();
    }
    catch (CoreException expected) {}
    finally {}
  }
  
  public void testCreateAdapter_URL_WithXinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try {
      String xml = preProcessor.process(filename.toURI().toURL());
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
      doAssertions(marshalled);
    }
    finally {}
  }
  
  public void testProxy_CreateAdapter_String_WithXinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try (FileReader reader = new FileReader(filename)) {
      String xmlIn = IOUtils.toString(reader);
      String xml = preProcessor.process(xmlIn);
      
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
      doAssertions(marshalled);
    }
    finally {}
  }
  
}
