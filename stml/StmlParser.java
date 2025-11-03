/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package stml;

import stml.obj.*;
import stml.struct.StmlScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StmlParser {
    String stml;
    boolean parsedFlag;
    /**
     * Create a new StmlParser instance to parse the given file.
     * @param file The file to parse.
     * @throws IOException If the file cannot be read.
     */
    public StmlParser(File file) throws IOException {
        this.stml = Files.readString(file.toPath());
    }
    public void parse(){
        if (parsedFlag) return;
        this.stml = stml.trim();
        // Start reading the STML file
        int n = stml.length();
        int i = 0;
        int currentLine = 1;
        int currentColumn = 0;
        // Establish global scope
        StmlScope globalScope = new StmlScope();
        while (i < n) {
            char c = stml.charAt(i);
            // Comments, annotations, preprocessor directives
            if (c == '#')
            {
                i++; currentColumn++;
                if (stml.charAt(i) == '@')
                {
                    // Annotations (#type, #define, #@ etc.)
                    // Read until end of line or #
                    StringBuilder annotationContent = new StringBuilder();
                    boolean inDoubleQuotes = false;
                    boolean inSingleQuotes = false;
                    while (i < n) {
                        char ic = stml.charAt(i);
                        if (!inDoubleQuotes && !inSingleQuotes) {
                            if (ic == '\n' && (i + 1 >= n || stml.charAt(i + 1) != '\\')) {
                                // End of line
                                currentLine++;
                                currentColumn = 1;
                                i++;
                                break;
                            } else if (ic == '#') {
                                // Next directive
                                break;
                            } else if (ic == '"') {
                                inDoubleQuotes = true;
                            } else if (ic == '\'') {
                                inSingleQuotes = true;
                            }
                        } else {
                            if (ic == '\n') {
                                currentLine++;
                                currentColumn = 1;
                            }
                            if (inDoubleQuotes && ic == '"') {
                                inDoubleQuotes = false;
                            } else if (inSingleQuotes && ic == '\'') {
                                inSingleQuotes = false;
                            }
                        }
                        annotationContent.append(ic);
                        i++; currentColumn++;
                    }
                    String[] annotationStrs = annotationContent.toString().trim().split(",");
                    for (String annotStr : annotationStrs) {
                        annotStr.trim(); // TODO: process annotations
                        System.out.println(annotStr);// DEBUG
                    }
                } else if (stml.substring(i, Math.min(i + 7, n)).equals("define ")) {
                    // Define directive
                    i += 7; currentColumn += 7;
                    // TODO: implement define directive
                } else if (stml.substring(i, Math.min(i + 5, n)).equals("type ")) {
                    // Type definition
                    i += 5; currentColumn += 5;
                    // TODO: implement type definitions
                }
                while (i < n)
                {
                    char ic = stml.charAt(i);
                    if (ic == '\n') {
                        currentLine++;
                        currentColumn = 0;
                    }
                    // Exit if reach end of line (not escaped, as \ is allowed for line continuation)
                    if (ic == '\n' && (i + 1 >= n || stml.charAt(i + 1) != '\\')) {
                        i++; currentColumn++;
                        break;
                    }
                    // Exit if we encounter ##
                    if (ic == '#' && i + 1 < n && stml.charAt(i + 1) == '#') {
                        i += 2; currentColumn+=2;
                        break;
                    }
                    i++; currentColumn++;
                }
            }
            else if (c == '[')
            {
                // Start of a scope, read until matching ]
                int start = i;
                i++; currentColumn++;
                boolean inDoubleQuotes = false;
                boolean inSingleQuotes = false;
                while (i < n) {
                    char ic = stml.charAt(i);
                    if (inDoubleQuotes || inSingleQuotes) {
                        if (inDoubleQuotes && ic == '"') {
                            inDoubleQuotes = false;
                        } else if (inSingleQuotes && ic == '\'') {
                            inSingleQuotes = false;
                        }
                        if (ic == '\n') {
                            currentLine++;
                            currentColumn = 1;
                        }
                    } else {
                        if (ic == '\n') {
                            // Cannot escape newlines in scope names
                            throw new StmlParseException("Newline in scope name", currentLine, currentColumn);
                        } else if (ic == '"') {
                            inDoubleQuotes = true;
                        } else if (ic == '\'') {
                            inSingleQuotes = true;
                        } else if (ic == ']') {
                            break; // end of scope
                        } else if (!(StmlScope.isLegalScopeNameChar(ic) || ic == '.') || ic == ' ') {
                            throw new StmlParseException("Invalid character " + ic + " in scope name", currentLine, currentColumn);
                        }
                    }
                    i++; currentColumn++;
                }
                String scopeContent = stml.substring(start + 1, i - 1).trim();
            }
            else
            {
                if (c == '\n') {
                    currentLine++;
                    currentColumn = 1;
                } else {
                    currentColumn++;
                }
                i++;
            }
        }
        parsedFlag = true;
    }

    public static StmlValue<?> parseObjectRecursive(String stml){
        if (stml == null || stml.isBlank()) return StmlNull.INSTANCE;
        stml = stml.trim();
        if (stml.equals("null") || stml.equals("NULL") || stml.equals("Null")) {
            return StmlNull.INSTANCE;
        } else if (stml.equals("true") || stml.equals("TRUE") || stml.equals("True")) {
            return StmlBoolean.TRUE;
        } else if (stml.equals("false") || stml.equals("FALSE") || stml.equals("False")) {
            return StmlBoolean.FALSE;
        } else if (stml.startsWith("\"") && stml.endsWith("\"")) {
            // Get the string literal
            String str = stml.substring(1, stml.length() - 1);
            // Unescape the string
            str = StmlString.unescape(str);
            return new StmlString(str);
        } else if (stml.startsWith("'") && stml.endsWith("'")) {
            // Get the string literal
            String str = stml.substring(1, stml.length() - 1);
            // Unescape the string
            str = StmlString.unescape(str);
            return new StmlString(str);
        } else if (stml.startsWith("[") && stml.endsWith("]")) {
            String[] elements = stml.substring(1, stml.length() - 1).split(",");
            StmlList list = new StmlList(elements.length);
            for (String element : elements) {
                StmlValue<?> obj = new StmlFuture(element);
                list.add(obj);
            }
            return list;
        } else if (stml.startsWith("{") && stml.endsWith("}")) {
            return null; // TODO: implement object parsing
        } else {
            // Parse as number or unquoted string
            try {
                return new StmlInteger(stml);
            } catch (NumberFormatException e) {
                try {
                    return new StmlFloat(stml);
                } catch (NumberFormatException ignored) {}
            }
            // This is a string without quotes
            return new StmlString(stml);
        }
    }

    public static void main(String[] args) throws IOException {
        StmlParser p = new StmlParser(new File("stml/example.stml"));
        p.parse();
    }
}
