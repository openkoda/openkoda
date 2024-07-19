package com.openkoda.repository;

import org.hibernate.query.TypedTupleTransformer;
import org.hibernate.transform.ResultTransformer;

import java.util.LinkedHashMap;

public class AliasToEntityHashMapResultTransformer implements ResultTransformer<LinkedHashMap<String,Object>>, TypedTupleTransformer<LinkedHashMap<String,Object>> {

    public static final AliasToEntityHashMapResultTransformer INSTANCE = new AliasToEntityHashMapResultTransformer();

    /**
     * Disallow instantiation of AliasToEntityMapResultTransformer.
     */
    private AliasToEntityHashMapResultTransformer() {
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Class getTransformedType() {
        return LinkedHashMap.class;
    }

    @Override
    public LinkedHashMap<String,Object> transformTuple(Object[] tuple, String[] aliases) {
        LinkedHashMap<String,Object> result = new LinkedHashMap<>( tuple.length );
        for ( int i = 0; i < tuple.length; i++ ) {
            String alias = aliases[i];
            if ( alias != null ) {
                result.put( alias, tuple[i] );
            }
        }
        return result;
    }

    /**
     * Serialization hook for ensuring singleton uniqueing.
     *
     * @return The singleton instance : {@link #INSTANCE}
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
