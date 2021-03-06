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

package org.nuxeo.studio.components.common.mapper.xmap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@XMemberAnnotation(XMemberAnnotation.NODE_MAP)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XNodeMap {

    /**
     * A path expression specifying the XML node to bind to.
     *
     * @return the node xpath
     */
    String value();

    /**
     * Whether to trim text content for element nodes.
     *
     * @return if the text is trimmed or not
     */
    boolean trim() default true;

    /**
     * The path relative to the current node (which is located by {@link XNodeMap#value()}) which contain the map key to
     * be used.
     *
     * @return the map key
     */
    String key();

    /**
     * The type of collection object.
     *
     * @return the collection's type
     */
    Class type();

    /**
     * The type of the objects in this collection.
     *
     * @return the type of items
     */
    Class componentType();

    /**
     * Whether the container should be set to null when no value is specified.
     *
     * @return if null is the default
     */
    boolean nullByDefault() default false;

}
