<template>
  <a-popover title='统计'>
    <template slot='content'>
      <p v-show='runtime.time'>最近时间：{{runtime.time}}</p>
      <p>处理成功：{{runtime.successCount}}</p>
      <p>处理失败：{{runtime.failCount}}</p>
      <p v-show='runtime.message'>失败原因：{{runtime.message}}</p>
    </template>
    <div class='node' :class='nodeClass'>
      <a-icon :component='nodeIcon[transform.transformMode]' class='icon'></a-icon>
      <span class='name'>{{transformModeMap[transform.transformMode]}}</span>
    </div>
  </a-popover>

</template>

<script>
import { pluginNode, scriptNode, withoutNode } from '@/core/icons'
import { transformModeMap } from '@/config/transform.config'

export default {
  inject: ['getGraph', 'getNode'],
  data() {
    return {
      transformModeMap:transformModeMap,
      pluginNode,
      scriptNode,
      withoutNode,
      runtime:{},
      transform: {},
      nodeIcon: {
        WITHOUT: withoutNode,
        SCRIPT: scriptNode,
        PLUGIN: pluginNode
      }
    }
  },

  methods: {
    mapper(source, target) {
      for (let key in target) {
        target[key] = source?.[key] ?? target[key]
      }
    }
  },

  created() {
    let node = this.getNode()
    // 初始化数据绑定
    this.mapper(node.data, this.$data)
    // 节点数据变化监听，从而绑定数据
    node.on('change:data', ({ current }) => this.mapper(current, this.$data))
  },

  computed: {
    nodeClass: function() {
      let clazz = {}
      if (this.runtime) {
        let status = this.runtime.status.toLowerCase()
        clazz[status] = true
      }
      return clazz
    }
  }
}
</script>

<style>

.node {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #fff;
  border: 1px solid #c2c8d5;
  border-radius: 4px;
  box-shadow: 0 2px 5px 1px rgba(0, 0, 0, 0.06);
}

.node .icon {
  flex-shrink: 0;
  margin-left: 8px;
}


.init {
  border-left: 4px solid #7f7f84;
}

.normal {
  border-left: 4px solid #9ce039;
}

.abnormal {
  border-left: 4px solid #fa2f2f;
}

.node .name{
  display: inline-block;
  flex-shrink: 0;
  width: 104px;
  margin-left: 8px;
  color: #666;
  font-size: 12px;
}


</style>