<template>
  <a-modal
    title='转换SQL'
    :width='800'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :bodyStyle='{padding:0}'
  >

    <div class='scriptView'>
      <monaco-editor ref='MonacoEditor' height='400px' :border='false' language='sql'></monaco-editor>
    </div>

    <template slot='footer'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='处理器数量' style='margin-bottom: 0'>
                <a-input-number v-model='transform.workerNum' placeholder='处理器数量' style='width: 100%' :min='1'/>
              </a-form-item>
            </a-col>
            <a-col :md='18' :sm='24'>
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
          sql: 'SELECT * FROM [资源ID]'
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
    handleCancel() {
      this.init()
      this.visible = false
    }
  }
}
</script>

<style>

</style>
