package com.adaptris.core.xinclude;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import org.xml.sax.InputSource;
import com.adaptris.core.CoreException;
import com.adaptris.core.config.ConfigPreProcessorImpl;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.KeyValuePairSet;


/**
 * Custom {@link com.adaptris.core.config.ConfigPreProcessor} implementation that supports xinclude
 * directives as part of the adapter configuration file.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap
 * property {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS}
 * to be <strong>xinclude</strong> and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * It is only useful if you are not using the UI to create configuration, as that will always create
 * a monolithic configuration file. It uses the
 * <a href="https://xerces.apache.org/xerces2-j/features.html#xinclude">XInclude feature</a> of
 * Xerces-J.
 * </p>
 * 
 * @author amcgrath
 * 
 */
public class XincludePreProcessor extends ConfigPreProcessorImpl {


  public XincludePreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  public XincludePreProcessor(KeyValuePairSet config) {
    super(config);
  }


  @Override
  public String process(String xml) throws CoreException {
    String result = null;
    try (StringReader rdr = new StringReader(xml)){
      InputSource source = new InputSource(rdr);
      result = Helper.toString(Helper.toDocument(source));
    } catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public String process(URL url) throws CoreException {
    String result = null;
    try (InputStream in = url.openConnection().getInputStream()) {
      result = Helper.toString(Helper.toDocument(in));
    } catch (Exception ex) {
      ExceptionHelper.rethrowCoreException(ex);
    }
    return result;
  }

}
