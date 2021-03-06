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
package asterix.parser.classad;

import org.apache.commons.lang3.mutable.MutableBoolean;
import asterix.parser.classad.AMutableCharArrayString;
import asterix.parser.classad.ClassAd;
import asterix.parser.classad.ClassAdTime;
import asterix.parser.classad.ClassAdUnParser;
import asterix.parser.classad.ExprList;
import asterix.parser.classad.PrettyPrint;
import asterix.parser.classad.Util;
import asterix.parser.classad.Value;
import org.apache.asterix.om.base.AMutableDouble;
import org.apache.asterix.om.base.AMutableInt32;
import org.apache.asterix.om.base.AMutableInt64;
import org.apache.hyracks.api.exceptions.HyracksDataException;

public class Value {

    private ValueType valueType;
    private long longVal;
    private double doubleVal;
    private boolean boolVal;
    private ClassAdTime timeVal = new ClassAdTime();
    private ClassAd classadVal = new ClassAd();
    private ExprList listVal = new ExprList();
    private String stringVal;

    /// Value types
    public enum ValueType {
        NULL_VALUE,
        /** The error value */
        ERROR_VALUE,
        /** The undefined value */
        UNDEFINED_VALUE,
        /** A boolean value (false, true) */
        BOOLEAN_VALUE,
        /** An integer value */
        INTEGER_VALUE,
        /** A real value */
        REAL_VALUE,
        /** A relative time value */
        RELATIVE_TIME_VALUE,
        /** An absolute time value */
        ABSOLUTE_TIME_VALUE,
        /** A string value */
        STRING_VALUE,
        /** A classad value */
        CLASSAD_VALUE,
        /** A list value (not owned here) */
        LIST_VALUE,
        /** A list value (owned via shared_ptr) */
        SLIST_VALUE
    };

    /// Number factors
    public enum NumberFactor {
        /** No factor specified */
        NO_FACTOR,
        /** Byte factor */
        B_FACTOR,
        /** Kilo factor */
        K_FACTOR,
        /** Mega factor */
        M_FACTOR,
        /** Giga factor */
        G_FACTOR,
        /** Terra factor */
        T_FACTOR
    };

    public ValueType getType() {
        return valueType;
    }

    public boolean isBooleanValue(MutableBoolean b) {
        if (valueType == ValueType.BOOLEAN_VALUE) {
            b.setValue(boolVal);
            return true;
        }
        return false;
    }

    public boolean isBooleanValue() {
        return (valueType == ValueType.BOOLEAN_VALUE);
    }

    public boolean isIntegerValue(AMutableInt64 i) {
        if (valueType == ValueType.INTEGER_VALUE) {
            i.setValue(longVal);
            return true;
        }
        return false;
    }

    public boolean isIntegerValue() {
        return (valueType == ValueType.INTEGER_VALUE);
    }

    public boolean isRealValue(AMutableDouble r) {

        if (valueType == ValueType.REAL_VALUE) {
            r.setValue(doubleVal);
            return true;
        }
        return false;
    }

    public boolean isRealValue() {
        return (valueType == ValueType.REAL_VALUE);
    }

    public boolean isListValue(ExprList el) {
        if (valueType == ValueType.LIST_VALUE || valueType == ValueType.SLIST_VALUE) {
            el.getExprList().addAll(listVal.getExprList());
            el.isShared = listVal.isShared;
            return true;
        } else {
            return false;
        }
    }

    public boolean isListValue() {
        return (valueType == ValueType.LIST_VALUE || valueType == ValueType.SLIST_VALUE);
    }

    public boolean isStringValue() {
        return (valueType == ValueType.STRING_VALUE);
    }

    public boolean isStringValue(AMutableCharArrayString s) {
        if (valueType == ValueType.STRING_VALUE) {
            s.setValue(stringVal);
            return true;
        } else {
            return false;
        }
    }

    public boolean isStringValue(AMutableCharArrayString s, int len) {
        if (valueType == ValueType.STRING_VALUE) {
            s.setValue(stringVal, len);
            return (true);
        }
        return (false);
    }

