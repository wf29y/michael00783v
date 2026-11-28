<template>
  <a-modal
    title='转换SQL'
    :width='800'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :bodyStyle='{paddingBottom:0}'
  >
    <a-form-model ref='sqlForm' layout='vertical'>
      <a-row :gutter='24'>
        <a-col :span='24'>
          <a-form-model-item label='SQL语句'>
            <monaco-editor ref='MonacoEditor' height='200px' language='sql'></monaco-editor>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='模拟数据（Json）'>
            <monaco-editor ref='MonacoEditorData' height='150px' language='json'></monaco-editor>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='输出结果'>
            <monaco-editor ref='MonacoEditorResult' height='150px' language='json'></monaco-editor>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>

    <template slot='footer'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='处理器数量' style='margin-bottom: 0'>
                <a-input-number v-model='transform.workerNum' placeholder='处理器数量' style='width: 100%' :min='1' />
              </a-form-item>
            </a-col>
            <a-col :md='18' :sm='24'>
              <a-button key='test' @click='handleTest'>执行SQL</a-button>
              <a-button key='submit' type='primary' @click='handleOk'>确定</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </template>

  </a-modal>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { postAction } from '@/api/manage'

export default {
  name: 'TransformSqlModel',
  components: { MonacoEditor },
  data() {
    return {
      visible: false,
      transformIndex: -1,
      transform: {}
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.transformIndex = -1
      this.transform = {
        transformMode: 'SQL',
        workerNum: 1,
        properties: {
          sql: 'SELECT * FROM 资源ID'
        }
      }
    },
    add() {
      this.init()
      this.edit(this.transform, -1)
    },
    edit(transform, index) {
      this.visible = true
      this.transform = JSON.parse(JSON.stringify(transform))
      this.transformIndex = index
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(transform.properties.sql)
      })
      this.$nextTick(() => {
        this.$refs.MonacoEditorData.set('{}')
      })
    },
    handleOk() {
      this.transform.properties.sql = this.$refs.MonacoEditor.get()
      if (this.transformIndex >= 0) {
        this.$emit('update', this.transform, this.transformIndex)
      } else {
        this.$emit('add', this.transform)
      }
      this.visible = false
    },
    handleTest() {
      let sql = this.$refs.MonacoEditor.get()
      let data = this.$refs.MonacoEditorData.get()
      if (!sql || !data) {
        this.$message.error('请输入要执行的SQL和模拟数据')
        return
      }
      postAction('/api/rule/testSql', { sql: sql, data: JSON.parse(data) }).then((res) => {
        if (res.code === 200) {
          this.$nextTick(() => {
            this.$refs.MonacoEditorResult.set(JSON.stringify(res.data))
          })
        }else {
          this.$message.error(res.message)
        }
      })
    },
    handleCancel() {
      this.init()
      this.visible = false
    }
  }
}
</script>

<style>

</style>
