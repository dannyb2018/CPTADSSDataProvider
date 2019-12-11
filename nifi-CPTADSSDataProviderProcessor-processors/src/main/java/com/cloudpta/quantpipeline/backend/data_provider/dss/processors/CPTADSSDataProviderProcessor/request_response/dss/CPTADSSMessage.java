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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.CPTADSSDataProviderProcessorConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTARefinitivMessage;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTADSSMessage extends CPTARefinitivMessage
{        
    @Override
    public JsonObject getResult
                              (
                              ComponentLog logger,
                              ProcessContext context, 
                              List<CPTAInstrumentSymbology> symbols, 
                              List<String> fields, 
                              List<CPTADSSProperty> properties
                              )
    {
        msgLogger = logger;
        // Get username and password from context
        context.getProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY).getValue();
        context.getProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY).getValue();
        // Get the data
        // Convert it to standardard format
        return null;
    }
                              
    /**
     * Request DSS2 for a Session Token (24 hour life)
     * 
     */
    protected void getSessionToken() 
    {
        msgLogger.trace("getting session token, user=" + user + ", password=" + password);
        Client client = javax.ws.rs.client.ClientBuilder.newClient();            
        String url = urlHost + "/Authentication/RequestToken";
        WebTarget webTarget = client.target(url);
        Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        builder.header("content-type", "application/json; odata=minimalmetadata");

        JsonObjectBuilder tokenRequestBuilder = Json.createObjectBuilder();
        JsonObjectBuilder credentialsBuilder = Json.createObjectBuilder();
        credentialsBuilder.add("Username", user);
        credentialsBuilder.add("Password", password);
        tokenRequestBuilder.add("Credentials", credentialsBuilder);
        String tokenRequestAsString = tokenRequestBuilder.build().toString();
        msgLogger.trace("token request " + tokenRequestAsString);
        Response response = builder.post(Entity.json(tokenRequestAsString));
        // Let make sure the authentication is ok
        if(200 != response.getStatus())
        {
            // If not then handle the error
            handleError(response);
        }
        String stringResponse = response.readEntity(String.class);
        msgLogger.trace("token response " + tokenRequestAsString);

        JsonObject jsonResponse = Json.createReader(new StringReader(stringResponse)).readObject();

        sessionToken = jsonResponse.getString("value");
        msgLogger.trace("token is " + sessionToken);
    }
    
    // BUGBUGDB set the timeout
    public JsonArray getData(long timeout)
    {
        msgLogger.trace("getting data");
        // Get session token first, if dont have it
        if( null == sessionToken )
        {
            msgLogger.trace("getting session token");
            getSessionToken();            
        }
        
        JsonArray result = null;
        msgLogger.trace("About to make request for data");
        Client client = javax.ws.rs.client.ClientBuilder.newClient();            
        String url = urlHost + "/Extractions/ExtractWithNotes";
        msgLogger.trace("url for requesting data " + url);
        WebTarget webTarget = client.target(url);
        Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        builder.header("content-type", "application/json; odata=minimalmetadata");
        msgLogger.trace("session token is " + sessionToken);
        builder.header("Authorization", "Token " + sessionToken);
        builder.header("Prefer", "respond-async");

        String extractionRequest = buildExtractionRequest();
        msgLogger.trace("setting the body of the request " + extractionRequest);
        msgLogger.trace("making request");
        Response response = builder.post(Entity.json(extractionRequest));
        // Get the response status code
        int respStatusCode = response.getStatus();
        msgLogger.trace("got response, status " + Integer.toString(respStatusCode) );

        // If it is either of the successes
        if( 200 == respStatusCode )
        {
            msgLogger.trace("HTTP status: " + respStatusCode + " - A response is available now!" );
            msgLogger.trace("got data");
            result = getResult(response);

        }
        else if( 202 == respStatusCode )
        {
            msgLogger.trace("HTTP status: " + respStatusCode + " - We must wait, and poll the location URL." );
            msgLogger.trace("data not ready yet, gotta poll");

            MultivaluedMap<String, String> headers = response.getStringHeaders();
            List<String> locations = headers.get("Location");
            String pollURL = "";
            if( 0 != locations.size())
            {
                pollURL = locations.get(0);
            }
            else
            {
                // BUGBUGDB no url to poll, throw exception
            }
            msgLogger.trace("poll url " + pollURL);
            client = javax.ws.rs.client.ClientBuilder.newClient();            
            webTarget = client.target(pollURL);
            builder = webTarget.request(MediaType.APPLICATION_JSON);

            builder.header("Authorization", "Token " + sessionToken);
            builder.header("Prefer", "respond-async");
            response = builder.get();
            respStatusCode = response.getStatus();
            msgLogger.trace("HTTP status: " + respStatusCode);

            // Poll the location URL until the extraction is completed:
            while(202==respStatusCode) 
            {
                msgLogger.trace("sleeping for " + Long.toString(timeout) + ", will poll after");
                sleep(timeout);
                msgLogger.trace("trying to get data");
                response = builder.get();
                respStatusCode = response.getStatus();
                msgLogger.trace("HTTP status: " + respStatusCode);
            }

            // A response should be available now:
            if (respStatusCode == 200) 
            {
                msgLogger.trace("got data");
                result = getResult(response);
            }                
        }
        else
        {
            msgLogger.trace("returned an error");
            handleError(response);
        }
        
        msgLogger.trace("returning result " + result.toString());
        return result;
    }
    
    protected void handleError(Response response)
    {
        msgLogger.trace("Got an error");
        // Get the response code
        int respStatusCode = response.getStatus();

        String jsonAsString = response.readEntity(String.class);
        msgLogger.trace("error text is " + jsonAsString);
        // Read in all the text
        msgLogger.trace("parsing error text");
        JsonObject responseAsJson = Json.createReader(new StringReader(jsonAsString)).readObject();
        responseAsJson = responseAsJson.getJsonObject("error");
        String errorMessage = responseAsJson.getString("message");
        msgLogger.trace("error text successfully parsed, error message is " + errorMessage);
        msgLogger.error("Error requesting data, " + Integer.toString(respStatusCode)+ ", response is " + jsonAsString + ", errorMsg is " + errorMessage);

        // handle the error
        switch(respStatusCode)
        {
            case 400:
            {
                msgLogger.error("HTTP status: 400 (Bad Request).  Request content is malformed and cannot be parsed");
                break;
            }
            //HTTP/1.1 403 Forbidden
            case 403:
            {
                msgLogger.error("HTTP status: HTTP status: 403 (Forbidden).  Account not permissioned for this type of data");
                break;                
            }
            case 401:
            {
                msgLogger.error("HTTP status: HTTP status: 401 (Unauthorized).  Authentication token is invalid or has expired");
                break;                                
            }
            default:
            {
                msgLogger.error("Cannot proceed. Please check the meaning of HTTP status " + respStatusCode);
                break;
            }
        }
        
        msgLogger.trace("building exception");
        ArrayList<String> errors = new ArrayList<>();
        errors.add(errorMessage);
        msgLogger.trace("throwing exception");
    }
    
    protected String buildExtractionRequest()
    {
        msgLogger.trace("building extraction request");
        JsonObjectBuilder detailsSpecificToThisExtractionRequest = Json.createObjectBuilder();
        //extractionType
        detailsSpecificToThisExtractionRequest.add("@odata.type", extractionType);
        // Build up fields to use
        msgLogger.trace("adding list of fields");
        JsonArrayBuilder fieldNames = Json.createArrayBuilder();
        for( String currentField : fields)
        {
            msgLogger.trace("adding field=" + currentField);
            fieldNames.add(currentField);                
        }

        detailsSpecificToThisExtractionRequest.add("ContentFieldNames", fieldNames);

        // Build up ids
        JsonArrayBuilder identifiers = Json.createArrayBuilder();
        msgLogger.trace("adding list of instruments");
        for( CPTAInstrumentSymbology currentSymbol : instrumentList)
        {
            msgLogger.trace("adding instrument id=" + currentSymbol.getID() + ", id source=" + currentSymbol.getIDSource());
            JsonObjectBuilder id = Json.createObjectBuilder();
            // Set the id
            id.add("Identifier", currentSymbol.getID());
            // set the id type
            id.add("IdentifierType", currentSymbol.getIDSource());

            msgLogger.trace("instrument added is " + id.toString());
            // Add to ids
            identifiers.add(id);
        }
        JsonObjectBuilder identifierList = Json.createObjectBuilder();
        identifierList.add("@odata.type", "#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.InstrumentIdentifierList");
        identifierList.add("InstrumentIdentifiers", identifiers);
        identifierList.add("ValidationOptions", JsonObject.NULL);
        identifierList.add("UseUserPreferencesForValidationOptions", false);
        detailsSpecificToThisExtractionRequest.add("IdentifierList", identifierList);

        // Add the conditions
        addConditions(detailsSpecificToThisExtractionRequest);

        JsonObjectBuilder extractionRequest = Json.createObjectBuilder();
        extractionRequest.add("ExtractionRequest", detailsSpecificToThisExtractionRequest);

        msgLogger.trace("extraction JSON request content:"+ extractionRequest.toString() +"");
        
        return extractionRequest.toString();
    }
    
    
    protected void addConditions(JsonObjectBuilder detailsSpecificToThisExtractionRequest)
    {
        // default is null, but will be overrrided for some messages
        detailsSpecificToThisExtractionRequest.addNull("Condition");
    }
    
    protected JsonArray getResult(Response response)
    {
        JsonObject responseContent = null;
    	String jsonAsString = response.readEntity(String.class);
        responseContent = Json.createReader(new StringReader(jsonAsString)).readObject();
        msgLogger.trace("result is " + jsonAsString);
        // Response looks something like
        //{"@odata.context":"https://hosted.datascopeapi.reuters.com/RestApi/v1/$metadata#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.ExtractionResult","Contents":[{"IdentifierType":"Ric","Identifier":"2618.TW","Close Price":16.05}],"Notes":["Extraction Services Version 11.3.38127 (a1f54c7bcc67), Built Dec 18 2017 20:28:34\r\nHoliday Rollover of Universal Close Price requested.\r\nProcessing started at 2018/01/10 20:42:20.\r\nUser ID: 9015802\r\nExtraction ID: 288690088\r\nSchedule: _OnD_0x060542a7f48b2f86 (ID = 0x060542a8975b2f86)\r\nInput List (1 items): _OnD_0x060542a7f48b2f86 (ID = 060542a8560b2f86) Created: 2018/01/10 20:42:13 Last Modified: 2018/01/10 20:42:14\r\nSchedule Time: 2018/01/10 20:42:15\r\nReport Template (7 fields): _OnD_0x060542a7f48b2f86 (ID = 0x060542a836cb2f86) Created: 2018/01/10 20:42:13 Last Modified: 2018/01/10 20:42:13\r\nProcessing completed successfully at 2018/01/10 20:42:20, taking 0.685 Secs.\r\nExtraction finished at 2018/01/10 12:42:20 UTC, with servers: x02A05, QSHA02 (0.0 secs), QSHC20 (0.0 secs)\r\nUsage Summary for User 9015802, Client 107597, Template Type EOD Pricing\r\nBase Usage\r\n        Instrument                          Instrument                   Terms          Price\r\n  Count Type                                Subtype                      Source         Source\r\n------- ----------------------------------- ---------------------------- -------------- ----------------------------------------\r\n      1 Equities                                                         N/A            N/A\r\n-------\r\n      1 Total instrument charged.\r\n      0 Instruments with no reported data.\r\n=======\r\n      1 Instrument in the input list.\r\nNo TRPS complex usage to report -- 1 Instrument in the input list had no reported data.\r\nWriting RIC maintenance report.\r\n","Identifier,IdentType,Source,RIC,RecordDate,MaintType,OldValue,NewValue,Factor,FactorType\r\n"]}
        
        // Extract the values from the content
        // So look for the field saying Contents
        JsonArray result = responseContent.getJsonArray("Contents");
        
        return result;
    }

    protected void sleep(long seconds) 
    {
        try 
        {
            System.out.println("Waiting " + seconds + " seconds ..");
            Thread.sleep(seconds * 1000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }        
    }
    
    @Override
    public String getMessageType()
    {
        return messageType;
    }
    
    String messageType;
    String user = null;
    String password = null;
    protected String urlHost = "https://hosted.datascopeapi.reuters.com/RestApi/v1";
    static protected String sessionToken = null;
    protected List<CPTAInstrumentSymbology> instrumentList;
    protected String extractionType = null;
    protected List<String> fields;
    protected Calendar startDate = null;
    protected Calendar endDate = null;    
    protected ComponentLog msgLogger = null;
}
