<template>
  <a-modal
    :title='scriptLanguageName + "脚本"'
    :width='900'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :dialog-style="{ top: '20px' }"
    :bodyStyle='{paddingBottom:0}'
  >

    <a-form-model ref='scriptForm' layout='vertical'>
      <a-row :gutter='24'>
        <a-col :span='24'>
          <a-form-model-item label='脚本'>
            <monaco-editor ref='MonacoEditor' height='400px' :minimap='true' :auto-init='false'></monaco-editor>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='模拟数据（Json）'>
            <monaco-editor ref='MonacoEditorParam' height='150px' language='json'></monaco-editor>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='输出结果'>
            <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;color: #000000'>
              <span v-show='time >= 0' style='display: inline-block;padding-right: 15px'>用时：{{ time }}ms</span>
            </div>
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
              <a-button key='select' @click='handleSelect'>选择脚本</a-button>
              <a-button key='test' @click='handleTest'>执行脚本</a-button>
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
import { scriptLanguageMap } from '@/config/language.config'
import { postAction } from '@/api/manage'

export default {
  name: 'TransformScriptModel',
  components: { MonacoEditor, ScriptSelectModel },
  data() {
    return {
      visible: false,
      transformIndex: -1,
      transform: {},
      time: -1,
      scriptLanguageMap: scriptLanguageMap
    }
  },
  mounted() {
    this.init('', '')
  },
  computed: {
    scriptLanguageName() {
      return this.transform.properties && this.transform.properties.language ? this.scriptLanguageMap[this.transform.properties.language].name : ''
    }
  },
  methods: {
    init(language, script) {
      this.transformIndex = -1
      this.transform = {
        transformMode: 'SCRIPT',
        workerNum: 1,
        properties: {
          language: language,
          script: script
        }
      }
    },
    add(language) {
      this.init(language, this.scriptLanguageMap[language].default)
      this.edit(this.transform, -1)
    },
    edit(transform, index) {
      this.visible = true
      this.transform = JSON.parse(JSON.stringify(transform))
      this.transformIndex = index
      this.$nextTick(() => {
        this.$refs.MonacoEditor.init(transform.properties.script, this.scriptLanguageMap[transform.properties.language].editor)
        this.$refs.MonacoEditorParam.set('{}')
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
      this.init('', '')
      this.visible = false
    },
    handleSelect() {
      this.$refs.ScriptSelectModel.show(this.transform.properties.language)
    },
    select(record) {
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(record.scriptContent)
      })
    },
    handleTest() {
      let script = this.$refs.MonacoEditor.get()
      let param = this.$refs.MonacoEditorParam.get()
      if (!script || !param) {
        this.$message.error('请输入要执行的脚本和模拟数据')
        return
      }
      postAction('/api/rule/testScript', {
        language: this.transform.properties.language,
        script: script,
        param: param
      }).then((res) => {
        if (res.code === 200) {
          this.$nextTick(() => {
            this.$refs.MonacoEditorResult.set(JSON.stringify(res.data.result))
            this.$refs.MonacoEditorResult.format()
            this.time = res.data.time
          })
        } else {
          this.$message.error(res.message)
        }
      })
    }
  }
}
</script>

<style>

</style>
