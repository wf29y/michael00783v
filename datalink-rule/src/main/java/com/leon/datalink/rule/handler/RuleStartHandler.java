package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import com.leon.datalink.rule.entity.Rule;

public interface RuleStartHandler {

    void start(Rule rule, ActorContext actorContext);

}
