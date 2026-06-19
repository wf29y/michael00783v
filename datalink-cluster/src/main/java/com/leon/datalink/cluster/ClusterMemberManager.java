package com.leon.datalink.cluster;

import akka.cluster.Member;
import cn.hutool.core.date.DateTime;
import com.leon.datalink.cluster.constants.ClusterMemberStateEnum;

import java.util.*;

/**
 * 集群成员
 */
public class ClusterMemberManager {

    private static final Map<String, ClusterMember> members = new HashMap<>();

    private static String localMemberName;

    public static void up(Member member) {
        String name = getMemberName(member);
        ClusterMember clusterMember = new ClusterMember();
        clusterMember.setMemberName(name);
        clusterMember.setMemberState(ClusterMemberStateEnum.UP);
        clusterMember.setUpdateTime(DateTime.now().toString());
        clusterMember.setLocal(name.equals(localMemberName));
        members.put(name, clusterMember);
    }

    public static void down(Member member) {
        String name = getMemberName(member);
        ClusterMember clusterMember = members.get(name);
        if (null == clusterMember) return;
        clusterMember.setMemberState(ClusterMemberStateEnum.DOWN);
        clusterMember.setUpdateTime(DateTime.now().toString());
    }

    public static List<ClusterMember> getList() {
        Collection<ClusterMember> values = members.values();
        return new ArrayList<>(values);
    }

    public static String getMemberName(Member member) {
        return member.address().toString().replace("akka://", "");
    }

    public static String getLocalMemberName() {
        return localMemberName;
    }

    public static void setLocalMemberName(String localMemberName) {
        ClusterMemberManager.localMemberName = localMemberName;
    }
}
