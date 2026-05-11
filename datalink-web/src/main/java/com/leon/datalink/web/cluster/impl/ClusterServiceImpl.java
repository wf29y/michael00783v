package com.leon.datalink.web.cluster.impl;

import com.leon.datalink.core.cluster.ClusterMember;
import com.leon.datalink.core.cluster.ClusterMemberContent;
import com.leon.datalink.web.cluster.ClusterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Override
    public List<ClusterMember> list(ClusterMember clusterMember) {
        Stream<ClusterMember> stream = ClusterMemberContent.getList().stream();
        if (null != clusterMember) {
            if (null != clusterMember.getMemberName()) {
                stream = stream.filter(r -> r.getMemberName().contains(clusterMember.getMemberName()));
            }
            if (null != clusterMember.getMemberState()) {
                stream = stream.filter(r -> r.getMemberState().equals(clusterMember.getMemberState()));
            }
        }
        return stream.collect(Collectors.toList());
    }

}
