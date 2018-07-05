package com.adaptris.core.xinclude;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import com.adaptris.core.AdaptrisMarshaller;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.URLString;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * XStream version of {@link AdaptrisMarshaller} that supports xinclude directives when unmarshalling.
 * <p>It uses the <a href="https://xerces.apache.org/xerces2-j/features.html#xinclude">XInclude feature</a> of Xerces-J.
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
      result = unmarshal(new ReaderInputStream(reader));
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
      result = unmarshal(r);
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
      result = unmarshal(in);
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
      result = this.unmarshal(in);
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
        result = this.unmarshal(in);
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
    String parsed = null;
    try {
      parsed = Helper.toString(Helper.toDocument(stream));
    } catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    } finally {
    }
    return getInstance().fromXML(parsed);
  }

}
