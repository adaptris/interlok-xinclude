package com.adaptris.interlok.preprocessor.xslt;

import static com.adaptris.core.util.PropertyHelper.getPropertyIgnoringCase;
import static com.adaptris.core.util.PropertyHelper.getPropertySubset;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.input.XmlStreamReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.adaptris.core.CoreException;
import com.adaptris.core.config.ConfigPreProcessorImpl;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.util.DocumentBuilderFactoryBuilder;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.KeyValuePairBag;
import com.adaptris.util.KeyValuePairSet;
import com.adaptris.util.URLHelper;
import com.adaptris.util.URLString;
import com.mchange.v1.lang.BooleanUtils;

/**
 * Custom {@link com.adaptris.core.config.ConfigPreProcessor} implementation that allows you to execute an XSLT before unmarshalling the
 * adapter configuration file.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be {@code xslt} and making sure the required
 * jars are available on the classpath.
 * </p>
 *
 */
public class XsltPreProcessor extends ConfigPreProcessorImpl {

  /**
   * The key in configuration defining the properties file where the transform is defined: {@value #XSLT_URL}.
   *
   */
  public static final String XSLT_URL = "xslt.preprocessor.url";

  /**
   * The prefix key in configuration that will contain all the parameters you want to pass in to the transform.
   * <p>
   * The prefix is {@value #XSLT_PARAM_PREFIX}; properties prefixed by this key will be stripped of the prefix and passed into the transform
   * as-is, e.g. {@code xslt.preprocessor.params.count=5} will be passed in as {@code count} which means you can access it in your xslt as
   * {@code $count}.
   * </p>
   *
   */
  public static final String XSLT_PARAM_PREFIX = "xslt.preprocessor.params.";

  /**
   * The key in configuration that dictates the XML transform factory to use : {@value #XSLT_TRANSFORMER_IMPL}.
   * <p>
   * Unless you have a pressing need to override the default provided {@link TransformerFactory#newInstance()} then you don't need to modify
   * this.
   * </p>
   *
   */
  public static final String XSLT_TRANSFORMER_IMPL = "xslt.preprocessor.transformerImpl";

  /**
   * The key in configuration specifying whether or not system properties and environment variables are passed as-is through to the
   * stylesheet : {@value #XSLT_PASS_ENV}.
   * <p>
   * Note that parameters specified by {@link #XSLT_PARAM_PREFIX} will overwrite any environment/system properties that are passed through
   * to the xslt
   * </p>
   */
  public static final String XSLT_PASS_ENV = "xslt.preprocessor.environment.params";

  private transient DocumentBuilder builder;

  public XsltPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  public XsltPreProcessor(KeyValuePairSet config) {
    super(config);
  }

  @Override
  public String process(String xml) throws CoreException {
    return transform(new StringReader(xml));
  }

  @Override
  public String process(URL url) throws CoreException {
    try {
      return transform(new XmlStreamReader(url));
    } catch (IOException e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
  }

  private String transform(Reader input) throws CoreException {
    String result = null;
    String url = backslashToSlash(getPropertyIgnoringCase(getProperties(), XSLT_URL));
    try (Reader autoClose = input; StringWriter output = new StringWriter()) {
      configure(createTransformer(url)).transform(new DOMSource(builder().parse(new InputSource(input))), new StreamResult(output));
      result = output.toString();
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  private Transformer createTransformer(String url) throws Exception {
    try (InputStream in = URLHelper.connect(new URLString(url))) {
      Document xmlDoc = builder().parse(new InputSource(in));
      return newInstance(getPropertyIgnoringCase(getProperties(), XSLT_TRANSFORMER_IMPL, "")).newTransformer(new DOMSource(xmlDoc, url));
    }
  }

  private static TransformerFactory newInstance(String customImpl) {
    return isBlank(customImpl) ? TransformerFactory.newInstance() : TransformerFactory.newInstance(customImpl, null);
  }

  private Transformer configure(Transformer transform) {
    transform.clearParameters();
    Map<String, String> params = getEnvironment();
    params.putAll(getConfiguredParams());
    for (Map.Entry<String, String> e : params.entrySet()) {
      transform.setParameter(e.getKey(), e.getValue());
    }
    return transform;
  }

  private DocumentBuilder builder() throws ParserConfigurationException {
    if (builder == null) {
      builder = DocumentBuilderFactoryBuilder.newInstance().withNamespaceAware(true)
          .newDocumentBuilder(DocumentBuilderFactory.newInstance());
    }
    return builder;
  }

  private Map<String, String> getEnvironment() {
    Map<String, String> result = new HashMap<>();
    if (BooleanUtils.parseBoolean(getPropertyIgnoringCase(getProperties(), XSLT_PASS_ENV, "false"))) {
      result.putAll(System.getenv());
      result.putAll(KeyValuePairBag.asMap(new KeyValuePairSet(System.getProperties())));
    }
    return result;
  }

  private Map<String, String> getConfiguredParams() {
    Map<String, String> result = new HashMap<>();
    Properties params = getPropertySubset(getProperties(), XSLT_PARAM_PREFIX, true);
    for (String key : params.stringPropertyNames()) {
      result.put(key.replace(XSLT_PARAM_PREFIX, ""), params.getProperty(key));
    }
    return result;
  }

  private static String backslashToSlash(String url) {
    if (!isEmpty(url)) {
      return url.replaceAll("\\\\", "/");
    }
    return url;
  }

}
