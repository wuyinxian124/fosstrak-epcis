/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.SubscriptionControlsException;

/**
 * Tests for exceptions and error messages (SE51-SE65, SE68-SE72, SE74).
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class ErrorMessagesTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if InvalidURIException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE52() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE52-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            fis.close();
            client.unsubscribe("QuerySE52"); // clean up
            fail("InvalidURIException expected");
        } catch (InvalidURIException e) {
            fis.close();
            assertEquals("Destination URI is invalid: unknown protocol: htto",
                    e.getReason());
        }
    }

    /**
     * Tests if DuplicateSubscriptionException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE53() throws IOException, ServiceException {
        // subscribe first query
        final String query = "Test-EPCIS10-SE53-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
        fis.close();

        // subscribe second query
        final String query2 = "Test-EPCIS10-SE53-Request-2-Subscribe.xml";
        fis = new FileInputStream(PATH + query2);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE53"); // clean up
            fail("DuplicateSubscriptionException expected");
        } catch (DuplicateSubscriptionException e) {
            fis.close();
            client.unsubscribe("QuerySE53"); // clean up
            assertEquals(
                    "SubscriptionID 'QuerySE53' already exists. Choose a different subscriptionID.",
                    e.getReason());
        }
    }

    /**
     * Tests if NoSuchSubscriptionException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE54() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE54-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
        fis.close();

        // try to unsubscribe a non existing query
        try {
            client.unsubscribe("QuerySE54-2");
            // fail
            client.unsubscribe("QuerySE54-1"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (NoSuchSubscriptionException e) {
            // ok
            client.unsubscribe("QuerySE54-1");
            assertEquals("There is no subscription with ID 'QuerySE54-2'.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE55() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE55-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE55"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '61' for parameter 'second' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE56() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE56-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE56"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '-1' for parameter 'second' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value invalid).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE57() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE57-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE57"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'second' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE58() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE58-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE58"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '[1-8]' for parameter 'dayOfWeek' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value
     * invalid).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE59() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE59-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE59"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'x' for parameter 'dayOfWeek' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE60() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE60-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE60"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '-1' for parameter 'minute' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE61() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE61-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE61"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '61' for parameter 'minute' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value invalid).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE62() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE62-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE62"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'minute' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE63() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE63-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE63"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '24' for parameter 'hour' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE64() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE64-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE64"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value '-1' for parameter 'hour' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE65() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE65-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE65"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'hour' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (parameter name not defined).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE70() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE70-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals("The parameter EQ_abcd cannot be recognised.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (invalid parameter value).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE71() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE71-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals(
                    "The input value for parameter GE_quantity (3.1459) of eventType QuantityEvent is not of the type required.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (multiple occurences of same
     * parameter).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE72() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE72-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals(
                    "Two or more inputs are provided for the same parameter 'EQ_bizStep'.",
                    e.getReason());
        }
    }
}
