/*

Copyright 2017-2019 Advanced Products Limited, 
dannyb@cloudpta.com
github.com/dannyb2018

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss;

import java.util.UUID;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Danny
 */
public class CPTADSSMessageTest
{
    /**
     * Test of getSessionToken method, of class CPTADSSMessage.
     */
    @Test
    public void testGetTokenRequest()
    {
        System.out.println("getTokenRequest");
        CPTADSSMessage instance = new CPTADSSMessage();
        String user = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        instance.user = user;
        instance.password = password;
        String tokenRequest = instance.getTokenRequest();
        
    }

    /**
     * Test of getData method, of class CPTADSSMessage.
     */
    @Test
    public void testGetData()
    {
        System.out.println("getData");
        long timeout = 0L;
        CPTADSSMessage instance = new CPTADSSMessage();
    }

    /**
     * Test of handleError method, of class CPTADSSMessage.
     */
    @Test
    public void testHandleError()
    {
        System.out.println("handleError");
        Response response = null;
        CPTADSSMessage instance = new CPTADSSMessage();
    }

    /**
     * Test of buildExtractionRequest method, of class CPTADSSMessage.
     */
    @Test
    public void testBuildExtractionRequest()
    {
        System.out.println("buildExtractionRequest");
        CPTADSSMessage instance = new CPTADSSMessage();
    }

    /**
     * Test of addConditions method, of class CPTADSSMessage.
     */
    @Test
    public void testAddConditions()
    {
        System.out.println("addConditions");
        JsonObjectBuilder detailsSpecificToThisExtractionRequest = null;
        CPTADSSMessage instance = new CPTADSSMessage();
    }

    /**
     * Test of getResult method, of class CPTADSSMessage.
     */
    @Test
    public void testGetResult_Response()
    {
        System.out.println("getResult");
        Response response = null;
        CPTADSSMessage instance = new CPTADSSMessage();
    }

    /**
     * Test of getMessageType method, of class CPTADSSMessage.
     */
    @Test
    public void testGetMessageType()
    {
        System.out.println("getMessageType");
        CPTADSSMessage instance = new CPTADSSMessage();
    }    
}
