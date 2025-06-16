<template>
  <div>
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>


      <a-card :bordered='false' style='margin-bottom: 20px' :body-style='{padding:"17px 24px"}'>
        <a-row>
          <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
            {{ modal.scriptId ? '编辑' : '新建' }}脚本
          </a-col>
          <a-col :span='12' style='text-align: right'>
            <a-space size='small'>
            <span style='display: inline-block;margin-right: 30px;color: #b8b4b4'
                  v-if='modal.updateTime'>最后修改：{{ modal.updateTime }}</span>
            <a-popconfirm title='放弃编辑的内容?' @confirm='() => {onClose()}'>
              <a-button style='width:75px;'> 返回</a-button>
            </a-popconfirm>
            <a-button type='primary' @click='saveScript' style='width:75px'> 保存</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-card>

      <a-row :gutter='24'>

        <a-col :span='15'>
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
                <a-form-model-item label='JavaScript脚本' prop='scriptContent'>
                  <monaco-editor height='470px' :minimap='true' ref='ScriptContentEditor'></monaco-editor>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
        <a-col :span='9'>

          <a-card title='调试' :body-style='{paddingBottom:0}' :bordered='false'>
            <div slot='extra' style='padding: 0'>
              <a-button type='primary' @click='runScript' icon='caret-right' class='runBtn'> 运行
              </a-button>
            </div>

            <variables-model ref='VariablesModel'></variables-model>

            <a-form-model-item label='输入参数（Json）' prop='paramContent' class='inputModel'>
              <monaco-editor height='150px' ref='ParamContentEditor' language='json'></monaco-editor>
            </a-form-model-item>

            <a-form-model-item label='运行结果' prop='resultContent' class='outputModel'>
              <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;color: #000000'>
                <span v-show='time >= 0' style='display: inline-block;padding-right: 15px'>用时：{{ time }}ms</span>
              </div>
              <monaco-editor height='150px' ref='ResultContentEditor' language='json'></monaco-editor>
            </a-form-model-item>

          </a-card>
        </a-col>

      </a-row>
    </a-form-model>
  </div>


</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
//import { formatJson } from '@/utils/util'
import VariablesModel from './VariablesModel'
import MonacoEditor from '@/components/Editor/MonacoEditor'


export default {
  name: 'ResourceModel',
  components: { MonacoEditor, VariablesModel },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {
        scriptContent: '/**\n' +
          '* 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
          '*/\n' +
          'function transform(data) {\n' +
          '    return data;\n' +
          '}',
        paramContent: '{}',
        resultContent: '',
        variables: {}
      },
      url: {
        info: '/api/script/info',
        add: '/api/script/add',
        update: '/api/script/update',
        run: '/api/script/run'
      },
      rules: {
        scriptName: [{ required: true, message: '请输入脚本名称', trigger: 'blur' }],
        scriptContent: [{ required: true, message: '请输入脚本内容', trigger: 'blur' }]
      },
      time: -1
    }
  },
  mounted() {
    let scriptId = this.$route.params.scriptId
    if (scriptId !== 'new') {
      this.scriptId = scriptId
      getAction(this.url.info, { scriptId: this.scriptId }).then(res => {
        this.modal = res.data
        this.setEditorValue()
      })
    } else {
      this.setEditorValue()
    }
  },
  methods: {
    setEditorValue() {
      this.$refs.VariablesModel.set(this.modal.variables)
      this.$refs.ScriptContentEditor.set(this.modal.scriptContent)
      this.$refs.ParamContentEditor.set(this.modal.paramContent)
      this.$refs.ResultContentEditor.set(this.modal.resultContent)
    },
    getEditorValue() {
      this.modal.variables = this.$refs.VariablesModel.get()
      this.modal.scriptContent = this.$refs.ScriptContentEditor.get()
      this.modal.paramContent = this.$refs.ParamContentEditor.get()
      this.modal.resultContent = this.$refs.ResultContentEditor.get()
    },
    runScript() {
      this.getEditorValue()
      postAction(this.url.run, this.modal).then(res => {
        if (res.code === 200) {
          this.modal.resultContent = JSON.stringify(res.data.result)
          this.$refs.VariablesModel.set(res.data.variables)
          this.$refs.ResultContentEditor.set(this.modal.resultContent)
          this.time = res.data.time
          this.$message.success('运行成功')
        } else {
          this.$message.error('运行失败: ' + res.message)
        }
      })
    },
    saveScript() {
      this.getEditorValue()
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
