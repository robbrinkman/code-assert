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

import org.apache.commons.io.input.CountingInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ClassFileParser</code> class is responsible for
 * parsing a Java class file to create a <code>JavaClass</code>
 * instance.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
class ClassFileParser {
    private static final int JAVA_MAGIC = 0xCAFEBABE;

    private ConstantPool constantPool;
    private CountingInputStream counter;
    private DataInputStream in;

    public JavaClass parse(File file, Model model) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return parse(in, model);
        }
    }

    public JavaClass parse(InputStream is, Model model) throws IOException {
        counter = new CountingInputStream(is);
        in = new DataInputStream(counter);

        parseMagic();
        parseMinorVersion();
        parseMajorVersion();

        constantPool = ConstantPool.fromData(in);

        parseAccessFlags();

        final String className = parseClassName();
        final String superClassName = parseSuperClassName();
        final List<String> interfaceNames = parseInterfaces();
        final List<MemberInfo> fields = parseMembers();
        final List<MemberInfo> methods = parseMembers();
        final List<AttributeInfo> attributes = parseAttributes();

        final JavaClassImportBuilder adder = new JavaClassImportBuilder(className, model, constantPool);
        adder.addClassConstantReferences();
        adder.addSuperClass(superClassName);
        adder.addInterfaces(interfaceNames);
        adder.addFieldRefs(fields);
        adder.addMethodRefs(methods);
        adder.addAttributeRefs(attributes);

        handlePackageInfo(adder, model, className);
        setSizes(adder, methods);
        return adder.clazz;
    }

    private void handlePackageInfo(JavaClassImportBuilder adder, Model model, String className) {
        if (className.endsWith(".package-info")) {
            final JavaPackage pack = model.getOrCreatePackage(Model.packageOf(className));
            for (final JavaClass ann : adder.clazz.getAnnotations()) {
                pack.addAnnotation(ann);
            }
        }
    }

    private void setSizes(JavaClassImportBuilder adder, List<MemberInfo> methods) {
        int codeSize = 0;
        for (final MemberInfo method : methods) {
            codeSize += method.codeSize;
        }
        adder.clazz.codeSize = codeSize;
        adder.clazz.totalSize = this.counter.getCount();
    }

    private int parseMagic() throws IOException {
        final int magic = in.readInt();
        if (magic != JAVA_MAGIC) {
            throw new IOException("Invalid class file");
        }
        return magic;
    }

    private int parseMinorVersion() throws IOException {
        return in.readUnsignedShort();
    }

    private int parseMajorVersion() throws IOException {
        return in.readUnsignedShort();
    }

    private void parseAccessFlags() throws IOException {
        in.readUnsignedShort();
    }

    private String parseClassName() throws IOException {
        final int entryIndex = in.readUnsignedShort();
        return constantPool.getClassConstantName(entryIndex);
    }

    private String parseSuperClassName() throws IOException {
        final int entryIndex = in.readUnsignedShort();
        return constantPool.getClassConstantName(entryIndex);
    }

    private List<String> parseInterfaces() throws IOException {
        final int count = in.readUnsignedShort();
        final List<String> names = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int entryIndex = in.readUnsignedShort();
            names.add(constantPool.getClassConstantName(entryIndex));
        }
        return names;
    }

    private List<MemberInfo> parseMembers() throws IOException {
        final int count = in.readUnsignedShort();
        final List<MemberInfo> infos = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            infos.add(MemberInfo.fromData(in, constantPool));
        }
        return infos;
    }

    private List<AttributeInfo> parseAttributes() throws IOException {
        final int count = in.readUnsignedShort();
        final List<AttributeInfo> attributes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            attributes.add(AttributeInfo.fromData(in, constantPool));
        }
        return attributes;
    }

}
