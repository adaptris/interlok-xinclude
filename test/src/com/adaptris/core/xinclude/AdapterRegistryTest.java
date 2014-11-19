package com.adaptris.core.xinclude;

import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML;
import static com.adaptris.core.xinclude.JunitHelper.KEY_XINCLUDE_ADAPTER_XML_BAD;
import static com.adaptris.core.xinclude.JunitHelper.doAssertions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;

import org.apache.commons.io.IOUtils;

import com.adaptris.core.Adapter;
import com.adaptris.core.ClosedState;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.JndiContextFactory;
import com.adaptris.core.runtime.AdapterManagerMBean;
import com.adaptris.core.runtime.AdapterRegistryMBean;
import com.adaptris.core.runtime.ComponentManagerCase;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import com.adaptris.util.license.LicenseException;

public class AdapterRegistryTest extends ComponentManagerCase {

  private AdapterRegistry adapterRegistry;
  private ObjectName registryObjectName;

  private transient Properties contextEnv = new Properties();

  public AdapterRegistryTest(String name) {
    super(name);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    adapterRegistry = new AdapterRegistry(new JunitBootstrapProperties(new Properties()));
    adapterRegistry.registerMBean();
    registryObjectName = adapterRegistry.createObjectName();
    contextEnv.put(Context.INITIAL_CONTEXT_FACTORY, JndiContextFactory.class.getName());
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    adapterRegistry.unregisterMBean();
  }

  public void testCreateAdapter_BadURL() throws Exception {
    Object marker = new Object();
    File filename = deleteLater(marker);
    try {
      ObjectName objName = adapterRegistry.createAdapter(filename.toURI().toURL());
      fail();
    }
    catch (IOException | MalformedObjectNameException | CoreException | LicenseException expected) {

    }

  }

  public void testCreateAdapter_URL_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try {
      ObjectName objName = adapterRegistry.createAdapter(filename.toURI().toURL());
      fail();
    }
    catch (IOException | MalformedObjectNameException | CoreException | LicenseException expected) {

    }
    finally {

    }
  }

  public void testCreateAdapter_URL_WithXinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try {
      ObjectName objName = adapterRegistry.createAdapter(filename.toURI().toURL());
      AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
      assertNotNull(manager);
      assertEquals(ClosedState.getInstance(), manager.getComponentState());
      assertEquals(1, adapterRegistry.getAdapters().size());
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(manager.getConfiguration());
      doAssertions(marshalled);
    }
    finally {

    }
  }

  public void testCreateAdapter_URL() throws Exception {
    String adapterName = this.getClass().getSimpleName() + "." + getName();
    Adapter adapter = createAdapter(adapterName, 2, 2);
    File filename = deleteLater(adapter);
    DefaultMarshaller.getDefaultMarshaller().marshal(adapter, filename);
    ObjectName objName = adapterRegistry.createAdapter(filename.toURI().toURL());
    assertNotNull(objName);
    assertTrue(mBeanServer.isRegistered(objName));
    AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
    assertNotNull(manager);
    assertEquals(ClosedState.getInstance(), manager.getComponentState());
    assertEquals(1, adapterRegistry.getAdapters().size());
  }

  public void testProxy_CreateAdapter_URL() throws Exception {
    AdapterRegistryMBean registry = JMX.newMBeanProxy(mBeanServer, registryObjectName, AdapterRegistryMBean.class);
    String adapterName = this.getClass().getSimpleName() + "." + getName();
    Adapter adapter = createAdapter(adapterName, 2, 2);
    File filename = deleteLater(adapter);
    DefaultMarshaller.getDefaultMarshaller().marshal(adapter, filename);
    ObjectName objName = registry.createAdapter(filename.toURI().toURL());
    assertNotNull(objName);
    assertTrue(mBeanServer.isRegistered(objName));
    AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
    assertNotNull(manager);
    assertEquals(ClosedState.getInstance(), manager.getComponentState());
    assertEquals(1, adapterRegistry.getAdapters().size());
    assertEquals(1, registry.getAdapters().size());
  }

  public void testCreateAdapter_String() throws Exception {
    String adapterName = this.getClass().getSimpleName() + "." + getName();
    Adapter adapter = createAdapter(adapterName, 2, 2);
    String xml = DefaultMarshaller.getDefaultMarshaller().marshal(adapter);
    ObjectName objName = adapterRegistry.createAdapter(xml);
    assertNotNull(objName);
    assertTrue(mBeanServer.isRegistered(objName));
    AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
    assertNotNull(manager);
    assertEquals(ClosedState.getInstance(), manager.getComponentState());
    assertEquals(1, adapterRegistry.getAdapters().size());
  }

  public void testProxy_CreateAdapter_String() throws Exception {
    AdapterRegistryMBean registry = JMX.newMBeanProxy(mBeanServer, registryObjectName, AdapterRegistryMBean.class);
    String adapterName = this.getClass().getSimpleName() + "." + getName();
    Adapter adapter = createAdapter(adapterName, 2, 2);
    String xml = DefaultMarshaller.getDefaultMarshaller().marshal(adapter);
    ObjectName objName = registry.createAdapter(xml);
    assertNotNull(objName);
    assertTrue(mBeanServer.isRegistered(objName));
    AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
    assertNotNull(manager);
    assertEquals(ClosedState.getInstance(), manager.getComponentState());
    assertEquals(1, registry.getAdapters().size());
  }

  public void testProxy_CreateAdapter_String_WithXinclude() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML));
    try (FileReader reader = new FileReader(filename)) {
      String xml = IOUtils.toString(reader);
      AdapterRegistryMBean registry = JMX.newMBeanProxy(mBeanServer, registryObjectName, AdapterRegistryMBean.class);
      ObjectName objName = registry.createAdapter(xml);
      AdapterManagerMBean manager = JMX.newMBeanProxy(mBeanServer, objName, AdapterManagerMBean.class);
      assertNotNull(manager);
      assertEquals(ClosedState.getInstance(), manager.getComponentState());
      assertEquals(1, adapterRegistry.getAdapters().size());
      Adapter marshalled = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(manager.getConfiguration());
      doAssertions(marshalled);

    }
    finally {

    }
  }

  public void testProxy_CreateAdapter_String_CrapData() throws Exception {
    File filename = new File(PROPERTIES.getProperty(KEY_XINCLUDE_ADAPTER_XML_BAD));
    try (FileReader reader = new FileReader(filename)) {
      String xml = IOUtils.toString(reader);
      try {
        ObjectName objName = adapterRegistry.createAdapter(filename.toURI().toURL());
        fail();
      }
      catch (IOException | MalformedObjectNameException | CoreException | LicenseException expected) {

      }
    }
    finally {

    }
  }

}
