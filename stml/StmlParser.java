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

import java.io.File;

public class StmlParser {
    /**
     * Create a new StmlParser instance to parse the given file.
     * @param file The file to parse.
     */
    public StmlParser(File file) {
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
}
