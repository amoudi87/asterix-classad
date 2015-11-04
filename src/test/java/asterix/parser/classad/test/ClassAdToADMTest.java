/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package asterix.parser.classad.test;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import asterix.parser.classad.test.ClassAdToADMTest;
import org.apache.asterix.om.io.AsterixInputStreamReader;
import org.apache.asterix.om.io.AsterixSemiStructuredRecordReader;
import org.apache.asterix.om.io.BaseAsterixInputStream;
import org.apache.asterix.om.io.IAsterixRecord;
import asterix.parser.classad.CaseInsensitiveString;
import asterix.parser.classad.CharArrayLexerSource;
import asterix.parser.classad.ClassAd;
import asterix.parser.classad.ClassAdParser;
import asterix.parser.classad.ExprTree;
import asterix.parser.classad.Value;

public class ClassAdToADMTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public ClassAdToADMTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ClassAdToADMTest.class);
    }

    /**
     * 
     */
    public void testApp() {
        try {
            // test here
            ClassAd pAd = new ClassAd();
            String[] files = new String[] { "/jobads.txt" };
            ClassAdParser parser = new ClassAdParser();
            CharArrayLexerSource lexerSource = new CharArrayLexerSource();
            for (String path : files) {
                AsterixSemiStructuredRecordReader recordReader = new AsterixSemiStructuredRecordReader('[', ']');
                recordReader.init(null);
                recordReader.setInputStreamReader(new AsterixInputStreamReader(BaseAsterixInputStream
                        .createBaseInputStream(new FileInputStream(getClass().getResource(path).getPath()))));
                IAsterixRecord record = null;
                //infile = Files.newBufferedReader(Paths.get(getClass().getResource(path).getPath()));
                Value val = new Value();
                int i = 0;
                while (recordReader.hasNext()) {
                    i++;
                    System.out.print("{ ");
                    val.clear();
                    record = recordReader.next();
                    lexerSource.setNewSource(record.getRecordCharBuffer());
                    parser.setLexerSource(lexerSource);
                    parser.parseNext(pAd);
                    //System.out.println(pAd);
                    Map<CaseInsensitiveString, ExprTree> attrs = pAd.getAttrList();
                    boolean notFirst = false;
                    for (Entry<CaseInsensitiveString, ExprTree> entry : attrs.entrySet()) {
                        CaseInsensitiveString name = entry.getKey();
                        ExprTree tree = entry.getValue();
                        if (notFirst) {
                            System.out.print(", ");
                        }
                        notFirst = true;
                        switch (tree.getKind()) {
                            case ATTRREF_NODE:
                            case CLASSAD_NODE:
                            case EXPR_ENVELOPE:
                            case EXPR_LIST_NODE:
                            case FN_CALL_NODE:
                            case OP_NODE:
                                if (pAd.evaluateAttr(name.get(), val)) {
                                    System.out.print("\"" + name + "Expr\":" + "\"expr=" + tree + "\"");
                                    System.out.print(", \"" + name + "\":" + val);
                                } else {
                                    System.out.print("\"" + name + "\":" + tree);
                                }
                                break;
                            case LITERAL_NODE:
                                // No need to do anything
                                System.out.print("\"" + name + "\":" + tree);
                                break;
                            default:
                                System.out.println("Something is wrong");
                                break;
                        }
                    }
                    System.out.println(" }");
                }
                System.out.println(i + " number of records found");
                recordReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}