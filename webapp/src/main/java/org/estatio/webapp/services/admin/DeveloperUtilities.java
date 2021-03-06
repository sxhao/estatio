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
package org.estatio.webapp.services.admin;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.metamodel.services.devutils.DeveloperUtilitiesServiceDefault;

public class DeveloperUtilities extends DeveloperUtilitiesServiceDefault {

    /**
     * 'Move' the action underneath the 'Administration' menu item. 
     */
    @MemberOrder(name="Administration", sequence="90")
    @Override
    @ActionSemantics(Of.SAFE)
    public Clob downloadMetaModel() {
        return super.downloadMetaModel();
    }
    
    /**
     * 'Move' the action underneath the 'Administration' menu item. 
     */
    @MemberOrder(name="Administration", sequence="91")
    @ActionSemantics(Of.SAFE)
    @Override
    public Blob downloadLayouts() {
        return super.downloadLayouts();
    }
    
    /**
     * 'Move' the action underneath the 'Administration' menu item. 
     */
    @MemberOrder(name="Administration", sequence="92")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public void refreshServices() {
        super.refreshServices();
    }
}
