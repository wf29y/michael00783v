package com.leon.datalink.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.leon.datalink.core.utils.IdUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.ScriptUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.model.RestResult;
import com.leon.datalink.web.model.RestResultUtils;
import com.leon.datalink.web.model.ScriptParamVO;
import com.leon.datalink.web.model.SqlParamVO;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.web.util.ValidatorUtil;
import org.jetlinks.reactor.ql.ReactorQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.script.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RulesController
 * @Description 规则管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    /**
     * 生成与已有id不重复的id
     *
     * @param excludeList 已有id
     * @throws Exception
     */
    @PostMapping("/createId")
    public Object createId(@RequestBody List<String> excludeList) throws Exception {
        JSONObject result = new JSONObject();
        result.put("id", IdUtil.getId(5, excludeList));
        return result;
    }

    /**
     * 查询规则
     *
     * @param ruleId
     */
    @GetMapping("/info")
    public Object getRule(@RequestParam(value = "ruleId") String ruleId) {
        return ruleService.get(ruleId);
    }


    /**
     * 新增规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/add")
    public void addRule(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleName(), rule.getTransformList(), rule.getSourceResourceList(), rule.getDestResourceList());
        ruleService.add(rule);
    }

    /**
     * 查询规则
     *
     * @param rule
     */
    @PostMapping("/list")
    public Object listRule(@RequestBody Rule rule) {
        return ruleService.list(rule);
    }

    /**
     * 移除规则
     *
     * @param rule
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.remove(ruleId);
    }

    /**
     * 更新规则
     *
     * @param rule
     * @throws Exception
     */
    @PutMapping("/update")
    public void updateRule(@RequestBody Rule rule) throws Exception {
        ValidatorUtil.isNotEmpty(rule.getRuleId(), rule.getRuleName(), rule.getTransformList(), rule.getSourceResourceList(), rule.getDestResourceList());
        ruleService.update(rule);
    }

    /**
     * 启动规则
     */
    @PostMapping("/start")
    public void startRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.startRule(ruleId);
    }

    /**
     * 停止规则
     */
    @PostMapping("/stop")
    public void stopRule(@RequestBody Rule rule) throws Exception {
        String ruleId = rule.getRuleId();
        ValidatorUtil.isNotEmpty(ruleId);
        ruleService.stopRule(ruleId);
    }


    /**
     * 测试sql
     */
    @PostMapping("/testSql")
    public void testSql(@RequestBody SqlParamVO sqlParamVO, HttpServletResponse response) throws Exception {
        ValidatorUtil.isNotEmpty(sqlParamVO.getSql(), sqlParamVO.getData());
        ServletOutputStream outputStream = response.getOutputStream();
        ReactorQL reactorQL = ReactorQL.builder().sql(sqlParamVO.getSql()).build();
        long time1 = System.currentTimeMillis();
        reactorQL.start(name -> Flux.just(sqlParamVO.getData()))
                .doOnNext(result -> {
                    try {
                        long time2 = System.currentTimeMillis();
                        Map<String, Object> map = new HashMap<>();
                        map.put("result", result);
                        map.put("time", time2 - time1);
                        RestResult<Map<String, Object>> success = RestResultUtils.success(map);
                        outputStream.write(JacksonUtils.toJsonBytes(success));
                    } catch (IOException e) {
                        Loggers.WEB.error(e.getMessage());
                    }
                }).subscribe();
    }

    /**
     * 运行调试脚本
     */
    @PostMapping("/testScript")
    public Object testScript(@RequestBody ScriptParamVO scriptParamVO) throws Exception {
        String language = scriptParamVO.getLanguage();
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(language);
        long time1 = System.currentTimeMillis();
        // 获取并绑定自定义环境变量
        Bindings bind = scriptEngine.createBindings();

        Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
        for (String key : globalVariable.keySet()) {
            bind.put(key, globalVariable.get(key));
        }

        scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(scriptParamVO.getScript());
        Invocable jsInvoke = (Invocable) scriptEngine;
        Object transform = jsInvoke.invokeFunction("transform", JacksonUtils.toObj(scriptParamVO.getParam(), Object.class));

        long time2 = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        result.put("result", "javascript".equals(language) ? ScriptUtil.toJavaObject(transform) : transform);
        result.put("time", time2 - time1);
        return result;
    }


}

