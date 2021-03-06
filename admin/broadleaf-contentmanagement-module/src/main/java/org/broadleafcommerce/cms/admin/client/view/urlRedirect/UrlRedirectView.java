/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.client.view.urlRedirect;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.view.user.BasicListDetailView;
import org.broadleafcommerce.openadmin.client.view.user.PermissionManagementDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class UrlRedirectView extends BasicListDetailView implements PermissionManagementDisplay {

    @Override
    public String getViewPrefix() {
        return "permission";
    }

    @Override
    public String getFormTitle() {
        return BLCMain.getMessageManager().getString("permissionDetailsTitle");
    }

    @Override
    public String getListTitle() {
        return BLCMain.getMessageManager().getString("permissionListTitle");
    }
}