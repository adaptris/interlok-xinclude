package com.adaptris.core.xinclude;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.etourdot.xincproc.xinclude.XIncProcEngine;

import com.adaptris.core.AdaptrisMarshaller;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.URLString;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * XStream version of {@link AdaptrisMarshaller} that supports xinclude directives when unmarshalling.
 * <p>
 * It is only useful if you are not using the UI to create configuration, as that will always create a monolithic configuration
 * file. It uses the <a href="https://github.com/etourdot/xincproc">XIncProc Framework</a> to pre-process the document before
 * attempting to unmarshal the configuration. Note that the {@code org.etourdot.xincproc.xinclude.sax} package does emit a lot of
 * logging at TRACE level.
 * </p>
 * 
 * @config xstream-xinclude-marshaller
 * 
 */
@XStreamAlias("xstream-xinclude-marshaller")
public class XStreamMarshaller extends com.adaptris.core.XStreamMarshaller {

  public XStreamMarshaller() throws CoreException {
  }

  @Override
  public Object unmarshal(Reader reader) throws CoreException {
    Object result = null;
    try {
      result = unmarshal(reader, "inline:xstream");
    }
    catch (Exception e) {
      throw new CoreException(e);
    }
    finally {
      IOUtils.closeQuietly(reader);
    }
    return result;
  }

  @Override
  public Object unmarshal(String xml) throws CoreException {
    Object result = null;
    try (StringReader r = new StringReader(xml)) {
      result = unmarshal(r, "inline:xstream");
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(File file) throws CoreException {
    Object result = null;
    try (InputStream in = new FileInputStream(file)) {
      result = unmarshal(in, file.toURI().toURL().toExternalForm());
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URL fileUrl) throws CoreException {
    if (fileUrl == null) {
      throw new IllegalArgumentException("Cannot unmarshall null");
    }
    Object result = null;
    try (InputStream in = fileUrl.openStream()) {
      result = this.unmarshal(in, fileUrl.toExternalForm());
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URLString loc) throws CoreException {
    Object result = null;
    try (InputStream in = connectToUrl(loc)) {
      if (in != null) {
        result = this.unmarshal(in, loc.toString());
      }
      else {
        throw new IOException("could not unmarshal component from [" + loc + "]");
      }
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(InputStream stream) throws CoreException {
    return this.unmarshal(stream, "inline:xml");
  }

  private Object unmarshal(Reader reader, String xmlbase) throws CoreException {
    return unmarshal(new ReaderInputStream(reader), xmlbase);
  }

  private Object unmarshal(InputStream input, String xmlbase) throws CoreException {
    Object result = null;
    StringWriter sw = new StringWriter();
    try (WriterOutputStream out = new WriterOutputStream(sw)) {
      XIncProcEngine.parse(input, xmlbase, out);
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    result = getInstance().fromXML(sw.toString());
    return result;

  }

}
