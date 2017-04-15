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
package guru.nidi.codeassert.snippets;

import guru.nidi.codeassert.checkstyle.CheckstyleAnalyzer;
import guru.nidi.codeassert.checkstyle.CheckstyleResult;
import guru.nidi.codeassert.checkstyle.StyleChecks;
import guru.nidi.codeassert.checkstyle.StyleEventCollector;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.findbugs.BugCollector;
import guru.nidi.codeassert.findbugs.FindBugsAnalyzer;
import guru.nidi.codeassert.findbugs.FindBugsResult;
import guru.nidi.codeassert.junit.CodeAssertTest;
import guru.nidi.codeassert.model.ModelAnalyzer;
import guru.nidi.codeassert.model.ModelResult;
import guru.nidi.codeassert.pmd.PmdAnalyzer;
import guru.nidi.codeassert.pmd.PmdResult;
import guru.nidi.codeassert.pmd.PmdViolationCollector;
import org.junit.Ignore;
import org.junit.Test;

import static guru.nidi.codeassert.dependency.DependencyRules.denyAll;
import static guru.nidi.codeassert.junit.CodeAssertMatchers.packagesMatchExactly;
import static guru.nidi.codeassert.pmd.Rulesets.basic;
import static guru.nidi.codeassert.pmd.Rulesets.braces;
import static org.junit.Assert.assertThat;

@Ignore
//## codeTest
public class CodeTest extends CodeAssertTest {

    private static final AnalyzerConfig CONFIG = AnalyzerConfig.maven().main();

    @Test
    public void dependency() {
        class MyProject extends DependencyRuler {
            DependencyRule packages;

            @Override
            public void defineRules() {
                //TODO
            }
        }

        final DependencyRules rules = denyAll().withExternals("java*").withRelativeRules(new MyProject());
        assertThat(modelResult(), packagesMatchExactly(rules));
    }

    @Override
    protected ModelResult analyzeModel() {
        return new ModelAnalyzer(CONFIG).analyze();
    }

    @Override
    protected FindBugsResult analyzeFindBugs() {
        final BugCollector bugCollector = new BugCollector().just(
                In.loc("*Exception").ignore("SE_BAD_FIELD"));
        return new FindBugsAnalyzer(CONFIG, bugCollector).analyze();
    }

    @Override
    protected CheckstyleResult analyzeCheckstyle() {
        final StyleEventCollector bugCollector = new StyleEventCollector().just(
                In.everywhere().ignore("javadoc.missing"));
        return new CheckstyleAnalyzer(CONFIG, StyleChecks.google(), bugCollector).analyze();
    }

    @Override
    protected PmdResult analyzePmd() {
        final PmdViolationCollector collector = new PmdViolationCollector().just(
                In.everywhere().ignore("MethodArgumentCouldBeFinal"));
        return new PmdAnalyzer(CONFIG, collector).withRulesets(basic(), braces()).analyze();
    }
}
//##
