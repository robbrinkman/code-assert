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
package guru.nidi.codeassert.model;

import guru.nidi.codeassert.AnalyzerException;

import java.io.*;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class Model {
    public static final String UNNAMED_PACKAGE = "<Unnamed Package>";

    final Map<String, JavaPackage> packages = new HashMap<>();
    final Map<String, JavaClass> classes = new HashMap<>();

    public static Model from(File... files) {
        return from(Arrays.asList(files));
    }

    public static Model from(List<File> files) {
        try {
            final Model model = new Model();
            final ClassFileParser parser = new ClassFileParser();
            for (final File file : files) {
                try (final InputStream in = new FileInputStream(file)) {
                    add(parser, model, file.getName(), in);
                }
            }
            return model;
        } catch (IOException e) {
            throw new AnalyzerException("Problem creating a Model", e);
        }
    }

    private static void add(ClassFileParser parser, Model model, String name, InputStream in) throws IOException {
        if (name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".war") || name.endsWith(".ear")) {
            final JarInputStream jar = new JarInputStream(in);
            ZipEntry entry;
            while ((entry = jar.getNextEntry()) != null) {
                try {
                    if (!entry.isDirectory()) {
                        add(parser, model, entry.getName(), jar);
                    }
                } finally {
                    jar.closeEntry();
                }
            }
        } else if (name.endsWith(".class")) {
            parser.parse(in, model);
        }
    }

    JavaPackage getOrCreatePackage(String name) {
        JavaPackage pack = packages.get(name);
        if (pack == null) {
            pack = new JavaPackage(name);
            packages.put(name, pack);
        }
        return pack;
    }

    JavaClass getOrCreateClass(String name) {
        JavaClass clazz = classes.get(name);
        if (clazz == null) {
            final JavaPackage pack = getOrCreatePackage(packageOf(name));
            clazz = new JavaClass(name, pack);
            classes.put(name, clazz);
            pack.addClass(clazz);
        }
        return clazz;
    }

    static String packageOf(String type) {
        final int pos = type.lastIndexOf('.');
        return pos < 0 ? UNNAMED_PACKAGE : type.substring(0, pos);
    }

    public Collection<JavaPackage> getPackages() {
        return packages.values();
    }

    public Collection<JavaClass> getClasses() {
        return classes.values();
    }

}
