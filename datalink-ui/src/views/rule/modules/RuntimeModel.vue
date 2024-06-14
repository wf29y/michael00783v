<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='运行状态'
    :width='700'
    :visible='visible'
    @cancel='onClose'
  >

    <a-descriptions :column='2'>
      <a-descriptions-item label='启动运行'>
        {{ runtime.startTime }}
      </a-descriptions-item>
      <a-descriptions-item label='最近处理'>
        {{ runtime.lastTime ? runtime.lastTime : '无' }}
      </a-descriptions-item>
      <a-descriptions-item label='处理成功'>
        {{ runtime.successCount }}
      </a-descriptions-item>
      <a-descriptions-item label='处理失败'>
        {{ runtime.failCount }}
      </a-descriptions-item>
    </a-descriptions>

    <a-table :columns='columns' :data-source='runtime.lastData' size='small' :pagination='false'>
      <span slot='data' slot-scope='text'> {{ text }} </span>
    </a-table>

    <div
      :style="{
        position: 'absolute',
        right: 0,
        bottom: 0,
        width: '100%',
        borderTop: '1px solid #e9e9e9',
        padding: '10px 16px',
        background: '#fff',
        textAlign: 'right',
        zIndex: 1
      }"
    >
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 关闭</a-button>
      <a-button type='primary' @click='getRuntime'> 刷新</a-button>
    </div>
  </a-modal>
</template>

<script>
import { getAction } from '@/api/manage'

export default {
  name: 'RuntimeModel',
  components: {},
  data() {
    return {
      visible: false,
      confirmLoading: false,
      runtime: {},
      url: {
        runtime: '/api/rule/runtime'
      },
      columns: [
        {
          title: '时间',
          dataIndex: 'time'
        },
        {
          title: '数据',
          dataIndex: 'data',
          scopedSlots: { customRender: 'data' }
        }
      ],
      ruleId: null
    }
  },
  methods: {
    show(ruleId) {
      this.runtime = {}
      this.ruleId = ruleId
      this.getRuntime()
      this.visible = true
    },
    onClose() {
      this.visible = false
    },
    getRuntime() {
      if (!this.ruleId) return
      getAction(this.url.runtime, { ruleId: this.ruleId }).then(res => {
        if (res.code === 200) {
          this.runtime = res.data
        }
      })
    }

  }
}
</script>

<style scoped>
</style>
