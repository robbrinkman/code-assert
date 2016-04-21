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
package guru.nidi.codeassert.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import guru.nidi.codeassert.Analyzer;
import guru.nidi.codeassert.AnalyzerResult;

import java.util.List;

/**
 *
 */
public class FindBugsResult extends AnalyzerResult<List<BugInstance>> {
    public FindBugsResult(Analyzer<List<BugInstance>> analyzer, List<BugInstance> findings, List<String> unusedActions) {
        super(analyzer, findings, unusedActions);
    }
}