    public boolean isStringValue(AMutableInt32 size) {
        if (valueType == ValueType.STRING_VALUE) {
            size.setValue(stringVal.length());
            return true;
        } else {
            size.setValue(-1);
            return false;
        }
    }

    public boolean isClassAdValue(ClassAd ad) {
        if (valueType == ValueType.CLASSAD_VALUE) {
            ad.setValue(classadVal);
            return true;
        } else {
            return false;
        }
    }

    public boolean isClassAdValue() {
        return (valueType == ValueType.CLASSAD_VALUE);
    }

    public boolean isUndefinedValue() {
        return (valueType == ValueType.UNDEFINED_VALUE);
    }

    public boolean isErrorValue() {
        return (valueType == ValueType.ERROR_VALUE);
    }

    public boolean isExceptional() {
        return (valueType == ValueType.UNDEFINED_VALUE || valueType == ValueType.ERROR_VALUE);
    }

    public boolean isAbsoluteTimeValue() {
        return (valueType == ValueType.ABSOLUTE_TIME_VALUE);
    }

    public boolean isAbsoluteTimeValue(ClassAdTime secs) {
        if (valueType == ValueType.ABSOLUTE_TIME_VALUE) {
            secs.setValue(timeVal);
            return true;
        } else {
            return false;
        }
    }

    public boolean isRelativeTimeValue() {
        return (valueType == ValueType.RELATIVE_TIME_VALUE);
    }

    public boolean isRelativeTimeValue(ClassAdTime secs) {

        if (valueType == ValueType.RELATIVE_TIME_VALUE) {
            secs.setValue(timeVal);
            return true;
        }
        return false;
    }

    public boolean isNumber() {
        return (valueType == ValueType.INTEGER_VALUE || valueType == ValueType.REAL_VALUE);
    }

    public boolean isNumber(AMutableInt64 i) {
        switch (valueType) {
            case INTEGER_VALUE:
                i.setValue(longVal);
                return true;

            case REAL_VALUE:
                i.setValue((long) doubleVal);
                return true;

            case BOOLEAN_VALUE:
                i.setValue(boolVal ? 1L : 0L);
                return true;
            default:
                return false;
        }
    }

    public boolean isNumber(AMutableDouble r) {
        switch (valueType) {
            case INTEGER_VALUE:
                r.setValue((double) longVal);
                return true;

            case REAL_VALUE:
                r.setValue(doubleVal);
                return true;

            case BOOLEAN_VALUE:
                r.setValue(boolVal ? 1.0 : 0.0);
                return true;

            default:
                return false;
        }
    }

    // Implementation
    public static final double[] ScaleFactor = { 1.0, // none
            1.0, // B
            1024.0, // Kilo
            1024.0 * 1024.0, // Mega
            1024.0 * 1024.0 * 1024.0, // Giga
            1024.0 * 1024.0 * 1024.0 * 1024.0 // Terra
    };

    public Value() {
        valueType = ValueType.UNDEFINED_VALUE;
    }

    public Value(Value value) throws HyracksDataException {
        valueType = value.valueType;
        switch (value.valueType) {
            case ABSOLUTE_TIME_VALUE:
                timeVal = new ClassAdTime(value.timeVal);
                break;
            case BOOLEAN_VALUE:
                this.boolVal = value.boolVal;
                break;
            case CLASSAD_VALUE:
                this.classadVal = new ClassAd(value.classadVal);
                break;
            case ERROR_VALUE:
                break;
            case INTEGER_VALUE:
                this.longVal = value.longVal;
                break;
            case LIST_VALUE:
                this.listVal = new ExprList(value.listVal);
                break;
            case NULL_VALUE:
                break;
            case REAL_VALUE:
                this.doubleVal = value.doubleVal;
                break;
            case RELATIVE_TIME_VALUE:
                this.timeVal = new ClassAdTime(value.timeVal);
                break;
            case SLIST_VALUE:
                this.listVal = new ExprList(value.listVal);
                break;
            case STRING_VALUE:
                this.stringVal = value.stringVal;
                break;
            case UNDEFINED_VALUE:
                break;
            default:
                break;
        }
    }

