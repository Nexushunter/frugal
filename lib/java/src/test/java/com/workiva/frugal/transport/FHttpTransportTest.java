package com.workiva.frugal.transport;

import com.workiva.frugal.exception.FMessageSizeException;
import com.workiva.frugal.protocol.FContext;
import com.workiva.frugal.protocol.FRegistry;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FHttpTransport}.
 */
public class FHttpTransportTest {

    private CloseableHttpClient client;
    private String url = "http://foo.com";
    private FHttpTransport transport;
    private FContext context = new FContext();

    @Before
    public void setUp() {
        client = mock(CloseableHttpClient.class);
        transport = new FHttpTransport.Builder(client, url).build();
    }

    @Test
    public void testOpenClose() throws TTransportException, IOException, InterruptedException {
        assertTrue(transport.isOpen());
        transport.open();
        assertTrue(transport.isOpen());
        transport.close();
        assertTrue(transport.isOpen());
    }

    @Test(expected = FMessageSizeException.class)
    public void testSend_sizeException() throws TTransportException {
        int requestSizeLimit = 1024 * 4;
        transport = new FHttpTransport.Builder(client, url).withRequestSizeLimit(requestSizeLimit).build();
        transport.request(context, false, new byte[requestSizeLimit + 1]);
    }

    @Test
    public void testRequest() throws TException, IOException {
        int responseSizeLimit = 1024 * 4;
        transport = new FHttpTransport.Builder(client, url).withResponseSizeLimit(responseSizeLimit).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null);
        byte[] framedResponsePayload = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
        byte[] responsePayload = new byte[]{4, 5, 6, 7};
        String encoded = Base64.encodeBase64String(framedResponsePayload);
        StringEntity responseEntity = new StringEntity(encoded, ContentType.create("application/x-frugal", "utf-8"));

        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);
        response.setEntity(responseEntity);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        byte[] buff = "helloserver".getBytes();
        byte[] actualResponse = transport.request(context, false, buff);

        assertArrayEquals(responsePayload, actualResponse);

        HttpPost actual = topicCaptor.getValue();
        HttpPost expected = validRequest(buff, responseSizeLimit);
        assertEquals(EntityUtils.toString(expected.getEntity()), EntityUtils.toString(actual.getEntity()));
        assertEquals(expected.getFirstHeader("content-type"), actual.getFirstHeader("content-type"));
        assertEquals(expected.getURI(), actual.getURI());
    }

    @Test(expected = TTransportException.class)
    public void testSend_requestIOException() throws TTransportException, IOException {
        byte[] buff = "helloserver".getBytes();
        when(client.execute(any(HttpPost.class))).thenThrow(new IOException());
        transport.request(context, false, buff);
    }

    @Test(expected = FMessageSizeException.class)
    public void testSend_responseTooLarge() throws TTransportException, IOException {
        int responseSizeLimit = 1024 * 4;
        transport = new FHttpTransport.Builder(client, url).withResponseSizeLimit(responseSizeLimit).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_REQUEST_TOO_LONG, null);
        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        byte[] buff = "helloserver".getBytes();
        transport.request(context, false, buff);
    }

    @Test(expected = TTransportException.class)
    public void testSend_responseBadStatus() throws TTransportException, IOException {
        transport = new FHttpTransport.Builder(client, url).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, null);
        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        byte[] buff = "helloserver".getBytes();
        transport.request(context, false, buff);
    }

    @Test(expected = TTransportException.class)
    public void testSend_badResponseLength() throws TTransportException, IOException {
        transport = new FHttpTransport.Builder(client, url).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null);
        byte[] responsePayload = new byte[1];
        String encoded = Base64.encodeBase64String(responsePayload);
        StringEntity responseEntity = new StringEntity(encoded, ContentType.create("application/x-frugal", "utf-8"));

        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);
        response.setEntity(responseEntity);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        byte[] buff = "helloserver".getBytes();
        transport.request(context, false, buff);
    }

    @Test(expected = TTransportException.class)
    public void testSend_missingData() throws TTransportException, IOException {
        transport = new FHttpTransport.Builder(client, url).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null);
        byte[] responsePayload = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
        String encoded = Base64.encodeBase64String(responsePayload);
        StringEntity responseEntity = new StringEntity(encoded, ContentType.create("application/x-frugal", "utf-8"));

        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);
        response.setEntity(responseEntity);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        byte[] buff = "helloserver".getBytes();
        transport.request(context, false, buff);
    }

    @Test
    public void testSend_oneWay() throws TException, IOException {
        transport = new FHttpTransport.Builder(client, url).build();

        StatusLine statusLine = new StatusLineImpl(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null);
        byte[] responsePayload = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String encoded = Base64.encodeBase64String(responsePayload);
        StringEntity responseEntity = new StringEntity(encoded, ContentType.create("application/x-frugal", "utf-8"));

        CloseableHttpResponse response = new BasicClosableHttpResponse(statusLine);
        response.setEntity(responseEntity);

        ArgumentCaptor<HttpPost> topicCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(client.execute(topicCaptor.capture())).thenReturn(response);

        FRegistry mockRegistry = mock(FRegistry.class);
        transport.registry = mockRegistry;
        byte[] buff = "helloserver".getBytes();
        transport.request(context, true, buff);

        verify(mockRegistry, never()).execute(any(byte[].class));
    }

    private HttpPost validRequest(byte[] payload, int responseSizeLimit) {
        // Encode request payload
        String encoded = Base64.encodeBase64String(payload);
        StringEntity requestEntity = new StringEntity(encoded, ContentType.create("application/x-frugal", "utf-8"));

        // Set headers and payload
        HttpPost request = new HttpPost(url);
        request.setHeader("accept", "application/x-frugal");
        request.setHeader("content-transfer-encoding", "base64");
        if (responseSizeLimit > 0) {
            request.setHeader("x-frugal-payload-limit", Integer.toString(responseSizeLimit));
        }
        request.setEntity(requestEntity);
        return request;
    }

    private class StatusLineImpl implements StatusLine {
        private ProtocolVersion protocolVersion;
        private int statusCode;
        private String reasonPhrase;

        StatusLineImpl(ProtocolVersion protocolVersion, int statusCode, String reasonPhrase) {
            this.protocolVersion = protocolVersion;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return protocolVersion;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String getReasonPhrase() {
            return reasonPhrase;
        }
    }

    private class BasicClosableHttpResponse extends BasicHttpResponse implements CloseableHttpResponse {

        BasicClosableHttpResponse(StatusLine statusline) {
            super(statusline);
        }

        public void close() throws IOException { }
    }
}
