package com.openkoda.core.flow;

import java.util.List;

public interface TestPageAttributes {

    PageAttr<List<Integer>> someIntegerList = new PageAttr<>("someIntegerList");
    PageAttr<String> someMessageString = new PageAttr<>("someMessageString");
    PageAttr<Integer> someNumber = new PageAttr<>("someNumber");


}
