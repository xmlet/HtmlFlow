/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package htmlflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Represents a set of character entity references defined by the
 * HTML 4.0 standard.
 *
 * <p>A complete description of the HTML 4.0 character set can be found
 * at https://www.w3.org/TR/html4/charset.html.
 *
 * @author Juergen Hoeller
 * @author Martin Kersten
 * @author Craig Andrews
 * @since 1.2.1
 */
class HtmlCharacterEntityReferences {
    /**
     * Default character encoding to use when {@code request.getCharacterEncoding}
     * returns {@code null}, according to the Servlet spec.
     */
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    static final String DECIMAL_REFERENCE_START = "&#";

    static final String HEX_REFERENCE_START = "&#x";

    static final char REFERENCE_END = ';';

    static final char CHAR_NULL = (char) -1;


    private final String[] characterToEntityReferenceMap = new String[3000];

    private final Map<String, Character> entityReferenceToCharacterMap = new HashMap<>(512);




    /**
     * Return the number of supported entity references.
     */
    public int getSupportedReferenceCount() {
        return this.entityReferenceToCharacterMap.size();
    }

    /**
     * Return true if the given character is mapped to a supported entity reference.
     */
    public boolean isMappedToReference(char character) {
        return isMappedToReference(character, DEFAULT_CHARACTER_ENCODING);
    }

    /**
     * Return true if the given character is mapped to a supported entity reference.
     */
    public boolean isMappedToReference(char character, String encoding) {
        return (convertToReference(character, encoding) != null);
    }

    /**
     * Return the reference mapped to the given character, or {@code null} if none found.
     * @since 4.1.2
     */
    public String convertToReference(char character, String encoding) {
        if (encoding.startsWith("UTF-")){
            if (character == '<') {
                return "&lt;";
            } else if (character == '>') {
                return "&gt;";
            } else if (character == '"') {
                return "&quot;";
            } else if (character == '&') {
                return "&amp;";
            } else if (character == '\'') {
                return "&#39;";
            } else {
                return null;
            }
        }
        else if (character < 1000 || (character >= 8000 && character < 10000)) {
            int index = (character < 1000 ? character : character - 7000);
            String entityReference = this.characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
        }
        return null;
    }

    /**
     * Return the char mapped to the given entityReference or -1.
     */
    public char convertToCharacter(String entityReference) {
        Character referredCharacter = this.entityReferenceToCharacterMap.get(entityReference);
        if (referredCharacter != null) {
            return referredCharacter;
        }
        return CHAR_NULL;
    }

}