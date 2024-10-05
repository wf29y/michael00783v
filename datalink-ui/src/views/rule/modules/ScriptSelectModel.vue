<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='选择脚本'
    :width='700'
    :visible='visible'
    @cancel='onClose'
  >

    <a-table :columns='columns' :data-source='scriptList' size='small' :pagination='false'>
      <span slot='action' slot-scope='text,record'>
        <a @click='select(record)'>使用</a>
      </span>
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
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
    </div>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'

export default {
  name: 'ScriptSelectModel',
  components: {},
  data() {
    return {
      visible: false,
      confirmLoading: false,
      url: {
        script: '/api/script/list'
      },
      columns: [
      /*  {
          title: 'ID',
          dataIndex: 'scriptId'
        },*/
        {
          title: '名称',
          dataIndex: 'scriptName'
        },
        {
          title: '操作',
          dataIndex: 'action',
          scopedSlots: { customRender: 'action' }
        }
      ],
      scriptList: []
    }
  },
  methods: {
    show() {
      this.getScriptList()
      this.visible = true
    },
    onClose() {
      this.visible = false
    },
    getScriptList() {
      postAction(this.url.script, {}).then(res => {
        if (res.code === 200) {
          this.scriptList = res.data
        }
      })
    },
    select(record) {
      this.$emit('select', record)
      this.visible = false
    }

  }
}
</script>

<style scoped>
</style>
