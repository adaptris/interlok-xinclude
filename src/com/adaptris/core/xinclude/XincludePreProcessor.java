package com.adaptris.core.xinclude;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.etourdot.xincproc.xinclude.XIncProcEngine;
import org.etourdot.xincproc.xinclude.exceptions.XIncludeFatalException;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.runtime.AbstractConfigurationPreProcessor;


/**
 * Custom {@link com.adaptris.core.runtime.ConfigurationPreProcessor} implementation that supports xinclude directives as part of the adapter
 * configuration file.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the system property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be
 * {@code com.adaptris.core.xinclude.XincludePreProcessor} and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * It is only useful if you are not using the UI to create configuration, as that will always create a monolithic configuration
 * file. It uses the <a href="https://github.com/etourdot/xincproc">XIncProc Framework</a> to pre-process the document before
 * attempting to unmarshal the configuration. Note that the {@code org.etourdot.xincproc.xinclude.sax} package does emit a lot of
 * logging at TRACE level.
 * </p>
 * 
 * @author amcgrath
 * 
 */
public class XincludePreProcessor extends AbstractConfigurationPreProcessor {

  public XincludePreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  @Override
  public String process(String xml) throws CoreException {
    try (InputStream in = IOUtils.toInputStream(xml, "UTF-8"); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XIncProcEngine.parse(in, "inline:adapter.xml", out);
      return out.toString("UTF-8");
    } catch (XIncludeFatalException e) {
      throw new CoreException(e);
    } catch (IOException ex) {
      throw new CoreException(ex);
    }
  }

  @Override
  public String process(URL url) throws CoreException {
    try (InputStream in = url.openConnection().getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XIncProcEngine.parse(in, url.toExternalForm(), out);
      return out.toString("UTF-8");
    } catch (XIncludeFatalException e) {
      throw new CoreException(e);
    } catch (IOException ex) {
      throw new CoreException(ex);
    }
  }
}
