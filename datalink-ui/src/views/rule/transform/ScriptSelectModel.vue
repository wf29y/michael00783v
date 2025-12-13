<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='脚本'
    :width='700'
    :visible='visible'
    @cancel='onClose'
    :footer='null'
    :bodyStyle='{padding: 0}'
  >
    <div style='min-height: 400px'>

      <a-table :columns='columns' :data-source='scriptList' size='small' :pagination='false'>
          <span slot='action' slot-scope='text,record'>
            <a @click='select(record)'>选择</a>
          </span>
      </a-table>

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
        {
          title: '名称',
          align:'center',
          dataIndex: 'scriptName',
        },
        {
          title: '最后修改',
          align:'center',
          dataIndex: 'updateTime',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align:'center',
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