    public void setValue(Value value) throws HyracksDataException {
        valueType = value.valueType;
        switch (value.valueType) {
            case ABSOLUTE_TIME_VALUE:
                ((ClassAdTime) this.timeVal).setValue((ClassAdTime) value.timeVal);
                break;
            case BOOLEAN_VALUE:
                this.boolVal = value.boolVal;
                break;
            case CLASSAD_VALUE:
                ((ClassAd) this.classadVal).setValue((ClassAd) value.classadVal);
                break;
            case ERROR_VALUE:
                break;
            case INTEGER_VALUE:
                this.longVal = value.longVal;
                break;
            case LIST_VALUE:
                ((ExprList) this.listVal).setValue((ExprList) value.listVal);
                break;
            case NULL_VALUE:
                break;
            case REAL_VALUE:
                this.doubleVal = value.doubleVal;
                break;
            case RELATIVE_TIME_VALUE:
                this.timeVal.setValue((value.timeVal));
                break;
            case SLIST_VALUE:
                listVal.setValue((ExprList) value.listVal);
                break;
            case STRING_VALUE:
                stringVal = value.stringVal;
                break;
            case UNDEFINED_VALUE:
                break;
            default:
                break;
        }
    }

    public void assign(Value value) throws HyracksDataException {
        if (this != value) {
            setValue(value);
        }
    }

    public void clear() {
        valueType = ValueType.UNDEFINED_VALUE;
    }

    public void setRealValue(double r) {
        valueType = ValueType.REAL_VALUE;
        doubleVal = r;
    }

    public void setRealValue(AMutableDouble r) {
        valueType = ValueType.REAL_VALUE;
        doubleVal = r.getDoubleValue();
    }

    public void setBooleanValue(boolean b) {
        valueType = ValueType.BOOLEAN_VALUE;
        boolVal = b;
    }

    public void setBooleanValue(MutableBoolean b) {
        valueType = ValueType.BOOLEAN_VALUE;
        boolVal = b.booleanValue();
    }

    public void setIntegerValue(long i) {
        valueType = ValueType.INTEGER_VALUE;
        longVal = i;
    }

    public void setUndefinedValue() {
        valueType = ValueType.UNDEFINED_VALUE;
    }

    public void setErrorValue() {
        valueType = ValueType.ERROR_VALUE;
    }

    public void setStringValue(AMutableCharArrayString s) {
        valueType = ValueType.STRING_VALUE;
        stringVal = s.toString();
    }

    public void setStringValue(String s) {
        valueType = ValueType.STRING_VALUE;
        stringVal = s;
    }

    public void setListValue(ExprList expList) throws HyracksDataException {
        valueType = expList.isShared ? ValueType.SLIST_VALUE : ValueType.LIST_VALUE;
        listVal.setValue(expList);
    }

    public void setClassAdValue(ClassAd ad) {
        clear();
        valueType = ValueType.CLASSAD_VALUE;
        ((ClassAd) classadVal).setValue(ad);
    }

    public void setRelativeTimeValue(ClassAdTime rsecs) {
        clear();
        valueType = ValueType.RELATIVE_TIME_VALUE;
        ((ClassAdTime) timeVal).setValue(rsecs);
    }

    public void setRelativeTimeValue(long rsecs) {
        clear();
        valueType = ValueType.RELATIVE_TIME_VALUE;
        ((ClassAdTime) timeVal).setValue(rsecs);
        timeVal.isAbsolute(false);
    }

    public void setAbsoluteTimeValue(ClassAdTime tim) {
        clear();
        valueType = ValueType.ABSOLUTE_TIME_VALUE;
        ((ClassAdTime) timeVal).setValue(tim);
    }

