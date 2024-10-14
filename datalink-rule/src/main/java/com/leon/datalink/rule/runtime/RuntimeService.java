package com.leon.datalink.rule.runtime;

import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.entity.Runtime;
import com.leon.datalink.rule.runtime.actor.RuntimeUpdateMsg;

public interface RuntimeService {

   void init(Rule rule);

   void update(String ruleId, RuntimeUpdateMsg runtimeUpdateMsg);

   Runtime get(String ruleId);

   void remove(String ruleId);
}
