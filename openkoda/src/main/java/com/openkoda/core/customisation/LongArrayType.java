///*
//MIT License
//
//Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>
//
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
//and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice
//shall be included in all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
//INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
//OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
//IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//*/
//
//package com.openkoda.core.customisation;
//
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
//import org.hibernate.type.ArrayType;
//import org.hibernate.usertype.UserType;
//
//import java.io.Serializable;
//import java.sql.*;
//
///**
// * Custom Hibernate type that allows to make Long[] array mapping to an JPA @Entity field.
// */
//public class LongArrayType implements UserType<ArrayType> {
//
//    private final int arrayType = Types.ARRAY;
//
//    @Override
//    public int getSqlType() {
//        return arrayType;
//    }
//
//    @Override
//    public Class<ArrayType> returnedClass() {
//        return ArrayType.class;
//    }
//
//    @Override
//    public boolean equals(ArrayType x, ArrayType y) {
//        return x == null || y == null ? false : x.equals(y);
//    }
//
//    @Override
//    public int hashCode(ArrayType x) {
//        return x == null ? 0 : x.hashCode();
//    }
//
//    @Override
//    public ArrayType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
//        if (rs != null && rs.getArray(position) != null) {
//            return rs.getArray(position).getArray();
//        }
//        return null;
//    }
//
//    @Override
//    public void nullSafeSet(PreparedStatement st, ArrayType value, int index, SharedSessionContractImplementor session) throws SQLException {
//        // setting the column with string array
//        if (value != null && st != null) {
//            Long[] castObject = (Long[]) value;
//            Array array = session.doReturningWork(connection -> connection.createArrayOf("bigint", castObject));
//            st.setArray(index, array);
//        } else {
//            st.setNull(index, arrayType);
//        }
//    }
//
//    @Override
//    public ArrayType deepCopy(ArrayType value) {
//        return null;
//    }
//
////    @Override
////    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException,
////            SQLException {
////        // setting the column with string array
////        if (value != null && st != null) {
////            Long[] castObject = (Long[]) value;
////            Array array = session.doReturningWork(connection -> connection.createArrayOf("bigint", castObject));
////            st.setArray(index, array);
////        } else {
////            st.setNull(index, arrayType);
////        }
////    }
//
//    @Override
//    public Object deepCopy(Object value) throws HibernateException {
//        return value == null ? null : ((String[]) value).clone();
//    }
//
//    @Override
//    public boolean isMutable() {
//        return false;
//    }
//
//    @Override
//    public Serializable disassemble(ArrayType value) {
//        return (Serializable) value;
//    }
//
//    @Override
//    public Object assemble(Serializable cached, Object owner) throws HibernateException {
//        return cached;
//    }
//
//    @Override
//    public ArrayType replace(ArrayType detached, ArrayType managed, Object owner) {
//        return detached;
//    }
//}