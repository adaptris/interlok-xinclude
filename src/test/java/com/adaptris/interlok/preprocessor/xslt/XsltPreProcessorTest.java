package com.adaptris.interlok.preprocessor.xslt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.adaptris.core.Adapter;
import com.adaptris.core.Channel;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.StandardWorkflow;
import com.adaptris.core.WorkflowList;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import com.adaptris.core.stubs.TempFileUtils;
import com.adaptris.interlok.junit.scaffolding.BaseCase;

public class XsltPreProcessorTest extends BaseCase {
  public static final String KEY_XINCLUDE_ADAPTER_XML = "xslt.transform.url";

  @Test
  public void testCreateAdapter_WithNoTransform() throws Exception {

    XsltPreProcessor preProcessor = new XsltPreProcessor(new JunitBootstrapProperties(new Properties()));
    try {
      preProcessor.process(createAdapterXml());
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testCreateAdapter() throws Exception {

    Properties p = new Properties();
    p.setProperty(XsltPreProcessor.XSLT_URL, PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    p.setProperty(XsltPreProcessor.XSLT_PARAM_PREFIX + "duplicates", "5");
    p.setProperty(XsltPreProcessor.XSLT_PASS_ENV, "true");
    XsltPreProcessor preProcessor = new XsltPreProcessor(new JunitBootstrapProperties(p));
    try {
      String xml = preProcessor.process(createAdapterXml());
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
      doAssertions(marshalled, 5);
    } finally {
    }
  }

  @Test
  public void testCreateAdapter_CustomImpl() throws Exception {
    Properties p = new Properties();
    p.setProperty(XsltPreProcessor.XSLT_URL, PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    p.setProperty(XsltPreProcessor.XSLT_PARAM_PREFIX + "duplicates", "5");
    p.setProperty(XsltPreProcessor.XSLT_TRANSFORMER_IMPL, net.sf.saxon.TransformerFactoryImpl.class.getCanonicalName());
    XsltPreProcessor preProcessor = new XsltPreProcessor(new JunitBootstrapProperties(p));
    try {
      String xml = preProcessor.process(createAdapterXml());
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
      doAssertions(marshalled, 5);
    } finally {
    }
  }

  @Test
  public void testCreateAdapter_URL() throws Exception {
    Properties p = new Properties();
    p.setProperty(XsltPreProcessor.XSLT_URL, PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    p.setProperty(XsltPreProcessor.XSLT_PARAM_PREFIX + "duplicates", "5");
    XsltPreProcessor preProcessor = new XsltPreProcessor(new JunitBootstrapProperties(p));
    Object marker = new Object();
    try {
      URL url = create(createAdapterXml(), marker);
      String xml = preProcessor.process(url);
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
      doAssertions(marshalled, 5);
    } finally {
    }
  }

  private URL create(String xml, Object marker) throws IOException {
    File filename = TempFileUtils.createTrackedFile(getName(), null, marker);
    try (FileOutputStream out = new FileOutputStream(filename)) {
      IOUtils.write(xml, out, StandardCharsets.UTF_8);
    }
    return filename.toURI().toURL();
  }

  private String createAdapterXml() throws CoreException {
    Adapter a = new Adapter();
    a.setUniqueId("adapter");
    StandardWorkflow w = new StandardWorkflow();
    w.setUniqueId("workflow");
    Channel c = new Channel();
    c.getWorkflowList().add(w);
    a.getChannelList().add(c);
    return DefaultMarshaller.getDefaultMarshaller().marshal(a);
  }

  public static void doAssertions(Adapter adapter, int workflowCount) throws Exception {
    assertEquals(1, adapter.getChannelList().size());
    WorkflowList list = adapter.getChannelList().get(0).getWorkflowList();
    assertEquals(workflowCount, list.size());
    for (int i = 0; i < workflowCount; i++) {
      assertEquals(StandardWorkflow.class, list.get(i).getClass());
      assertEquals("workflow-" + (i + 1), list.get(i).getUniqueId());
    }
  }

}
