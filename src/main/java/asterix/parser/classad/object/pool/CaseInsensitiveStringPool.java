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
package asterix.parser.classad.object.pool;

import java.util.Stack;

import asterix.parser.classad.object.pool.Pool;
import asterix.parser.classad.CaseInsensitiveString;

public class CaseInsensitiveStringPool extends Pool<CaseInsensitiveString> {

    protected Stack<CaseInsensitiveString> stock = new Stack<CaseInsensitiveString>();
    public CaseInsensitiveString get() {
        if (!stock.isEmpty()) {
            return stock.pop();
        } else {
            return newInstance();

        }
    }

    public void reset() {
    }

    public void put(CaseInsensitiveString aString) {
        stock.push(aString);
    }

    @Override
    public CaseInsensitiveString newInstance() {
        return new CaseInsensitiveString();
    }

    @Override
    protected void reset(CaseInsensitiveString obj) {
    }
}