    public boolean sameAs(Value otherValue) {
        boolean is_same = false;
        if (valueType != otherValue.valueType) {
            is_same = false;
        } else {
            switch (valueType) {
                case NULL_VALUE:
                case ERROR_VALUE:
                case UNDEFINED_VALUE:
                    is_same = true;
                    break;
                case BOOLEAN_VALUE:
                    is_same = (boolVal == otherValue.boolVal);
                    break;
                case INTEGER_VALUE:
                    is_same = (longVal == otherValue.longVal);
                    break;
                case REAL_VALUE:
                    is_same = (doubleVal == otherValue.doubleVal);
                    break;
                case LIST_VALUE:
                case SLIST_VALUE:
                    is_same = ((ExprList) listVal).equals(otherValue.listVal);
                    break;
                case CLASSAD_VALUE:
                    is_same = ((ClassAd) classadVal).equals(otherValue.classadVal);
                    break;
                case RELATIVE_TIME_VALUE:
                case ABSOLUTE_TIME_VALUE:
                    is_same = ((ClassAdTime) timeVal).equals(otherValue.timeVal);
                    break;
                case STRING_VALUE:
                    is_same = stringVal.equals(otherValue.stringVal);
                    break;
            }
        }
        return is_same;
    }

    public boolean equals(Value value) {
        return sameAs(value);
    }

    public boolean isBooleanValueEquiv(MutableBoolean b) {
        return isBooleanValue(b);
    }

    public String toString() {
        ClassAdUnParser unparser = new PrettyPrint();
        AMutableCharArrayString unparsed_text = new AMutableCharArrayString();
        switch (valueType) {
            case ABSOLUTE_TIME_VALUE:
            case CLASSAD_VALUE:
            case RELATIVE_TIME_VALUE:
            case SLIST_VALUE:
            case LIST_VALUE:
                try {
                    unparser.unparse(unparsed_text, this);
                } catch (HyracksDataException e) {
                    e.printStackTrace();
                }
                return unparsed_text.toString();
            case BOOLEAN_VALUE:
                if (boolVal) {
                    return "true";
                } else {
                    return "false";
                }
            case ERROR_VALUE:
                return "error";
            case INTEGER_VALUE:
                return String.valueOf(longVal);
            case NULL_VALUE:
                return "(null)";
            case REAL_VALUE:
                return String.valueOf(doubleVal);
            case STRING_VALUE:
                return stringVal;
            case UNDEFINED_VALUE:
                return "undefined";
            default:
                break;
        }
        return null;
    }

    public static boolean convertValueToRealValue(Value value, Value realValue) throws HyracksDataException {
        boolean could_convert;
        AMutableCharArrayString buf = new AMutableCharArrayString();
        int endIndex;
        char end;
        AMutableInt64 ivalue = new AMutableInt64(0);
        ClassAdTime atvalue = new ClassAdTime();
        MutableBoolean bvalue = new MutableBoolean();
        double rvalue;
        NumberFactor nf = NumberFactor.NO_FACTOR;

        switch (value.getType()) {
            case UNDEFINED_VALUE:
                realValue.setUndefinedValue();
                could_convert = false;
                break;

            case ERROR_VALUE:
            case CLASSAD_VALUE:
            case LIST_VALUE:
            case SLIST_VALUE:
                realValue.setErrorValue();
                could_convert = false;
                break;

            case STRING_VALUE:
                could_convert = true;
                value.isStringValue(buf);
                endIndex = buf.fistNonDoubleDigitChar();
                if (endIndex < 0) {
                    // no non digit
                    String buffString = buf.toString();
                    if (buffString.contains("INF")) {
                        buffString = buffString.replace("INF", "Infinity");
                    }
                    rvalue = Double.parseDouble(buffString);
                    nf = NumberFactor.NO_FACTOR;
                } else {
                    rvalue = Double.parseDouble(buf.substr(0, endIndex));
                    end = buf.charAt(endIndex);
                    switch (Character.toUpperCase(end)) {
                        case 'B':
                            nf = NumberFactor.B_FACTOR;
                            break;
                        case 'K':
                            nf = NumberFactor.K_FACTOR;
                            break;
                        case 'M':
                            nf = NumberFactor.M_FACTOR;
                            break;
                        case 'G':
                            nf = NumberFactor.G_FACTOR;
                            break;
                        case 'T':
                            nf = NumberFactor.T_FACTOR;
                            break;
                        case '\0':
                            nf = NumberFactor.NO_FACTOR;
                            break;
                        default:
                            nf = NumberFactor.NO_FACTOR;
                            break;
                    }
                }

                if (could_convert) {
                    realValue.setRealValue(rvalue * Value.ScaleFactor[nf.ordinal()]);
                }
                break;

            case BOOLEAN_VALUE:
                value.isBooleanValue(bvalue);
                realValue.setRealValue(bvalue.booleanValue() ? 1.0 : 0.0);
                could_convert = true;
                break;

            case INTEGER_VALUE:
                value.isIntegerValue(ivalue);
                realValue.setRealValue((double) ivalue.getLongValue());
                could_convert = true;
                break;

            case REAL_VALUE:
                realValue.copyFrom(value);
                could_convert = true;
                break;

            case ABSOLUTE_TIME_VALUE:
                value.isAbsoluteTimeValue(atvalue);
                realValue.setRealValue(atvalue.getTimeInMillis() / 1000.0);
                could_convert = true;
                break;

            case RELATIVE_TIME_VALUE:
                value.isRelativeTimeValue(atvalue);
                realValue.setRealValue(atvalue.getRelativeTime() / 1000.0);
                could_convert = true;
                break;

            default:
                could_convert = false; // Make gcc's -Wuninitalized happy
                throw new HyracksDataException("Should not reach here");
        }
        return could_convert;
    }

