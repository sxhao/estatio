/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.services.links;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.danhaywood.isis.domainservice.stringinterpolator.StringInterpolatorService;
import com.danhaywood.isis.domainservice.stringinterpolator.StringInterpolatorService.Root;

import org.estatio.services.settings.EstatioSettingsService;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;

public class LinkContributions {

    @NotInServiceMenu
    @Named("Reports")
    public URL openLink(
            final Object domainObject, 
            final @Named("Report") Link link) throws MalformedURLException {
        final Root root = new Root(domainObject){
            @SuppressWarnings("unused")
            public String getReportServerBaseUrl() {
                return estatioSettingsService.fetchReportServerBaseUrl();
            }
        };
        final String urlStr = stringInterpolator.interpolate(root, link.getUrlTemplate());
        return new URL(urlStr);
    }
    
    public boolean hideOpenLink(Object domainObject, Link link) {
         return links.findAllForClassHierarchy(domainObject).isEmpty();
    }

    public List<Link> choices1OpenLink(Object domainObject, Link link) {
        return links.findAllForClassHierarchy(domainObject);
    }
    
    
    // //////////////////////////////////////

    @javax.inject.Inject
    private StringInterpolatorService stringInterpolator;

    @javax.inject.Inject
    private Links links;
    
    @javax.inject.Inject
    private EstatioSettingsService estatioSettingsService;
}
