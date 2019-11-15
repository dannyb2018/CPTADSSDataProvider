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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSField;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.nifi.logging.ComponentLog;

@Tags({"DSS data provider"})
@CapabilityDescription("Gets data from DSS")
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
    .name("Success")
    .description("Got data from DSS/Datastream")
    .build();
    
    private static final Relationship FAILURE = new Relationship
    .Builder()
    .name("FAILURE")
    .description("failed to get data from DSS/Datastream")
    .build();       
    
    private ComponentLog log = this.getLogger();

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
        FlowFile flowFile = session.get();
        if ( flowFile == null ) 
        {
            return;
        }

        String results = null;
        InputStream in = session.read(flowFile);
        try
        {
            // Read in the json
            JsonReader reader = Json.createReader(in);
            JsonObject request = reader.readObject();

            // Format is rics, which is a simple array of sttring
            final List<CPTAInstrumentSymbology> ricsArray = getRics(request);

            // Fields are array of field objects, 
            // which are message type and field
            List<CPTADSSField> fieldsArray = getRequestFields(request);

            // Finally there is the properties array
            List<CPTADSSProperty> requestPropertiesArray = getRequestProperties(request);
            // Feed that in to the dss api
            JsonObject response = makeRequest(context, ricsArray, fieldsArray, requestPropertiesArray);
            // Parse the result
            results = getResults(response);
        }
        catch(Exception E)
        {

        }

        // Write the results back out to flow file
        OutputStream out = session.write(flowFile);
        try
        {
            out.write(results.getBytes());
        }
        catch(IOException E)
        {
            
        }
        
        session.transfer(flowFile, SUCCESS);
    }
    
    
    protected List<CPTAInstrumentSymbology> getRics(JsonObject request)
    {
        JsonArray ricsAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.RICS_ARRAY_NAME);  
        // This is a list of 
        return null;
    }
    
    protected List<CPTADSSField> getRequestFields(JsonObject request)
    {
        JsonArray fieldsAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.FIELDS_ARRAY_NAME);
        return null;
    }
    
    protected List<CPTADSSProperty> getRequestProperties(JsonObject request)
    {
        JsonArray propertiesAsArray = request.getJsonArray(CPTADSSDataProviderProcessorConstants.PROPERTIES_ARRAY_NAME);
        return null;
    }
    
    protected JsonObject makeRequest(ProcessContext context, List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties)
    {
        // get the dss username, password
        String dssUserName = context.getProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY).getValue();
        String dssPassword = context.getProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY).getValue();
        // Get the datastream username, password
        return null;
    }
    
    protected String getResults(JsonObject response)
    {
        // Results are array of rics
        // each ric has a list of fields
        // Then there is an error with a list of rics
        return null;
    }    
}