    public static boolean convertValueToIntegerValue(Value value, Value integerValue) throws HyracksDataException {
        boolean could_convert;
        AMutableCharArrayString buf = new AMutableCharArrayString();
        char end;
        AMutableInt64 ivalue = new AMutableInt64(0);
        AMutableDouble rtvalue = new AMutableDouble(0);
        ClassAdTime atvalue = new ClassAdTime();
        MutableBoolean bvalue = new MutableBoolean();
        NumberFactor nf;

        switch (value.getType()) {
            case UNDEFINED_VALUE:
                integerValue.setUndefinedValue();
                could_convert = false;
                break;

            case ERROR_VALUE:
            case CLASSAD_VALUE:
            case LIST_VALUE:
            case SLIST_VALUE:
                integerValue.setErrorValue();
                could_convert = false;
                break;

            case STRING_VALUE:
                could_convert = true;
                value.isStringValue(buf);
                int endIndex = buf.fistNonDigitChar();
                if (endIndex < 0) {
                    // no non digit
                    ivalue.setValue(Long.parseLong(buf.toString()));
                    nf = NumberFactor.NO_FACTOR;
                    break;
                } else {
                    ivalue.setValue(Long.parseLong(buf.substr(0, endIndex)));
                    end = buf.charAt(endIndex);
                    switch (Character.toUpperCase(end)) {
                        case 'B':
                            nf = NumberFactor.B_FACTOR;
                            break;
                        case 'K':
                            nf = NumberFactor.K_FACTOR;
                            break;
                        case 'M':
                            nf = NumberFactor.M_FACTOR;
                            break;
                        case 'G':
                            nf = NumberFactor.G_FACTOR;
                            break;
                        case 'T':
                            nf = NumberFactor.T_FACTOR;
                            break;
                        case '\0':
                            nf = NumberFactor.NO_FACTOR;
                            break;
                        default:
                            nf = NumberFactor.NO_FACTOR;
                            break;
                    }
                    if (could_convert) {
                        integerValue
                                .setIntegerValue((long) ((ivalue.getLongValue() * Value.ScaleFactor[nf.ordinal()])));
                    }
                }
                break;

            case BOOLEAN_VALUE:
                value.isBooleanValue(bvalue);
                integerValue.setIntegerValue(bvalue.booleanValue() ? 1 : 0);
                could_convert = true;
                break;

            case INTEGER_VALUE:
                integerValue.copyFrom(value);
                could_convert = true;
                break;

            case REAL_VALUE:
                value.isRealValue(rtvalue);
                integerValue.setIntegerValue((long) rtvalue.getDoubleValue());
                could_convert = true;
                break;

            case ABSOLUTE_TIME_VALUE:
                value.isAbsoluteTimeValue(atvalue);
                integerValue.setIntegerValue(atvalue.getTimeInMillis() / 1000L);
                could_convert = true;
                break;
            case RELATIVE_TIME_VALUE:

                value.isRelativeTimeValue(atvalue);
                integerValue.setIntegerValue((atvalue.getTime() / 1000L));
                could_convert = true;
                break;

            default:
                could_convert = false; // Make gcc's -Wuninitalized happy
                throw new HyracksDataException("Should not reach here");
        }
        return could_convert;
    }

