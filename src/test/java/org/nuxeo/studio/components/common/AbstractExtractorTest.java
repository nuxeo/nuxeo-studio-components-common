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

package org.nuxeo.studio.components.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.nuxeo.studio.components.common.bundle.BundleWalker;
import org.nuxeo.studio.components.common.bundle.ContributionsHolder;
import org.nuxeo.studio.components.common.bundle.RegistrationInfo;
import org.nuxeo.studio.components.common.runtime.ExtractorContext;
import org.nuxeo.studio.components.common.serializer.StudioSerializer;

import junit.framework.AssertionFailedError;
import net.sf.json.test.JSONAssert;

public class AbstractExtractorTest {
    protected ExtractorOptions opts;

    protected BundleWalker walker;

    @Before
    public void beforeEach() throws IOException {
        walker = new BundleWalker(new File("."));
        opts = new ExtractorOptions();
    }

    @After
    public void afterEach() {
        ExtractorContext.instance.clearExternalSources();
    }

    protected RegistrationInfo getRegistrationInfo(String resourcePath) throws URISyntaxException {
        URL resource = TestHelper.getResource(resourcePath);
        assertNotNull(resource);

        RegistrationInfo ri = walker.read(new File(resource.toURI()).toPath());
        assertNotNull(ri);

        return ri;
    }

    protected ContributionsHolder loadComponent(String filename) throws URISyntaxException {
        RegistrationInfo ri = getRegistrationInfo(filename);
        ContributionsHolder holder = new ContributionsHolder();
        holder.load(ri);
        return holder;
    }

    protected void loadResources(String... filenames) {
        ContributionsHolder holder = new ContributionsHolder();
        URL resource = TestHelper.getResource(filenames[0]);
    }

    protected <T> void assertSerialization(String component, Class<T> descriptor, int size, String expectedJson)
            throws URISyntaxException {
        ContributionsHolder holder = loadComponent(component);
        assertEquals(size, holder.getContributions(descriptor).size());

        StudioSerializer serializer = new StudioSerializer(holder, ExtractorOptions.DEFAULT);
        String result = serializer.serializeDescriptors(descriptor);
        assertJsonEquals(expectedJson, result);
    }

    protected void assertJsonEquals(String expected, String actual) {
        try {
            JSONAssert.assertJsonEquals(expected, actual);
        } catch (AssertionFailedError e) {
            System.out.println(String.format("Expected: %s", expected));
            System.out.println(String.format("Actual: %s", actual));
            throw e;
        }
    }
}
