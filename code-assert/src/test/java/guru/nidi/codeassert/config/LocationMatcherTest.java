/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
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
 */
package guru.nidi.codeassert.config;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationMatcherTest {
    @Test
    void emptyPattern() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher(""));
    }

    @Test
    void nullPattern() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher(null));
    }

    @Test
    void wildcardInMiddle() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("a*b"));
    }

    @Test
    void doubleWildcard() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("**"));
    }

    @Test
    void wildcardBetweenPackageAndClass() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("a*B"));
    }

    @Test
    void illegalWildcardInPackage3() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("*a*b"));
    }

    @Test
    void illegalWildcardInClass() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("A*b"));
    }

    @Test
    void illegalWildcardInMethod() {
        assertThrows(IllegalArgumentException.class, () -> new LocationMatcher("#a*b"));
    }

    @Test
    void all() {
        final LocationMatcher m = new LocationMatcher("*");
        assertTrue(m.matchesPackage("pa"));
        assertTrue(m.matchesPackageClass("pa", "Cl"));
        assertTrue(m.matches("pa", "Cl", "me"));
    }

    @Test
    void methodOnly() {
        final LocationMatcher m = new LocationMatcher("#me");
        assertFalse(m.matchesPackage("pa"));
        assertFalse(m.matchesPackageClass("pa", "Cl"));
        assertTrue(m.matches("pa", "Cl", "me"));
        assertFalse(m.matches("pa", "Cl", "mu"));
    }

    @Test
    void classOnlyUppercase() {
        final LocationMatcher m = new LocationMatcher("Cl");
        assertFalse(m.matchesPackage("pa"));
        assertTrue(m.matchesPackageClass("pa", "Cl"));
        assertTrue(m.matches("pa", "Cl", "me"));
        assertFalse(m.matchesPackageClass("pa", "Co"));
    }

    @Test
    void classOnlySlash() {
        final LocationMatcher m = new LocationMatcher("/Cl");
        assertFalse(m.matchesPackage("pa"));
        assertTrue(m.matchesPackageClass("pa", "Cl"));
        assertTrue(m.matches("pa", "Cl", "me"));
        assertFalse(m.matchesPackageClass("pa", "Co"));
    }

    @Test
    void classOnlyInner() {
        final LocationMatcher m = new LocationMatcher("Cl");
        assertTrue(m.matchesClass("Cl$bla"));
        assertTrue(m.matchesPackageClass("pa", "Cl$blu"));
        assertTrue(m.matches("pa", "Cl$blo", ""));
    }

    @Test
    void classMethodInner() {
        final LocationMatcher m = new LocationMatcher("Cl#me");
        assertFalse(m.matches("pa", "Cl$bla", "me"));
    }

    @Test
    void packageOnly() {
        final LocationMatcher m = new LocationMatcher("pa");
        assertTrue(m.matchesPackage("pa"));
        assertTrue(m.matchesPackageClass("pa", "Cl"));
        assertTrue(m.matches("pa", "Cl", "me"));
        assertFalse(m.matchesPackage("po"));
    }

    @Test
    void startWildcard() {
        final LocationMatcher m = new LocationMatcher("*pa");
        assertTrue(m.matchesPackage("pa"));
        assertTrue(m.matchesPackage("xxxpa"));
        assertFalse(m.matchesPackage("paxxx"));
    }

    @Test
    void endWildcard() {
        final LocationMatcher m = new LocationMatcher("pa*");
        assertTrue(m.matchesPackage("pa"));
        assertFalse(m.matchesPackage("xxxpa"));
        assertTrue(m.matchesPackage("paxxx"));
    }

    @Test
    void bothWildcard() {
        final LocationMatcher m = new LocationMatcher("*pa*");
        assertTrue(m.matchesPackage("pa"));
        assertTrue(m.matchesPackage("xxxpa"));
        assertTrue(m.matchesPackage("paxxx"));
        assertFalse(m.matchesPackage(""));
    }

    @Test
    void onlyWildcard() {
        final LocationMatcher m = new LocationMatcher("*");
        assertTrue(m.matchesPackage("pa"));
        assertTrue(m.matchesPackage(""));
    }

    @Test
    void packageAndClass() {
        final LocationMatcher m = new LocationMatcher("a.B");
        assertTrue(m.matchesPackageClass("a", "B"));
        assertFalse(m.matchesPackage("a"));
        assertFalse(m.matchesPackageClass("a", "C"));
    }

    @Test
    void wildcardPackageAndClass2() {
        final LocationMatcher m = new LocationMatcher("a*.B");
        assertTrue(m.matchesPackageClass("a", "B"));
        assertTrue(m.matchesPackageClass("ab", "B"));
        assertTrue(m.matchesPackageClass("a.b", "B"));
        assertFalse(m.matchesPackageClass("b", "B"));
        assertFalse(m.matchesPackageClass("a", "C"));
    }

    @Test
    void packageAndWildcardClass() {
        final LocationMatcher m = new LocationMatcher("a.*B");
        assertTrue(m.matchesPackageClass("a", "B"));
        assertTrue(m.matchesPackageClass("a", "xxxB"));
        assertFalse(m.matchesPackage("a"));
        assertFalse(m.matchesPackageClass("a", "C"));
    }

    @Test
    void wildcardPackageAndWildcardClass() {
        final LocationMatcher m = new LocationMatcher("a*.*B");
        assertTrue(m.matchesPackageClass("a", "B"));
        assertTrue(m.matchesPackageClass("ab", "B"));
        assertTrue(m.matchesPackageClass("a.b", "B"));
        assertTrue(m.matchesPackageClass("a", "xxxB"));
        assertFalse(m.matchesPackage("a"));
        assertFalse(m.matchesPackageClass("b", "B"));
        assertFalse(m.matchesPackageClass("a", "C"));
    }

}