    public void copyFrom(Value val) throws HyracksDataException {
        clear();
        valueType = val.valueType;
        switch (val.valueType) {
            case STRING_VALUE:
                stringVal = val.stringVal;
                return;

            case BOOLEAN_VALUE:
                boolVal = val.boolVal;
                return;

            case INTEGER_VALUE:
                longVal = val.longVal;
                return;

            case REAL_VALUE:

                doubleVal = val.doubleVal;
                return;
            case UNDEFINED_VALUE:
            case ERROR_VALUE:
                return;
            case LIST_VALUE:
            case SLIST_VALUE:
                listVal.copyFrom(val.listVal);
                return;
            case CLASSAD_VALUE:
                ((ClassAd) classadVal).copyFrom(val.classadVal);
                return;

            case RELATIVE_TIME_VALUE:
            case ABSOLUTE_TIME_VALUE:
                timeVal.setValue(val.timeVal);
                return;
            default:
                setUndefinedValue();
        }
    }

    public static boolean convertValueToStringValue(Value value, Value stringValue) throws HyracksDataException {
        boolean could_convert = false;
        ClassAdTime atvalue = new ClassAdTime();
        AMutableCharArrayString string_representation = new AMutableCharArrayString();
        ClassAdUnParser unparser = new PrettyPrint();

        switch (value.getType()) {
            case UNDEFINED_VALUE:
                stringValue.setUndefinedValue();
                could_convert = false;
                break;

            case ERROR_VALUE:
                stringValue.setErrorValue();
                could_convert = false;
                break;

            case STRING_VALUE:
                stringValue.copyFrom(value);
                could_convert = true;
                break;

            case CLASSAD_VALUE:
            case LIST_VALUE:
            case SLIST_VALUE:
            case BOOLEAN_VALUE:
            case INTEGER_VALUE:
            case REAL_VALUE:
                unparser.unparse(string_representation, value);
                stringValue.setStringValue(string_representation);
                could_convert = true;
                break;

            case ABSOLUTE_TIME_VALUE:
                value.isAbsoluteTimeValue(atvalue);
                Util.absTimeToString(atvalue, string_representation);
                stringValue.setStringValue(string_representation);
                could_convert = true;
                break;

            case RELATIVE_TIME_VALUE:
                value.isRelativeTimeValue(atvalue);
                Util.relTimeToString(atvalue.getTimeInMillis(), string_representation);
                stringValue.setStringValue(string_representation);
                could_convert = true;
                break;

            default:
                could_convert = false; // Make gcc's -Wuninitalized happy
                throw new HyracksDataException("Should not reach here");
        }
        return could_convert;
    }

    public boolean isSListValue(ExprList l) throws HyracksDataException {
        if (valueType == ValueType.SLIST_VALUE || valueType == ValueType.LIST_VALUE) {
            l.setValue((ExprList) listVal);
            return true;
        } else {
            return false;
        }
    }

    public ValueType getValueType() {
        return valueType;
    }

    public long getLongVal() {
        return longVal;
    }

    public double getDoubleVal() {
        return doubleVal;
    }

    public boolean getBoolVal() {
        return boolVal;
    }

    public ClassAdTime getTimeVal() {
        return timeVal;
    }

    public ClassAd getClassadVal() {
        return classadVal;
    }

    public ExprList getListVal() {
        return listVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public static double[] getScalefactor() {
        return ScaleFactor;
    }

}
