package com.openkoda.core.flow;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static com.openkoda.core.flow.TestPageAttributes.someMessageString;


/**
 * Created by arek on 2016-01-18.
 */
public class FlowTest {

   Flow testFlow = Flow.init().then(a -> Arrays.asList(1, 2, 3, 4, 5))
           .then(a -> a.result.stream().map( i -> i + 2).reduce(0, Integer::sum))
           .thenSet(someMessageString, a -> "The result is " + a.result)
           .thenSet("otherMessageString", a -> "Agreed. " + a.result);

   @Test
   public void testFlowExecute() {
       PageModelMap result = testFlow.execute();
       Assert.assertEquals("The result is 25", result.get(someMessageString));
   }

   @Test
   public void testStringFlowExecute() {


       PageModelMap result = testFlow.execute();
       Assert.assertEquals("The result is 25", result.get(someMessageString));
       Assert.assertEquals("Agreed. The result is 25", result.get("otherMessageString"));
   }

}
