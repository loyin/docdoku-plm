/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.server.viewers;

import com.docdoku.core.common.BinaryResource;
import com.docdoku.core.services.IBinaryStorageManagerLocal;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The ViewerUtils class expose helper methods around HTML viewers
 */
public class ViewerUtils {

    private ViewerUtils() {
    }

    /**
     * Returns the complete path for a BinaryResource
     *
     * @param binaryResource the binary resource to get URI from
     * @param uuid           an optional token for shared entities
     * @return the complete uri of the resource
     */
    public static String getURI(BinaryResource binaryResource, String uuid) {
        if (uuid == null) {
            return "/api/files/" + binaryResource.getFullName();
        } else {
            return "/api/files/" + binaryResource.getFullName() + "/uuid/" + uuid;
        }
    }

    /**
     * Compile and return an html container for a viewer
     *
     * @param storageManager Storage service
     * @param binaryResource the resource to render
     * @param uuid optional token for shared entities
     * @param viewer the compiled viewer
     * @param pdf true if resource can be rendered as pdf
     *
     * @return the compiled template
     * @throws IOException when mustache.execute fails
     */
    public static String getViewerTemplate(IBinaryStorageManagerLocal storageManager, BinaryResource binaryResource, String uuid, String viewer, boolean pdf) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("com/docdoku/server/viewers/viewer_template.mustache");
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("uriResource", ViewerUtils.getURI(binaryResource, uuid));
        if (pdf) {
            scopes.put("pdfUri", ViewerUtils.getURI(binaryResource, uuid) + "?output=pdf");
        }

        String externalURL = storageManager.getExternalStorageURI(binaryResource);
        scopes.put("externalUriResource", externalURL);

        String shortenExternalURL = storageManager.getShortenExternalStorageURI(binaryResource);
        scopes.put("shortenExternalUriResource", shortenExternalURL);

        scopes.put("fileName", binaryResource.getName());
        scopes.put("thisId", UUID.randomUUID().toString());

        scopes.put("viewer", viewer);

        StringWriter templateWriter = new StringWriter();
        mustache.execute(templateWriter, scopes).flush();

        return templateWriter.toString();
    }

}
