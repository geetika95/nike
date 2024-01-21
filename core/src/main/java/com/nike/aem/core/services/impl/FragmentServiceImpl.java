package com.nike.aem.core.services.impl;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.nike.aem.core.services.FragmentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = FragmentService.class)

public class FragmentServiceImpl implements FragmentService{
    @Reference
    private ModelFactory modelFactory;

    @Override
    public <T> T getFragment(ResourceResolver resourceResolver, String fragmentResourcePath, Class<T> modelClass) {
        if (StringUtils.isNotEmpty(fragmentResourcePath) && resourceResolver != null) {
            Resource fragmentResource = resourceResolver.getResource(fragmentResourcePath);
            if (fragmentResource != null) {
                return modelFactory.createModel(fragmentResource, modelClass);
            }
        }
        return null;
    }
}
