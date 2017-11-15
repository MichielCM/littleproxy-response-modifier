package nl.michielmeulendijk.lprm.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class Transformer {
	public static String transform(String xml, String xslt, URIResolver uriResolver) throws TransformerConfigurationException, TransformerException {
		return transform(
			new StringReader(xml),
			new StringReader(xslt),
			uriResolver
		);
	}
	
	public static String transform(Reader xml, Reader xslt, URIResolver uriResolver) throws TransformerConfigurationException, TransformerException {
		return transform(
			new StreamSource(xml),
			new StreamSource(xslt),
			uriResolver
		);
	}
	
	public static String transform(File xml, File xslt, URIResolver uriResolver) throws TransformerConfigurationException, TransformerException {
		return transform(
			new StreamSource(xml),
			new StreamSource(xslt),
			uriResolver
		);
	}
	
	public static String transform(InputStream xml, InputStream xslt, URIResolver uriResolver) throws TransformerConfigurationException, TransformerException {
		return transform(
			new StreamSource(xml),
			new StreamSource(xslt),
			uriResolver
		);
	}
	
	public static String transform(Source xml, Source xslt, URIResolver uriResolver) throws TransformerConfigurationException, TransformerException {
		StringWriter stringWriter = new StringWriter();
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setURIResolver(uriResolver);
		
		transformerFactory.newTransformer(xslt).transform(xml, new StreamResult(
			stringWriter
		));
		
		return stringWriter.toString();
	}
}
