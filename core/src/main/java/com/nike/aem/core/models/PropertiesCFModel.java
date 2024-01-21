package com.nike.aem.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nike.aem.core.servlets.GlobalNavServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = {PropertiesCFModel.class, ComponentExporter.class},
        resourceType = {PropertiesCFModel.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PropertiesCFModel implements ComponentExporter {
    public static final String RESOURCE_TYPE = "content-fragment";

    @SlingObject
    protected Resource resource;

    @JsonProperty
    private String title;

    @JsonProperty
    private String value;

    @JsonProperty
    private String key;

    @JsonProperty
    private String dataType;

    @JsonProperty
    private boolean isMultiValue;

    @JsonProperty
    private List<List<PropertiesCFModel>> properties;

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public boolean isMultiValue() {
        return isMultiValue;
    }

    public void setMultiValue(boolean multiValue) {
        isMultiValue = multiValue;
    }

    public String getDataType() {
        return dataType;
    }

    public List<List<PropertiesCFModel>> getProperties() {
        return properties;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setProperties(List<List<PropertiesCFModel>> properties) {
        this.properties = properties;
    }
}
