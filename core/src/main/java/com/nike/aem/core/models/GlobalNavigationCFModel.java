package com.nike.aem.core.models;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nike.aem.core.servlets.GlobalNavServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = {GlobalNavigationCFModel.class, ComponentExporter.class},
        resourceType = {GlobalNavigationCFModel.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class GlobalNavigationCFModel implements ComponentExporter {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalNavServlet.class);

    public static final String RESOURCE_TYPE = "content-fragment";

    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    ModelFactory modelFactory;

    @SlingObject
    protected Resource resource;

    @JsonProperty
    private List<PropertiesCFModel> content = new ArrayList<>();

    private ContentFragment contentFragment;

    @PostConstruct
    private void initContentFragmentImpl() {
        if (resource != null) {
            contentFragment = resource.adaptTo(ContentFragment.class);
            if (contentFragment != null) {
                Iterator<ContentElement> iterator = contentFragment.getElements();
                while (iterator.hasNext()) {
                    ContentElement element = iterator.next();
                    content.add(getPropertyModel(element));
                }
            }
        }
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public List<PropertiesCFModel> getContent() {
        return content;
    }

    private PropertiesCFModel getPropertyModel(ContentElement element) {
        ResourceResolver resourceResolver = resource.getResourceResolver();
        PropertiesCFModel propertiesCFModel = new PropertiesCFModel();
        propertiesCFModel.setKey(element.getName());
        propertiesCFModel.setTitle(element.getTitle());
        propertiesCFModel.setDataType(element.getValue().getDataType().getValueType());
        boolean isMultiValue = element.getValue().getDataType().isMultiValue();
        propertiesCFModel.setMultiValue(isMultiValue);
        if (isMultiValue && element.getValue().getValue() instanceof String[]) {
            List<List<PropertiesCFModel>> nestedFragmentProperties = new ArrayList<>();
            String[] fragments = (String[]) element.getValue().getValue();
            for (String fragmentResPath : fragments) {
                Resource fragmentRes = resourceResolver.getResource(fragmentResPath);
                if (fragmentRes != null) {
                    List<PropertiesCFModel> nestedProperties = new ArrayList<>();
                    ContentFragment contentFragment1 = fragmentRes.adaptTo(ContentFragment.class);
                    Iterator<ContentElement> iterator = contentFragment1.getElements();
                    while (iterator.hasNext()) {
                        nestedProperties.add(getPropertyModel(iterator.next()));
                    }
                  nestedFragmentProperties.add(nestedProperties);
                }
            }
            propertiesCFModel.setProperties(nestedFragmentProperties);
        } else if (!isMultiValue && element.getValue().getValue() instanceof String) {
            String value = (String) element.getValue().getValue();
            propertiesCFModel.setValue(value);
        }
        LOG.info("Property CF Model for {} {} ", element.getName(),propertiesCFModel);
        return propertiesCFModel;
    }
}
