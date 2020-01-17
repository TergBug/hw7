package org.mycode.mapping;

public interface Mapper<T, S, ID> {
    T map(S source, ID searchId) throws Exception;
}
