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
import java.util.Iterator;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = {SubNavMenuCFModel.class, ComponentExporter.class},
        resourceType = {SubNavMenuCFModel.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SubNavMenuCFModel implements ComponentExporter {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalNavServlet.class);

    public static final String RESOURCE_TYPE = "sub-nav-fragment";

    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    ModelFactory modelFactory;

    @SlingObject
    protected Resource resource;
    @JsonProperty
    private String id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String url;

    @JsonProperty
    private List<LinkCFModel> navigationLinks = new ArrayList<>();

    private ContentFragment contentFragment;

    @PostConstruct
    private void initContentFragmentImpl() {
        if (resource != null) {
            contentFragment = resource.adaptTo(ContentFragment.class);
            ResourceResolver resourceResolver = resource.getResourceResolver();
            if (contentFragment != null) {
                id = contentFragment.getElement("id").getContent();
                title = contentFragment.getElement("title").getContent();
                url = contentFragment.getElement("url").getContent();
                ContentElement navLinks = contentFragment.getElement("navigationLinks");
                if (navLinks.getValue().getValue() instanceof String[]) {
                    String[] fragments = (String[]) navLinks.getValue().getValue();
                    for (String fragmentResPath : fragments) {
                        Resource fragmentRes = resourceResolver.getResource(fragmentResPath);
                        if (fragmentRes != null) {
                            navigationLinks.add(modelFactory.createModel(fragmentRes, LinkCFModel.class));
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

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<LinkCFModel> getNavigationLinks() {
        return navigationLinks;
    }
}
