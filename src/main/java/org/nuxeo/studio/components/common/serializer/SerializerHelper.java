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

package org.nuxeo.studio.components.common.serializer;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.containsOnly;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializerHelper {
    private SerializerHelper() {
    }

    /**
     * Transform a camel case string into a more human readable one. It split by Character Camel case and capitalize the
     * result.
     * 
     * @param value String to humanize
     * @return the String humanized
     */
    public static String humanize(String value) {
        String[] words = splitByCharacterTypeCamelCase(value);
        return capitalize(Stream.of(words)
                                .filter(w -> !containsOnly(w, '_', '-'))
                                .map(w -> lowerCase(w))
                                .collect(Collectors.joining(" ")));
    }
}
