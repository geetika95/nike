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
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = {GlobalNavCFModel.class, ComponentExporter.class},
        resourceType = {GlobalNavCFModel.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class GlobalNavCFModel implements ComponentExporter {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalNavServlet.class);

    public static final String RESOURCE_TYPE = "global-nav-fragment";

    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    ModelFactory modelFactory;

    @SlingObject
    protected Resource resource;

    @JsonProperty
    private String id;

    @JsonProperty
    private String logoUrl;

    @JsonProperty
    private String logoImage;

    @JsonProperty
    private String searchPlaceholderText;

    @JsonProperty
    private List<TopMenuNavCFModel> topMenuItems = new ArrayList<>();

    private ContentFragment contentFragment;

    @PostConstruct
    private void initContentFragmentImpl() {
        if (resource != null) {
            contentFragment = resource.adaptTo(ContentFragment.class);
            ResourceResolver resourceResolver = resource.getResourceResolver();
            if (contentFragment != null) {
                id = contentFragment.getElement("id").getContent();
                searchPlaceholderText = contentFragment.getElement("searchPlaceholderText").getContent();
                logoUrl = contentFragment.getElement("logoUrl").getContent();
                logoImage = contentFragment.getElement("logoUrl").getContent();
                ContentElement referenceElement = contentFragment.getElement("references");
                if (referenceElement.getValue().getValue() instanceof String[]) {
                    String[] fragments = (String[]) referenceElement.getValue().getValue();
                    for (String fragmentResPath : fragments) {
                        Resource fragmentRes = resourceResolver.getResource(fragmentResPath);
                        if (fragmentRes != null) {
                            topMenuItems.add(modelFactory.createModel(fragmentRes, TopMenuNavCFModel.class));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public String getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public String getSearchPlaceholderText() {
        return searchPlaceholderText;
    }

    public List<TopMenuNavCFModel> getTopMenuItems() {
        return topMenuItems;
    }
}
