<template>

  <a-popover :title='resource.resourceName'>
    <template slot='content'>
      <p>资源类型：{{resourceTypeMap[resource.resourceType] }}</p>
      <p v-for='(element,index) in getDetails(resource)' :key='index'>{{ element.name }}：{{ element.value }}</p>
      <p v-show='runtime.time'>最近时间：{{runtime.time}}</p>
      <p>处理成功：{{runtime.successCount}}</p>
      <p>处理失败：{{runtime.failCount}}</p>
      <p v-show='runtime.message'>失败原因：{{runtime.message}}</p>
    </template>
    <div class='node' :class='nodeClass'>
      <a-icon :component='resourceNode' class='icon'></a-icon>
      <span class='name'>{{ resource.resourceName }}</span>
    </div>
  </a-popover>


</template>

<script>
import { resourceNode } from '@/core/icons'
import { resourceTypeMap, getResourceDetails } from '@/config/resource.config'

export default {
  inject: ['getGraph', 'getNode'],
  data() {
    return {
      resourceNode,
      resourceTypeMap: resourceTypeMap,
      resource: {},
      runtime: {}
    }
  },

  methods: {
    mapper(source, target) {
      for (let key in target) {
        target[key] = source?.[key] ?? target[key]
      }
    },
    getDetails(resource) {
      return getResourceDetails(resource, 'rule')
    }
  },

  created() {
    let node = this.getNode()
    // 初始化数据绑定
    this.mapper(node.data, this.$data)
    // 节点数据变化监听，从而绑定数据
    node.on('change:data', ({ current }) => this.mapper(current, this.$data))
    // console.log(this.runtime)
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

<style scoped>

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

.node .name {
  display: inline-block;
  flex-shrink: 0;
  width: 104px;
  margin-left: 8px;
  color: #666;
  font-size: 12px;
}


</style>