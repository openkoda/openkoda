/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.helper;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * <p>Cluster helper provides a set of static methods for getting application cluster node information.</p>
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Component("cluster")
public class ClusterHelper implements LoggingComponentWithRequestId {

    public static final String CLUSTER_EVENT_TOPIC = "clusterEvent";

    private static ClusterHelper instance;

    @Value("${hazelcast.members.commaSeparated:127.0.0.1}")
    private String hazelcastMembersCommaSeparated;
    @Value("${master.node:127.0.0.1:8080}")
    private String masterNode;

    private String thisNode;

    private boolean isMaster;

    private String[] hazelcastMembers;

    @Autowired(required = false)
    private HazelcastInstance hazelcastInstance;

    public static boolean isCluster() {
        return instance.hazelcastInstance != null;
    }

    @PostConstruct
    private void init() {
        instance = this;
        thisNode = detectThisNode();
        isMaster =  StringUtils.equals(masterNode, thisNode);
        hazelcastMembers = hazelcastMembersCommaSeparated.split(",");
    }

    /**
     * @return true if current instance is a master node
     */
    public static boolean isMaster() {
        return instance.isMaster;
    }

    public static boolean isSingleNodeCluster() {
        return isCluster() ? instance.hazelcastMembers.length == 1 : true;
    }


    /**
     * @return members nodes separated by comma
     */
    public static String getMembersCommaSeparated() {
        return instance.hazelcastMembersCommaSeparated;
    }

    /**
     * @return master node
     */
    public static String getMasterNode() {
        return instance.masterNode;
    }

    /**
     * @return this node
     */
    public static String getThisNode() {
        return instance.thisNode;
    }

    private static String detectThisNode() {
        if (!isCluster()) { return instance.masterNode; }
        Member m = instance.hazelcastInstance.getCluster().getLocalMember();
        return m.getSocketAddress().getHostString() + ":" + m.getAttribute("server.port");
    }

    /**
     * @return array of node members
     */
    public static String[] getMembers() {
        return instance.hazelcastInstance.getCluster().getMembers().stream().map(
                m -> m.getSocketAddress().getHostString() + ":" + m.getAttribute("server.port")).toArray(String[]::new);
    }

    public static HazelcastInstance getHazelcastInstance() {
        return instance.hazelcastInstance;
    }
}
