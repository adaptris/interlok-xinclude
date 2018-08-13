package com.adaptris.core.xinclude;

import static org.junit.Assert.assertEquals;

import com.adaptris.core.Adapter;
import com.adaptris.core.StandardWorkflow;

public class JunitHelper {

  public static final String KEY_XINCLUDE_ADAPTER_XML = "xinclude.adapter.xml";
  public static final String KEY_XINCLUDE_ADAPTER_XML_BAD = "xinclude.adapter.xml.bad";

  public static void doAssertions(Adapter adapter) throws Exception {
    assertEquals("xinclude_adapter", adapter.getUniqueId());
    assertEquals(1, adapter.getChannelList().size());
    assertEquals("SEND", adapter.getChannelList().get(0).getUniqueId());
    assertEquals(1, adapter.getChannelList().get(0).getWorkflowList().size());
    assertEquals(StandardWorkflow.class, adapter.getChannelList().get(0).getWorkflowList().get(0).getClass());
    assertEquals("SendMessage", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
  }
}
