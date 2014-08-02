/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.mail.internet;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataContentHandler;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class MimeMultipartTest extends TestCase {
    private CommandMap defaultMap;

    public void testWriteTo() throws MessagingException, IOException, Exception {
        writeToSetUp();

        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart part1 = new MimeBodyPart();
        part1.setHeader("foo", "bar");
        part1.setContent("Hello World", "text/plain");
        mp.addBodyPart(part1);
        final MimeBodyPart part2 = new MimeBodyPart();
        part2.setContent("Hello Again", "text/plain");
        mp.addBodyPart(part2);
        mp.writeTo(System.out);

        writeToTearDown();
    }

    public void testPreamble() throws MessagingException, IOException {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("rickmcg@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("rick@us.ibm.com"));
        message.setSubject("test subject");

        final BodyPart messageBodyPart1 = new MimeBodyPart();
        messageBodyPart1.setHeader("Content-Type", "text/xml");
        messageBodyPart1.setHeader("Content-Transfer-Encoding", "binary");
        messageBodyPart1.setText("This is a test");

        final MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart1);
        multipart.setPreamble("This is a preamble");

        assertEquals("This is a preamble", multipart.getPreamble());

        message.setContent(multipart);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        message.writeTo(out);
        out.writeTo(System.out);

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        final MimeMessage newMessage = new MimeMessage(session, in);
        assertEquals("This is a preamble\r\n", ((MimeMultipart)newMessage.getContent()).getPreamble());
    }

    public void testMIMEWriting() throws IOException, MessagingException {
        final File basedir = new File(System.getProperty("basedir", "."));
        final File testInput = new File(basedir, "src/test/resources/wmtom.bin");
        final FileInputStream inStream = new FileInputStream(testInput);
        final Properties props = new Properties();
        final javax.mail.Session session = javax.mail.Session
                .getInstance(props, null);
        final MimeMessage mimeMessage = new MimeMessage(session, inStream);
        final DataHandler dh = mimeMessage.getDataHandler();
        final MimeMultipart multiPart = new MimeMultipart(dh.getDataSource());
        final MimeBodyPart mimeBodyPart0 = (MimeBodyPart) multiPart.getBodyPart(0);
        final Object object0 = mimeBodyPart0.getContent();
        assertNotNull(object0);
        final MimeBodyPart mimeBodyPart1 = (MimeBodyPart) multiPart.getBodyPart(1);
        final Object object1 = mimeBodyPart1.getContent();
        assertNotNull(object1);
        assertEquals(multiPart.getCount(), 2);
    }
    
    public void testJavaMail15NewConstrucor() throws IOException, MessagingException {
        final File basedir = new File(System.getProperty("basedir", "."));
        final File testInput = new File(basedir, "src/test/resources/wmtom.bin");
        BodyPart[] bps = new BodyPart[2]; 
        bps[0] = new MimeBodyPart(new FileInputStream(testInput));
        bps[1] = new MimeBodyPart(new FileInputStream(testInput));
        final MimeMultipart multiPart = new MimeMultipart(bps);
        final MimeBodyPart mimeBodyPart0 = (MimeBodyPart) multiPart.getBodyPart(0);
        final Object object0 = mimeBodyPart0.getContent();
        assertNotNull(object0);
        final MimeBodyPart mimeBodyPart1 = (MimeBodyPart) multiPart.getBodyPart(1);
        final Object object1 = mimeBodyPart1.getContent();
        assertNotNull(object1);
        assertEquals(multiPart.getCount(), 2);
        assertTrue(multiPart.getContentType().startsWith("multipart/mixed"));
    }
    
    public void testJavaMail15NewConstrucor2() throws IOException, MessagingException {
        final File basedir = new File(System.getProperty("basedir", "."));
        final File testInput = new File(basedir, "src/test/resources/wmtom.bin");
        BodyPart[] bps = new BodyPart[2]; 
        bps[0] = new MimeBodyPart(new FileInputStream(testInput));
        bps[1] = new MimeBodyPart(new FileInputStream(testInput));
        final MimeMultipart multiPart = new MimeMultipart("alternative",bps);
        final MimeBodyPart mimeBodyPart0 = (MimeBodyPart) multiPart.getBodyPart(0);
        final Object object0 = mimeBodyPart0.getContent();
        assertNotNull(object0);
        final MimeBodyPart mimeBodyPart1 = (MimeBodyPart) multiPart.getBodyPart(1);
        final Object object1 = mimeBodyPart1.getContent();
        assertNotNull(object1);
        assertEquals(multiPart.getCount(), 2);
        assertTrue(multiPart.getContentType().startsWith("multipart/alternative"));
    }

    protected void writeToSetUp() throws Exception {
        defaultMap = CommandMap.getDefaultCommandMap();
        final MailcapCommandMap myMap = new MailcapCommandMap();
        myMap.addMailcap("text/plain;;    x-java-content-handler=" + DummyTextHandler.class.getName());
        myMap.addMailcap("multipart/*;;    x-java-content-handler=" + DummyMultipartHandler.class.getName());
        CommandMap.setDefaultCommandMap(myMap);
    }

    protected void writeToTearDown() throws Exception {
        CommandMap.setDefaultCommandMap(defaultMap);
    }

    public static class DummyTextHandler implements DataContentHandler {
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getTransferData(final DataFlavor df, final DataSource ds) throws UnsupportedFlavorException, IOException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getContent(final DataSource ds) throws IOException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
            os.write(((String)obj).getBytes("ISO8859-1"));
        }
    }

    public static class DummyMultipartHandler implements DataContentHandler {
        public DataFlavor[] getTransferDataFlavors() {
            throw new UnsupportedOperationException();
        }

        public Object getTransferData(final DataFlavor df, final DataSource ds) throws UnsupportedFlavorException, IOException {
            throw new UnsupportedOperationException();
        }

        public Object getContent(final DataSource ds) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
            final MimeMultipart mp = (MimeMultipart) obj;
            try {
                mp.writeTo(os);
            } catch (final MessagingException e) {
                throw (IOException) new IOException(e.getMessage()).initCause(e);
            }
        }
    }
}
