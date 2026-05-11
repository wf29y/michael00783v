package com.leon.datalink.web.cluster;


import com.leon.datalink.core.cluster.ClusterMember;
import com.leon.datalink.core.listener.Listener;

import java.util.List;


public interface ClusterService {

    List<ClusterMember> list(ClusterMember clusterMember);

}
