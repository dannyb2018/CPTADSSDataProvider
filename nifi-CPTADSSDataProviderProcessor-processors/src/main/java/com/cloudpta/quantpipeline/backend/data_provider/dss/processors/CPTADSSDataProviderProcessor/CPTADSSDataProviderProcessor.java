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
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor;

import com.cloudpta.quantpipeline.api.instrument.CPTAInstrumentConstants;
import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSField;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTARefinitivGetData;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.io.IOException;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.nifi.logging.ComponentLog;

@Tags({"DSS and Datastream data provider"})
@CapabilityDescription("Gets data from DSS and DataStream")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class CPTADSSDataProviderProcessor extends AbstractProcessor 
{
    private List<PropertyDescriptor> descriptors;
    
    // For DSS 
    // this is user name
    final PropertyDescriptor DSS_USER_NAME_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // password
    final PropertyDescriptor DSS_PASSWORD_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // base url
    final PropertyDescriptor DSS_BASE_URL_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DISPLAY)
    .defaultValue(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DEFAULT_VALUE)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // For DSWS 
    // this is user name
    final PropertyDescriptor DSWS_USER_NAME_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // password
    final PropertyDescriptor DSWS_PASSWORD_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // base url
    final PropertyDescriptor DSWS_BASE_URL_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DISPLAY)
    .defaultValue(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DEFAULT_VALUE)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    
    private Set<Relationship> relationships;
    private static final Relationship SUCCESS = new Relationship
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS)
    .description("Got data from DSS/Datastream")
    .build();
    
    private static final Relationship FAILURE = new Relationship
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_FAILURE)
    .description("failed to get data from DSS/Datastream")
    .build();       
    
    //private ComponentLog log = this.getLogger();

    @Override
    protected void init(final ProcessorInitializationContext context) 
    {
        // Build a list of properties
        final List<PropertyDescriptor> thisInstanceDescriptors = new ArrayList<>();

        thisInstanceDescriptors.add(DSS_USER_NAME_PROPERTY);
        thisInstanceDescriptors.add(DSS_PASSWORD_PROPERTY);
        thisInstanceDescriptors.add(DSWS_USER_NAME_PROPERTY);
        thisInstanceDescriptors.add(DSWS_PASSWORD_PROPERTY);
        // Set the descriptors
        descriptors = Collections.unmodifiableList(thisInstanceDescriptors);

        final Set<Relationship> thisInstanceRelationships = new HashSet<>();
        thisInstanceRelationships.add(SUCCESS);
        thisInstanceRelationships.add(FAILURE);
        // The two relationships are success and fail
        relationships = Collections.unmodifiableSet(thisInstanceRelationships);
    }

    @Override
    public Set<Relationship> getRelationships() 
    {
        return relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() 
    {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) 
    {
        
    }

    @Override
    public void onPropertyModified(PropertyDescriptor descriptor, String oldValue, String newValue)
    {
        super.onPropertyModified(descriptor, oldValue, newValue); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException 
    {
        // Always needs an incoming flow file
        FlowFile flowFile = session.get();
        if ( flowFile == null ) 
        {
            return;
        }

        // Has to be like this so we can access from within a lambda
        final AtomicReference<String> results = new AtomicReference<>();
        final AtomicReference<Relationship> statusOfQuery = new AtomicReference<>();
        // Has to be written like this otherwise will complain flow file isnt shut properly
        session.read
        (
            flowFile, in ->
            {
                try
                {
                    // Read in the json
                    JsonReader reader = Json.createReader(in);
                    JsonObject request = reader.readObject();

                    // Format is instruments, which is a simple array of json objects
                    // with identifier and identifier type
                    final List<CPTAInstrumentSymbology> instrumentsArray = getInstruments(request);

                    // Fields are array of field objects, 
                    // which are message type and field
                    List<CPTADSSField> fieldsArray = getRequestFields(request);

                    // Finally there is the properties array
                    List<CPTADSSProperty> requestPropertiesArray = getRequestProperties(request);
                    // Get data from refinitiv
                    String queryResults = getDataFromRefinitv(context, instrumentsArray, fieldsArray, requestPropertiesArray);
                    results.set(queryResults);
                    statusOfQuery.set(SUCCESS);
                }
                catch(CPTAException internalException)
                {
                    // Then there is an error
                    String error = internalException.getErrorsAsString();
                    results.set(error);
                    statusOfQuery.set(FAILURE);
                }
                catch(Exception unexpectedException)
                {
                    // There is another sort of error
                    CPTAException convertedException = new CPTAException(unexpectedException);
                    String error = convertedException.getErrorsAsString();
                    results.set(error);
                    statusOfQuery.set(FAILURE);                    
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch(Exception E2)
                    {

                    }
                }
            }
        );

        // Write the results back out to flow file
        // Has to be written like this otherwise will complain flow file isnt shut properly
        session.write
        (
            flowFile, out ->
            {
                try
                {
                    String resultsOfQuery = results.get();
                    out.write(resultsOfQuery.getBytes());
                }
                catch(IOException E)
                {

                }
                finally
                {
                    try
                    {
                        out.close();
                    }
                    catch(Exception E2)
                    {

                    }
                }
            }
        );

        // pass on the flow file along with whether the query succeeded or not
        session.transfer(flowFile, statusOfQuery.get());
    }
    
    
    protected List<CPTAInstrumentSymbology> getInstruments(JsonObject request)
    {
        // Should check that the json is properly formed at each stage otherwise throw an exception
        // Get the rics from the request, it is an array of json objects of instrument symbologies
        JsonArray ricsAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.INSTRUMENTS_ARRAY_NAME);  
        // Convert this list of rics to a list of symbols with type as rics
        List<CPTAInstrumentSymbology> instruments = new ArrayList<>();
        // Get the list so we iterate easily over it
        List<JsonObject> instrumentsAsJsonObjects = ricsAsArray.getValuesAs(JsonObject.class);        
        for(JsonObject currentInstrument: instrumentsAsJsonObjects)
        {
            // Create a symbology entry for this ric
            CPTAInstrumentSymbology currentInstrumentSymbology = new CPTAInstrumentSymbology();
            // get id
            String id = currentInstrument.getString(CPTAInstrumentConstants.ID_FIELD_NAME);            
            currentInstrumentSymbology.setID(id);
            // get id type
            String idType = currentInstrument.getString(CPTAInstrumentConstants.ID_SOURCE_FIELD_NAME);
            currentInstrumentSymbology.setIDSource(idType);
            // Add to list of instruments
            instruments.add(currentInstrumentSymbology);
        }
        
        // Return the list
        return instruments;
    }
    
    protected List<CPTADSSField> getRequestFields(JsonObject request)
    {
        // Get the fields from the request, it is an array of json objects representing the request fields
        JsonArray fieldsAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.FIELDS_ARRAY_NAME);
        // need to convert this from a json array of json objects to a list of fields
        List<CPTADSSField> fields = new ArrayList<>();
        // Get the json array as a list so we can iterate over it
        List<JsonObject> fieldsAsJsonObjects = fieldsAsArray.getValuesAs(JsonObject.class);
        for( JsonObject currentRequestFieldObject: fieldsAsJsonObjects)
        {
            // Turns the json object into a list of fields with name and message type
            CPTADSSField currentField = new CPTADSSField();
            currentField.messageType = currentRequestFieldObject.getString(CPTADSSDataProviderProcessorConstants.MESSAGE_TYPE_FIELD_NAME);
            currentField.name = currentRequestFieldObject.getString(CPTADSSDataProviderProcessorConstants.FIELD_NAME_FIELD_NAME);
            // Add it to list
            fields.add(currentField);
        }
        
        return fields;
    }
    
    protected List<CPTADSSProperty> getRequestProperties(JsonObject request)
    {
        // Get the properties from the request, it is an array of json objects representing the request fields
        JsonArray propertiesAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.PROPERTIES_ARRAY_NAME);
        // need to convert this from a json array of json objects to a list of properties
        List<CPTADSSProperty> properties = new ArrayList<>();
        // Get the json array as a list so we can iterate over it
        List<JsonObject> propertiesAsJsonObjects = propertiesAsArray.getValuesAs(JsonObject.class);
        for( JsonObject currentRequestPropertyObject: propertiesAsJsonObjects)
        {
            // Turns the json object into a list of properties with name and value
            CPTADSSProperty currentProperty = new CPTADSSProperty();
            currentProperty.name = currentRequestPropertyObject.getString(CPTADSSDataProviderProcessorConstants.PROPERTY_NAME_FIELD_NAME);
            currentProperty.value = currentRequestPropertyObject.getString(CPTADSSDataProviderProcessorConstants.PROPERTY_VALUE_FIELD_NAME);
            // Add it to list
            properties.add(currentProperty);
        }
        
        return properties;
    }
    
    protected String getDataFromRefinitv(ProcessContext context, List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties) throws CPTAException
    {
        ComponentLog log = this.getLogger();
        String result = CPTARefinitivGetData.getInstance().getData(log, context, symbols, fields, properties);
        return result;
    }    
}