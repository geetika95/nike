package com.nike.aem.core.services;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public interface FragmentService {

    public <T> T getFragment(ResourceResolver resourceResolver, String fragmentResourcePath, Class<T> modelClass) ;
}
