package io.jenkins.plugins.gcr.parsers;

import hudson.FilePath;
import io.jenkins.plugins.gcr.models.Coverage;
import io.jenkins.plugins.gcr.models.JacocoCoverage;
import io.jenkins.plugins.gcr.utils.XmlUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

public class JacocoParser implements CoverageParser {

    @Override
    public Coverage parse(FilePath filepath) throws ParserException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JacocoCoverage.class);
            SAXSource source = XmlUtils.getSAXSource(filepath);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Coverage) jaxbUnmarshaller.unmarshal(source);
        } catch (Exception ex) {
            String message = String.format("Failed to parse Jacoco coverage for filepath '%s'", filepath);
            throw new ParserException(message, ex);
        }
    }

}
