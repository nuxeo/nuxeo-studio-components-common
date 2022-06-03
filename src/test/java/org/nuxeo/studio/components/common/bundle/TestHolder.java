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

package org.nuxeo.studio.components.common.bundle;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;

import org.nuxeo.studio.components.common.AbstractExtractorTest;
import org.nuxeo.studio.components.common.mapper.MappersManager;
import org.nuxeo.studio.components.common.mapper.descriptors.DocumentTypeDescriptor;
import org.nuxeo.studio.components.common.mapper.impl.AutomationMapper;
import org.nuxeo.studio.components.common.mapper.impl.TypeServiceMapper;

public class TestHolder extends AbstractExtractorTest {

    @Test
    public void testSkippedPartialContribution() throws URISyntaxException {
        final Class<DocumentTypeDescriptor> docTypeDesc = DocumentTypeDescriptor.class;

        RegistrationInfo ri = getRegistrationInfo("simple-doctype-contrib.xml");
        ContributionsHolder holder = new ContributionsHolder();
        holder.load(ri);

        assertEquals(2, holder.contributions.get(docTypeDesc.getName()).size());
        assertEquals(1, holder.getContributions(docTypeDesc).size());
    }

    @Test
    public void testMapperTargets() {
        MappersManager manager = new MappersManager();
        assertEquals(0, manager.getRegisteredTargets().length);

        // Add AutomationMapper
        manager.add(new AutomationMapper());
        String[] registeredTargets = manager.getRegisteredTargets();
        assertEquals(2, registeredTargets.length);
        assertEquals("event_handlers", registeredTargets[0]);
        assertEquals("operations", registeredTargets[1]);

        // Add TypeServiceMapper with 3 available targets
        manager.add(new TypeServiceMapper());
        assertEquals(5, manager.getRegisteredTargets().length);
    }
}
