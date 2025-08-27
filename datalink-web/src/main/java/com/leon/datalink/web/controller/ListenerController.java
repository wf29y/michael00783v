package com.leon.datalink.web.controller;

import com.leon.datalink.core.listener.Listener;
import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.web.listener.ListenerService;
import com.leon.datalink.web.util.ValidatorUtil;
import com.leon.datalink.web.variable.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

/**
 * @ClassName ListenerController
 * @Description 监听器
 * @Author Leon
 * @Date 2022年9月3日13:40:00
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/listener")
public class ListenerController {

    @Autowired
    private ListenerService listenerService;

    /**
     * 查询
     */
    @PostMapping("/list")
    public Object list(@RequestBody Listener listener) {
        return listenerService.list(listener);
    }

}

