/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Arnaud Kervern
 */

package org.nuxeo.studio.components.common.mapper.impl;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.studio.components.common.mapper.ExtensionMapper;
import org.nuxeo.studio.components.common.mapper.descriptors.EventListenerDescriptor;

public class EventMapper extends ExtensionMapper {
    public static List<String> CORE_EVENTS;

    static {
        // Registering Common Core events from Nuxeo classes in order to skip them during export as already
        // present in Studio
        // XXX Must be tied from an external place... Studio, lib, whatever.
        // For now extracted from {@code org.nuxeo.ecm.core.api.LifeCycleConstants} and {@code
        // org.nuxeo.ecm.core.api.event.DocumentEventTypes} and server-10.10 studio registries
        CORE_EVENTS = Arrays.asList("ACEStatusUpdated", //
                "aboutToCheckIn", //
                "aboutToCheckout", //
                "aboutToCopy", //
                "aboutToCreate", //
                "aboutToImport", //
                "aboutToMove", //
                "aboutToRemove", //
                "aboutToRemoveVersion", //
                "addedToCollection", //
                "beforeAddedToCollection", //
                "beforeDocumentModification", //
                "beforeDocumentSecurityModification", //
                "beforeRemovedFromCollection", //
                "beforeRestoringDocument", //
                "binaryTextUpdated", //
                "childrenOrderChanged", //
                "documentCheckedIn", //
                "documentCheckedOut", //
                "documentCreated", //
                "documentCreatedByCopy", //
                "documentDuplicated", //
                "documentImported", //
                "documentLocked", //
                "documentModified", //
                "documentMoved", //
                "documentProxyPublished", //
                "documentProxyUpdated", //
                "documentPublished", //
                "documentRemovalCanceled", //
                "documentRemoved", //
                "documentRestored", //
                "documentSecurityUpdated", //
                "documentTrashed", //
                "documentTagUpdated", //
                "documentUndeleted", //
                "documentUnlocked", //
                "documentUntrashed", //
                "emailDocumentSend", //
                "emptyDocumentModelCreated", //
                "incrementBeforeUpdate", //
                "initialLifecycleState", //
                "lifecycle_transition_event", //
                "removedFromCollection", //
                "sectionContentPublished", //
                "sessionSaved", //
                "subscriptionAssigned", //
                "userWorkspaceCreated", //
                "versionRemoved", //
                "workflowCanceled");
    }

    @Override
    public void registerDescriptors() {
        registerDescriptor("events", EventListenerDescriptor.class);
    }

    @Override
    protected boolean accept(String target, String point) {
        return "org.nuxeo.ecm.core.event.EventServiceComponent".equalsIgnoreCase(target) && "listener".equals(point);
    }

    @Override
    public boolean isEnabled(Object contribution) {
        EventListenerDescriptor eld = (EventListenerDescriptor) contribution;
        return eld.isEnabled() && eld.getEvents() != null
                && !eld.getEvents().stream().allMatch(CORE_EVENTS::contains);
    }
}
