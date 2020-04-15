package com.atlassian.maven.plugins.amps.product.common;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.tree.BaseElement;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.io.File;
import java.io.IOException;

import static com.atlassian.maven.plugins.amps.product.jira.utils.DocumentUtils.copySampleDbConfigTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XMLDocumentHandlerTest {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public final String TEST_STRING = "TEST";

    protected com.atlassian.maven.plugins.amps.product.common.XMLDocumentProcessor XMLDocumentProcessor;
    protected XMLDocumentHandler xmlDocumentHandler;

    private static File getConfigFile() throws IOException {
        return new File(temporaryFolder.getRoot(), "dbconfig.xml");
    }

    @Before
    public void setUp() throws Exception {
        temporaryFolder.newFolder();
        copySampleDbConfigTo(getConfigFile());

        xmlDocumentHandler = mock(XMLDocumentHandler.class);
        when(xmlDocumentHandler.read())
                .thenReturn(DocumentFactory
                        .getInstance()
                        .createDocument(new BaseElement("root"))
                );

        XMLDocumentProcessor = new XMLDocumentProcessor(xmlDocumentHandler);
    }

    @Test
    public void shouldExecuteReadAndWriteInOrder() throws MojoExecutionException {
        XMLDocumentTransformer transformer = mock(XMLDocumentTransformer.class);
        when(transformer.transform(any())).thenReturn(true);

        XMLDocumentProcessor
                .load()
                .transform(transformer)
                .saveIfModified();

        InOrder inOrder = inOrder(xmlDocumentHandler);
        inOrder.verify(xmlDocumentHandler).read();
        inOrder.verify(xmlDocumentHandler).write(any());
    }

    @Test
    public void shouldReturnDocumentWhenReadIsExecuted() throws MojoExecutionException {
        Document document = xmlDocumentHandler.read();

        assertThat(document, is(not(nullValue())));
    }

    @Test
    public void shouldThrowExceptionWhenDocumentDoesntExist() throws MojoExecutionException {
        XMLDocumentHandler xmlDocumentHandler = new XMLDocumentHandler(new File("/not/existing/file"));

        thrown.expect(MojoExecutionException.class);
        xmlDocumentHandler.read();
    }

    @Test
    public void shouldInteractWithXMLDocumentWrapperWhenTransformExecuted() throws MojoExecutionException {
        XMLDocumentTransformer transformer = mock(XMLDocumentTransformer.class);

        XMLDocumentProcessor
                .load()
                .transform(transformer);

        verify(transformer).transform(isNotNull());
    }

    @Test
    public void shouldSaveDocumentWhenModified() throws Exception {
        XMLDocumentTransformer transformer = (document) -> {
            document.getRootElement().addElement(TEST_STRING).setText(TEST_STRING);
            return true;
        };

        XMLDocumentProcessor
                .load()
                .transform(transformer)
                .saveIfModified();

        verify(xmlDocumentHandler).write(argThat(hasTestElement()));
    }

    @Test
    public void shouldNotSaveDocumentWhenNotModified() throws Exception {
        XMLDocumentTransformer transformer = (document) -> false;

        XMLDocumentProcessor
                .load()
                .transform(transformer)
                .saveIfModified();

        verify(xmlDocumentHandler, times(0)).write(isNotNull());
    }

    @Test
    public void shouldThrowExceptionWhenValidationFails() throws Exception {
        XMLDocumentValidator negativevalidator = (document) -> {
            throw new ValidationException("");
        };

        thrown.expect(ValidationException.class);
        XMLDocumentProcessor.load().validate(negativevalidator);
    }

    @Test
    public void shouldContinueExecutionWhenValidationSucceeds() throws Exception {
        XMLDocumentTransformer transformer = (document) -> {
            document.getRootElement().addElement(TEST_STRING).setText(TEST_STRING);
            return true;
        };
        XMLDocumentValidator positivevalidator = (document) -> {
        };

        XMLDocumentProcessor
                .load()
                .validate(positivevalidator)
                .transform(transformer)
                .saveIfModified();

        verify(xmlDocumentHandler).write(argThat(hasTestElement()));
    }

    private ArgumentMatcher<Document> hasTestElement() {
        return (document) ->
                document.getRootElement()
                        .element(TEST_STRING)
                        .getText()
                        .equals(TEST_STRING);

    }
}
