<template>
  <div>
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>

      <a-card :bordered='false' style='margin-bottom: 20px' :body-style='{padding:"17px 24px"}'>
        <a-row>
          <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
            {{ modal.scriptId ? '编辑' : '新建' }}{{ scriptLanguageName }}脚本
          </a-col>
          <a-col :span='12' style='text-align: right'>
            <a-space size='small'>
              <!--              <a-popconfirm title='放弃未保存的内容?' @confirm='() => {onClose()}' placement='bottom'>
                              <a-button style='width:75px;'> 返回</a-button>
                            </a-popconfirm>-->
              <a-button style='width:75px;' @click='onClose'> 返回</a-button>
              <a-button type='primary' @click='saveScript' style='width:75px'> 保存</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-card>

      <a-row :gutter='24'>

        <a-col :span='16'>
          <a-card :bordered='false' :body-style='{paddingBottom: 0}'>
            <a-row :gutter='24'>
              <a-col :span='12'>
                <a-form-model-item label='名称' prop='scriptName'>
                  <a-input v-model='modal.scriptName' placeholder='请输入脚本名称'></a-input>
                </a-form-model-item>
              </a-col>
              <a-col :span='12'>
                <a-form-model-item label='说明' prop='description'>
                  <a-input v-model='modal.description' placeholder='请输入脚本说明'></a-input>
                </a-form-model-item>
              </a-col>
              <a-col :span='24' class='scriptModel'>
                <a-form-model-item :label='scriptLanguageName+"脚本"' prop='scriptContent'>
                  <monaco-editor height='600px' :minimap='true' ref='ScriptContentEditor'
                                 :auto-init='false'></monaco-editor>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
        <a-col :span='8'>

          <a-card title='调试' :body-style='{paddingBottom:0}' :bordered='false'>
            <span slot='extra'>
              <a-button type='primary' @click='runScript' icon='caret-right' class='runBtn'> 运行
              </a-button>
            </span>

            <a-form-model-item label='输入参数（Json）' prop='paramContent' class='inputModel'>
              <monaco-editor height='285px' ref='ParamContentEditor' language='json'></monaco-editor>
            </a-form-model-item>

            <a-form-model-item label='运行结果' prop='resultContent' class='outputModel'>
              <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;color: #000000'>
                <span v-show='time >= 0' style='display: inline-block;padding-right: 15px'>用时：{{ time }}ms</span>
              </div>
              <monaco-editor height='285px' ref='ResultContentEditor' language='json'></monaco-editor>
            </a-form-model-item>
          </a-card>
        </a-col>

      </a-row>
    </a-form-model>
  </div>


</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { scriptLanguageMap } from '@/config/language.config'

export default {
  name: 'ScriptModel',
  components: { MonacoEditor },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {
        scriptContent: '',
        scriptLanguage: ''
      },
      url: {
        info: '/api/script/info',
        add: '/api/script/add',
        update: '/api/script/update',
        testScript: '/api/rule/testScript'
      },
      rules: {
        scriptName: [{ required: true, message: '请输入脚本名称', trigger: 'blur' }],
        scriptLanguage: [{ required: true, message: '请选择脚本语言', trigger: 'change' }],
        scriptContent: [{ required: true, message: '请输入脚本内容', trigger: 'blur' }]
      },
      time: -1,
      scriptLanguageMap: scriptLanguageMap
    }
  },
  computed: {
    scriptLanguageName() {
      return this.modal.scriptLanguage ? this.scriptLanguageMap[this.modal.scriptLanguage].name : ''
    }
  },
  mounted() {
    let scriptId = this.$route.params.scriptId
    if (scriptId.startsWith('new')) {
      let scriptIds = scriptId.split('-')
      this.modal.scriptLanguage = scriptIds[1]
      this.modal.scriptContent = this.scriptLanguageMap[this.modal.scriptLanguage].default
      this.initEditorValue()
    } else {
      this.scriptId = scriptId
      getAction(this.url.info, { scriptId: this.scriptId }).then(res => {
        this.modal = res.data
        this.initEditorValue()
      })
    }
  },
  methods: {
    initEditorValue() {
      this.$nextTick(() => {
        this.$refs.ScriptContentEditor.init(this.modal.scriptContent, this.scriptLanguageMap[this.modal.scriptLanguage].editor)
        this.$refs.ParamContentEditor.set('{}')
        this.$refs.ResultContentEditor.set('')
      })
    },
    runScript() {
      let param = {
        language: this.modal.scriptLanguage,
        script: this.$refs.ScriptContentEditor.get(),
        param: this.$refs.ParamContentEditor.get()
      }
      postAction(this.url.testScript, param).then(res => {
        if (res.code === 200) {
          this.$refs.ResultContentEditor.set(JSON.stringify(res.data.result))
          this.$refs.ResultContentEditor.format()
          this.time = res.data.time
          this.$message.success('运行成功')
        } else {
          this.$message.error('运行失败: ' + res.message)
        }
      })
    },
    saveScript() {
      this.modal.scriptContent = this.$refs.ScriptContentEditor.get()
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let script = JSON.parse(JSON.stringify(this.modal))
          let obj
          if (this.scriptId) {
            obj = putAction(this.url.update, script)
          } else {
            obj = postAction(this.url.add, script)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.modal = res.data
            } else {
              that.$message.warning('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
            })

        } else {
          return false
        }
      })
    },
    onClose() {
      this.$router.push({ name: 'scriptList' })
    }
  }
}
</script>

<style>

</style>
