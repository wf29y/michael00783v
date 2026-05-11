package com.leon.datalink.core.cluster;

import akka.actor.Address;
import akka.cluster.Member;
import cn.hutool.core.date.DateTime;
import com.leon.datalink.core.listener.Listener;
import com.leon.datalink.core.listener.ListenerTypeEnum;

import java.util.*;

/**
 * 集群成员
 */
public class ClusterMemberContent {

    private static final Map<String, ClusterMember> members = new HashMap<>();

    public static void up(Member member) {
        String name = getName(member);
        ClusterMember clusterMember = new ClusterMember();
        clusterMember.setMemberName(name);
        clusterMember.setMemberState(ClusterMemberStateEnum.UP);
        clusterMember.setUpdateTime(DateTime.now().toString());
        members.put(name, clusterMember);
    }

    public static void down(Member member) {
        String name = getName(member);
        ClusterMember clusterMember = members.get(name);
        if (null == clusterMember) return;
        clusterMember.setMemberState(ClusterMemberStateEnum.DOWN);
        clusterMember.setUpdateTime(DateTime.now().toString());
    }

    public static List<ClusterMember> getList() {
        Collection<ClusterMember> values = members.values();
        return new ArrayList<>(values);
    }

    private static String getName(Member member) {
        return member.address().toString().replace("akka://", "");
    }


}
