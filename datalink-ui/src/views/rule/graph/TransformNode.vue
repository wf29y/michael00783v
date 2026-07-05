<template>
  <a-popover>
    <template slot='title'>
      <div style='padding: 5px 0'>{{transformModeMap[transform.transformMode]}}</div>
    </template>
    <template slot='content'>
      <a-descriptions :column='2' style='width: 500px'>
        <a-descriptions-item label='处理成功'>{{ runtime.successCount }}</a-descriptions-item>
        <a-descriptions-item label='处理失败'>{{runtime.failCount }}</a-descriptions-item>
      </a-descriptions>
      <a-table :columns='dataColumns' :data-source='getObjectValues(runtime.runtimeMemberList)' size='small' :pagination='false'>
            <span slot='status' slot-scope='text,record'>
                        <a-popover title='说明'>
                          <template slot='content'>
                            {{ record.message ? record.message : '—' }}
                          </template>
                            <a-badge v-if='text==="INIT"' status='default' text='未运行' />
                            <a-badge v-if='text==="NORMAL"' color='green' text='正常' />
                            <a-badge v-if='text==="ABNORMAL"' color='red' text='异常' />
                        </a-popover>
            </span>
      </a-table>
    </template>
    <div class='node' :class='nodeClass'>
      <a-icon :component='nodeIcon[transform.transformMode]' class='icon'></a-icon>
      <span class='name'>{{transformModeMap[transform.transformMode]}}</span>
    </div>
  </a-popover>

</template>

<script>
import { pluginNode, scriptNode, withoutNode, sqlNode } from '@/core/icons'
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
        PLUGIN: pluginNode,
        SQL: sqlNode,
      },
      dataColumns: [
        {
          title: '节点',
          dataIndex: 'memberName',
          width: 200
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '成功',
          dataIndex: 'successCount',
          width: 100
        },
        {
          title: '失败',
          dataIndex: 'failCount',
          width: 100
        }
      ]
    }
  },

  methods: {
    mapper(source, target) {
      for (let key in target) {
        target[key] = source?.[key] ?? target[key]
      }
    },
    getObjectValues(object) {
      let values = []
      for (let property in object)
        values.push(object[property])
      return values
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