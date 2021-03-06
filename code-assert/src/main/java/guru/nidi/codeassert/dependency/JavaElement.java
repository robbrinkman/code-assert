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
package guru.nidi.codeassert.dependency;

import guru.nidi.codeassert.config.LocationMatcher;
import guru.nidi.codeassert.model.UsingElement;
import guru.nidi.codeassert.model.UsingElementMatcher;

public class JavaElement implements UsingElementMatcher {
    final LocationMatcher pattern;
    final boolean allowAll;

    JavaElement(String pattern, boolean allowAll) {
        final int starPos = pattern.indexOf('*');
        if (starPos >= 0 && starPos != pattern.length() - 1) {
            throw new IllegalArgumentException("Wildcard * is only allowed at the end (e.g. java*)");
        }
        this.pattern = new LocationMatcher(pattern);
        this.allowAll = allowAll;
    }

    public DependencyRule allSub() {
        final String newPattern = pattern.toString() + (pattern.toString().endsWith(".") ? "*" : ".*");
        return DependencyRules.addRuleToCurrent(new DependencyRule(newPattern, allowAll));
    }

    public DependencyRule sub(String name) {
        final String newPattern = pattern.getPattern() + "." + name;
        return DependencyRules.addRuleToCurrent(new DependencyRule(newPattern, allowAll));
    }

    public DependencyRule rule() {
        final String newPattern = pattern.getPattern();
        return DependencyRules.addRuleToCurrent(new DependencyRule(newPattern, allowAll));
    }

    public boolean matches(UsingElement<?> elem) {
        return elem.isMatchedBy(pattern);
    }

    @Override
    public String toString() {
        return "The java element " + pattern;
    }
}
