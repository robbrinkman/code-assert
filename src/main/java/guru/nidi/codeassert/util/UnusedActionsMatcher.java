/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.codeassert.util;

import guru.nidi.codeassert.AnalyzerResult;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class UnusedActionsMatcher<T extends AnalyzerResult<?>> extends TypeSafeMatcher<T> {
    @Override
    protected boolean matchesSafely(T item) {
        return item.unusedActions().isEmpty();
    }

    public void describeTo(Description description) {
        description.appendText("Has no unused actions");
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        for (final String action : item.unusedActions()) {
            description.appendText("\n").appendText(action);
        }
    }
}