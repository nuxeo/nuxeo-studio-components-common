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

package com.nuxeo.studio.components.common.bundle;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegistrationInfo {
    protected List<Extension> extensions = new ArrayList<>();

    protected String name;

    public RegistrationInfo(Document document) {
        Element rootElement = document.getRootElement();
        name = rootElement.valueOf("@name");

        List<Node> nodes = rootElement.selectNodes("//extension");
        nodes.stream().map(Extension::new).forEach(extensions::add);
    }

    public static RegistrationInfo read(InputStream io) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(io);
            return new RegistrationInfo(document);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Extension> getExtensions() {
        return new ArrayList<>(extensions);
    }

    public String getName() {
        return name;
    }
}
