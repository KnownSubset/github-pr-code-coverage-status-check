package io.jenkins.plugins.gcr.utils;

import hudson.FilePath;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class XmlUtils {

    public static SAXSource getSAXSource(FilePath filepath) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        spf.setFeature("http://xml.org/sax/features/validation", false);
        spf.setValidating(false);
        XMLReader xmlReader = spf.newSAXParser().getXMLReader();
        InputStream inputStream = filepath.read();
        Reader fileReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        InputSource inputSource = new InputSource(fileReader);
        return new SAXSource(xmlReader, inputSource);
    }
}
