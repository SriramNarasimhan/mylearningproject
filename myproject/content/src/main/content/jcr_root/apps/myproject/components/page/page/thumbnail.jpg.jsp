<%@ page import="com.day.cq.commons.SlingRepositoryException,
                 com.day.cq.dam.api.Asset,
                 com.day.cq.wcm.foundation.ImageHelper,
                 com.day.image.Layer,
                 org.apache.sling.api.SlingHttpServletRequest,
                 org.apache.sling.api.SlingHttpServletResponse,
                 org.apache.sling.api.resource.Resource,
                 javax.jcr.Node,
                 javax.jcr.RepositoryException,
                 java.io.IOException" %><%!

    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        try {
            service((SlingHttpServletRequest) request,
                    (SlingHttpServletResponse) response);
        } catch (RepositoryException e) {
            throw new SlingRepositoryException(e);
        }
    }

    private void service(SlingHttpServletRequest req,
                         SlingHttpServletResponse resp)
            throws RepositoryException, IOException {
        Resource res = req.getResource();
        Node node;
        String[] sels = req.getRequestPathInfo().getSelectors();
        int width = 48;
        int height = 48;
        if (sels.length > 1) {
            try {
                width = Integer.parseInt(sels[1]);
                height = Integer.parseInt(sels[2]);
            } catch (Exception e) { }
        }
        if (res.adaptTo(Asset.class) != null) {
            node = res.adaptTo(Asset.class).getOriginal().adaptTo(Node.class);
        } else {
            node = res.adaptTo(Node.class);
        }
        Layer img = ImageHelper.createLayer(node.getSession(), node.getPath() + "/image/file");
        if (img != null) {
            img.resize(width, height > 0 ? height : Math.round(width / img.getWidth() * img.getHeight()));
        } else if (node.hasNode("image") &&
                node.getNode("image").hasProperty("fileReference")) {
            // try to get referenced image
            try {
                Asset asset = req.getResourceResolver().getResource(
                        node.getNode("image").getProperty("fileReference").getString()).
                        adaptTo(Asset.class);
                img = ImageHelper.createLayer(node.getSession(),
                        asset.getRendition("cq5dam.thumbnail." + width + "." + height + ".png").getPath());
            } catch (Exception e) {
                // unable to load referenced image
            }
        }
        if (img != null) {
            resp.setContentType("image/jpeg");
            img.write("image/jpeg", 0.8, resp.getOutputStream());
        }
    }
%>