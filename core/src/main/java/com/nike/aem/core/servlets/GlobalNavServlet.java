package com.nike.aem.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nike.aem.core.models.GlobalNavigationCFModel;
import com.nike.aem.core.services.FragmentService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_PATHS + "=/api/getGlobalNav",
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                SLING_SERVLET_EXTENSIONS + "=" + "json"
        }
)
@ServiceDescription("Global Nav Servlet")
public class GlobalNavServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalNavServlet.class);

    @Reference
    private FragmentService contentFragmentService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            String contentFragmentResourcePath = "/content/dam/nike/content-fragments/us/en/navigation/global-navigation";

            GlobalNavigationCFModel contentFragmentModel = contentFragmentService.getFragment(request.getResourceResolver(), contentFragmentResourcePath,GlobalNavigationCFModel.class);

            if (contentFragmentModel != null) {
                // Serialize the content fragment to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(contentFragmentModel);
                // Set response properties
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
            }
        } catch (Exception e) {
            LOG.error("Exception occured {}", e.getMessage());
        }
    }

}

