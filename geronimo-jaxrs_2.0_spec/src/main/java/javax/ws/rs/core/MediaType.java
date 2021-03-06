/*
 * #%L
 * Apache Geronimo JAX-RS Spec 2.0
 * %%
 * Copyright (C) 2003 - 2014 The Apache Software Foundation
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


@SuppressWarnings("JavaDoc")
public class MediaType {

    private String type;
    private String subtype;
    private Map<String, String> parameters;


    public static final String CHARSET_PARAMETER = "charset";

    public static final String MEDIA_TYPE_WILDCARD = "*";


    public final static String WILDCARD = "*/*";

    public final static MediaType WILDCARD_TYPE = new MediaType();

    public final static String APPLICATION_XML = "application/xml";

    public final static MediaType APPLICATION_XML_TYPE = new MediaType("application", "xml");

    public final static String APPLICATION_ATOM_XML = "application/atom+xml";

    public final static MediaType APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");

    public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";

    public final static MediaType APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");

    public final static String APPLICATION_SVG_XML = "application/svg+xml";

    public final static MediaType APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");

    public final static String APPLICATION_JSON = "application/json";

    public final static MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");

    public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public final static MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");

    public final static String MULTIPART_FORM_DATA = "multipart/form-data";

    public final static MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");

    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public final static MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");

    public final static String TEXT_PLAIN = "text/plain";

    public final static MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");

    public final static String TEXT_XML = "text/xml";

    public final static MediaType TEXT_XML_TYPE = new MediaType("text", "xml");

    public final static String TEXT_HTML = "text/html";

    public final static MediaType TEXT_HTML_TYPE = new MediaType("text", "html");


    public static MediaType valueOf(String type) {
        return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).fromString(type);
    }

    private static TreeMap<String, String> createParametersMap(Map<String, String> initialValues) {
        final TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        if (initialValues != null) {
            for (Map.Entry<String, String> e : initialValues.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
        }
        return map;
    }


    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this(type, subtype, null, createParametersMap(parameters));
    }


    public MediaType(String type, String subtype) {
        this(type, subtype, null, null);
    }


    public MediaType(String type, String subtype, String charset) {
        this(type, subtype, charset, null);
    }


    public MediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD, null, null);
    }

    private MediaType(String type, String subtype, String charset, Map<String, String> parameterMap) {

        this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
        this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;

        if (parameterMap == null) {
            parameterMap = new TreeMap<String, String>(new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }

        if (charset != null && !charset.isEmpty()) {
            parameterMap.put(CHARSET_PARAMETER, charset);
        }
        this.parameters = Collections.unmodifiableMap(parameterMap);
    }


    public String getType() {
        return this.type;
    }


    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }


    public String getSubtype() {
        return this.subtype;
    }


    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }


    public Map<String, String> getParameters() {
        return parameters;
    }


    public MediaType withCharset(String charset) {
        return new MediaType(this.type, this.subtype, charset, createParametersMap(this.parameters));
    }


    public boolean isCompatible(MediaType other) {
        return other != null && (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD) ||
            (type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD))) ||
            (type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype)));
    }


    @SuppressWarnings("UnnecessaryJavaDocLink")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MediaType)) {
            return false;
        }

        MediaType other = (MediaType) obj;
        return (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && this.parameters.equals(other.parameters));
    }


    @SuppressWarnings("UnnecessaryJavaDocLink")
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }


    @Override
    public String toString() {
        return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).toString(this);
    }
}
