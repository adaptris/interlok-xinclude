package com.adaptris.core.runtime.xinclude;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.io.IOUtils;
import org.etourdot.xincproc.xinclude.XIncProcEngine;
import org.etourdot.xincproc.xinclude.exceptions.XIncludeFatalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.license.LicenseException;

/**
 * Custom {@link com.adaptris.core.runtime.AdapterRegistry} implementation that supports xinclude directives as part of the adapter
 * configuration file.
 * <p>
 * This AdapterRegistry can be activated by the setting the system property
 * {@value com.adaptris.core.management.AdapterConfigManager#ADAPTER_REGISTRY_IMPL} to be
 * {@code com.adaptris.core.runtime.xinclude.AdapterRegistry} and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * It is only useful if you are not using the UI to create configuration, as that will always create a monolithic configuration
 * file. It uses the <a href="https://github.com/etourdot/xincproc">XIncProc Framework</a> to pre-process the document before
 * attempting to unmarshal the configuration. Note that the {@code org.etourdot.xincproc.xinclude.sax} package does emit a lot of
 * logging at TRACE level.
 * </p>
 * 
 * @author lchan
 * 
 */
public class AdapterRegistry extends com.adaptris.core.runtime.AdapterRegistry {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  public AdapterRegistry(BootstrapProperties config) throws MalformedObjectNameException {
    super(config);
  }

  @Override
  public ObjectName createAdapter(URL url) throws IOException, MalformedObjectNameException, CoreException, LicenseException {
    ObjectName name = null;
    try (InputStream in = url.openConnection().getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XIncProcEngine.parse(in, url.toExternalForm(), out);
      name = super.createAdapter(out.toString("UTF-8"));
    }
    catch (XIncludeFatalException e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return name;
  }

  @Override
  public ObjectName createAdapter(String xml) throws IOException, MalformedObjectNameException, CoreException, LicenseException {
    ObjectName name = null;
    try (InputStream in = IOUtils.toInputStream(xml, "UTF-8"); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XIncProcEngine.parse(in, "inline:adapter.xml", out);
      name = super.createAdapter(out.toString("UTF-8"));
    }
    catch (XIncludeFatalException e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return name;
  }

}
