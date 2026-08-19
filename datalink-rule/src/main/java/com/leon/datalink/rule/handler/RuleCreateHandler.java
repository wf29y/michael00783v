package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import com.leon.datalink.rule.entity.Rule;

public interface RuleCreateHandler {

    void create(Rule rule, ActorContext actorContext);

    void destroy();

}
