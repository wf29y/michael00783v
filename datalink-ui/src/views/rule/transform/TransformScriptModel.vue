<template>
  <a-modal
    title='转换脚本'
    :width='900'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :bodyStyle='{padding:0}'
  >

    <div class='scriptView'>
      <monaco-editor ref='MonacoEditor' height='500px' :border='false' :minimap='true'></monaco-editor>
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
              <a-button key='select' @click='handleSelect'>选择脚本</a-button>
              <a-button key='submit' type='primary' @click='handleOk'>确定</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </template>

    <script-select-model ref='ScriptSelectModel' @select='select'></script-select-model>

  </a-modal>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import ScriptSelectModel from './ScriptSelectModel'

export default {
  name: 'TransformScriptModel',
  components: { MonacoEditor, ScriptSelectModel },
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
        transformMode: 'SCRIPT',
        workerNum: 3,
        properties: {
          script: '/**\n' +
            '* 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
            '*/\n' +
            'function transform(data) {\n' +
            '    return data;\n' +
            '}'
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
        this.$refs.MonacoEditor.set(transform.properties.script)
      })
    },
    handleOk() {
      this.transform.properties.script = this.$refs.MonacoEditor.get()
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
    },
    handleSelect() {
      this.$refs.ScriptSelectModel.show()
    },
    select(record) {
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(record.scriptContent)
      })
    }
  }
}
</script>

<style>

</style>
